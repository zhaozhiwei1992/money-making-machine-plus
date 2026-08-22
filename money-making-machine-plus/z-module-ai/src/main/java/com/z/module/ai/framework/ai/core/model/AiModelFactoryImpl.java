package com.z.module.ai.framework.ai.core.model;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.z.module.ai.enums.model.AiPlatformEnum;
import com.z.module.ai.framework.ai.config.YudaoAiProperties;
import com.z.module.ai.framework.ai.core.model.midjourney.api.MidjourneyApi;
import com.z.module.ai.framework.ai.core.model.suno.api.SunoApi;
import io.micrometer.observation.ObservationRegistry;
import io.milvus.client.MilvusServiceClient;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.SneakyThrows;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.openai.api.common.OpenAiApiConstants;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusServiceClientConnectionDetails;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusServiceClientProperties;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreProperties;
import org.springframework.ai.vectorstore.observation.DefaultVectorStoreObservationConvention;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.vectorstore.qdrant.autoconfigure.QdrantVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.qdrant.autoconfigure.QdrantVectorStoreProperties;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import redis.clients.jedis.JedisPooled;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.z.framework.common.util.collection.CollectionUtils.convertList;

/**
 * AI Model 模型工厂的实现类
 * <p>
 * 已收敛为「OpenAI 兼容格式 + litellm 中转」单一路由：
 * 所有平台（含历史平台如 TONG_YI/ANTHROPIC 等）统一构建 OpenAI 兼容客户端，
 * 由 litellm 按 DB 中的模型名路由到真实模型，不再维护各厂商私有适配。
 *
 * @author 芋道源码
 */
public class AiModelFactoryImpl implements AiModelFactory {

    @Override
    public ChatModel getOrCreateChatModel(AiPlatformEnum platform, String apiKey, String url) {
        String cacheKey = buildClientCacheKey(ChatModel.class, platform, apiKey, url);
        return Singleton.get(cacheKey, (Func0<ChatModel>) () -> buildOpenAiChatModel(apiKey, url));
    }

    @Override
    public ChatModel getDefaultChatModel(AiPlatformEnum platform) {
        // 收敛后仅保留 OpenAI 兼容组件，历史平台（TONG_YI/ANTHROPIC 等）统一使用 openai 配置
        return SpringUtil.getBean(OpenAiChatModel.class);
    }

    @Override
    public ImageModel getDefaultImageModel(AiPlatformEnum platform) {
        return SpringUtil.getBean(OpenAiImageModel.class);
    }

    @Override
    public ImageModel getOrCreateImageModel(AiPlatformEnum platform, String apiKey, String url) {
        return buildOpenAiImageModel(apiKey, url);
    }

    @Override
    public MidjourneyApi getOrCreateMidjourneyApi(String apiKey, String url) {
        String cacheKey = buildClientCacheKey(MidjourneyApi.class, AiPlatformEnum.MIDJOURNEY.getPlatform(), apiKey,
                url);
        return Singleton.get(cacheKey, (Func0<MidjourneyApi>) () -> {
            YudaoAiProperties.Midjourney properties = SpringUtil.getBean(YudaoAiProperties.class)
                    .getMidjourney();
            return new MidjourneyApi(url, apiKey, properties.getNotifyUrl());
        });
    }

    @Override
    public SunoApi getOrCreateSunoApi(String apiKey, String url) {
        String cacheKey = buildClientCacheKey(SunoApi.class, AiPlatformEnum.SUNO.getPlatform(), apiKey, url);
        return Singleton.get(cacheKey, (Func0<SunoApi>) () -> new SunoApi(url));
    }

    @Override
    public EmbeddingModel getOrCreateEmbeddingModel(AiPlatformEnum platform, String apiKey, String url, String model) {
        String cacheKey = buildClientCacheKey(EmbeddingModel.class, platform, apiKey, url, model);
        return Singleton.get(cacheKey, (Func0<EmbeddingModel>) () -> buildOpenAiEmbeddingModel(apiKey, url, model));
    }

    @Override
    public VectorStore getOrCreateVectorStore(Class<? extends VectorStore> type,
                                              EmbeddingModel embeddingModel,
                                              Map<String, Class<?>> metadataFields) {
        String cacheKey = buildClientCacheKey(VectorStore.class, embeddingModel, type);
        return Singleton.get(cacheKey, (Func0<VectorStore>) () -> {
            if (type == SimpleVectorStore.class) {
                return buildSimpleVectorStore(embeddingModel);
            }
            if (type == QdrantVectorStore.class) {
                return buildQdrantVectorStore(embeddingModel);
            }
            if (type == RedisVectorStore.class) {
                return buildRedisVectorStore(embeddingModel, metadataFields);
            }
            if (type == MilvusVectorStore.class) {
                return buildMilvusVectorStore(embeddingModel);
            }
            throw new IllegalArgumentException(StrUtil.format("未知类型({})", type));
        });
    }

    private static String buildClientCacheKey(Class<?> clazz, Object... params) {
        if (ArrayUtil.isEmpty(params)) {
            return clazz.getName();
        }
        return StrUtil.format("{}#{}", clazz.getName(), ArrayUtil.join(params, "_"));
    }

    // ========== OpenAI 兼容客户端（litellm 单路由） ==========

    private static OpenAiChatModel buildOpenAiChatModel(String openAiToken, String url) {
        url = StrUtil.blankToDefault(url, OpenAiApiConstants.DEFAULT_BASE_URL);
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(url).apiKey(openAiToken).build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .toolCallingManager(getToolCallingManager())
                .build();
    }

    private OpenAiImageModel buildOpenAiImageModel(String openAiToken, String url) {
        url = StrUtil.blankToDefault(url, OpenAiApiConstants.DEFAULT_BASE_URL);
        OpenAiImageApi openAiApi = OpenAiImageApi.builder().baseUrl(url).apiKey(openAiToken).build();
        return new OpenAiImageModel(openAiApi);
    }

    private OpenAiEmbeddingModel buildOpenAiEmbeddingModel(String openAiToken, String url, String model) {
        url = StrUtil.blankToDefault(url, OpenAiApiConstants.DEFAULT_BASE_URL);
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(url).apiKey(openAiToken).build();
        return new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED, OpenAiEmbeddingOptions.builder().model(model).build());
    }

    // ========== Vector Store ==========

    private SimpleVectorStore buildSimpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        // 启动加载
        File file = new File(StrUtil.format("{}/vector_store/simple_{}.json",
                FileUtil.getUserHomePath(), embeddingModel.getClass().getSimpleName()));
        if (!file.exists()) {
            FileUtil.mkParentDirs(file);
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("无法创建向量存储文件: " + file.getAbsolutePath(), e);
            }
        } else if (file.length() > 0) {
            vectorStore.load(file);
        }
        // 定时持久化，每分钟一次
        Timer timer = new Timer("SimpleVectorStoreTimer-" + file.getAbsolutePath());
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                vectorStore.save(file);
            }

        }, Duration.ofMinutes(1).toMillis(), Duration.ofMinutes(1).toMillis());
        // 关闭时，进行持久化
        RuntimeUtil.addShutdownHook(() -> vectorStore.save(file));
        return vectorStore;
    }

    /**
     * 参考 {@link QdrantVectorStoreAutoConfiguration} 的 vectorStore 方法
     */
    @SneakyThrows
    private QdrantVectorStore buildQdrantVectorStore(EmbeddingModel embeddingModel) {
        QdrantVectorStoreAutoConfiguration configuration = new QdrantVectorStoreAutoConfiguration();
        QdrantVectorStoreProperties properties = SpringUtil.getBean(QdrantVectorStoreProperties.class);
        // 参考 QdrantVectorStoreAutoConfiguration 实现，创建 QdrantClient 对象
        QdrantGrpcClient.Builder grpcClientBuilder = QdrantGrpcClient.newBuilder(
                properties.getHost(), properties.getPort(), properties.isUseTls());
        if (StrUtil.isNotEmpty(properties.getApiKey())) {
            grpcClientBuilder.withApiKey(properties.getApiKey());
        }
        QdrantClient qdrantClient = new QdrantClient(grpcClientBuilder.build());
        // 创建 QdrantVectorStore 对象
        QdrantVectorStore vectorStore = configuration.vectorStore(embeddingModel, properties, qdrantClient,
                getObservationRegistry(), getCustomObservationConvention(), getBatchingStrategy());
        // 初始化索引
        vectorStore.afterPropertiesSet();
        return vectorStore;
    }

    /**
     * 参考 {@link RedisVectorStoreAutoConfiguration} 的 vectorStore 方法
     */
    private RedisVectorStore buildRedisVectorStore(EmbeddingModel embeddingModel,
                                                   Map<String, Class<?>> metadataFields) {
        // 创建 JedisPooled 对象
        RedisProperties redisProperties = SpringUtil.getBean(RedisProperties.class);
        JedisPooled jedisPooled = new JedisPooled(redisProperties.getHost(), redisProperties.getPort(),
                redisProperties.getUsername(), redisProperties.getPassword());
        // 创建 RedisVectorStoreProperties 对象
        RedisVectorStoreProperties properties = SpringUtil.getBean(RedisVectorStoreProperties.class);
        RedisVectorStore redisVectorStore = RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName(properties.getIndexName()).prefix(properties.getPrefix())
                .initializeSchema(properties.isInitializeSchema())
                .metadataFields(convertList(metadataFields.entrySet(), entry -> {
                    String fieldName = entry.getKey();
                    Class<?> fieldType = entry.getValue();
                    if (Number.class.isAssignableFrom(fieldType)) {
                        return RedisVectorStore.MetadataField.numeric(fieldName);
                    }
                    if (Boolean.class.isAssignableFrom(fieldType)) {
                        return RedisVectorStore.MetadataField.tag(fieldName);
                    }
                    return RedisVectorStore.MetadataField.text(fieldName);
                }))
                .observationRegistry(getObservationRegistry().getObject())
                .customObservationConvention(getCustomObservationConvention().getObject())
                .batchingStrategy(getBatchingStrategy())
                .build();
        // 初始化索引
        redisVectorStore.afterPropertiesSet();
        return redisVectorStore;
    }

    /**
     * 参考 {@link MilvusVectorStoreAutoConfiguration} 的 vectorStore 方法
     */
    @SneakyThrows
    private MilvusVectorStore buildMilvusVectorStore(EmbeddingModel embeddingModel) {
        MilvusVectorStoreAutoConfiguration configuration = new MilvusVectorStoreAutoConfiguration();
        // 获取配置属性
        MilvusVectorStoreProperties serverProperties = SpringUtil.getBean(MilvusVectorStoreProperties.class);
        MilvusServiceClientProperties clientProperties = SpringUtil.getBean(MilvusServiceClientProperties.class);

        // 创建 MilvusServiceClient 对象
        MilvusServiceClient milvusClient = configuration.milvusClient(serverProperties, clientProperties,
                new MilvusServiceClientConnectionDetails() {

                    @Override
                    public String getHost() {
                        return clientProperties.getHost();
                    }

                    @Override
                    public int getPort() {
                        return clientProperties.getPort();
                    }

                }
        );
        // 创建 MilvusVectorStore 对象
        MilvusVectorStore vectorStore = configuration.vectorStore(milvusClient, embeddingModel, serverProperties,
                getBatchingStrategy(), getObservationRegistry(), getCustomObservationConvention());

        // 初始化索引
        vectorStore.afterPropertiesSet();
        return vectorStore;
    }

    private static ObjectProvider<ObservationRegistry> getObservationRegistry() {
        return new ObjectProvider<>() {

            @Override
            public ObservationRegistry getObject(Object... args) throws BeansException {
                return null;
            }

            @Override
            public ObservationRegistry getIfAvailable() throws BeansException {
                return null;
            }

            @Override
            public ObservationRegistry getIfUnique() throws BeansException {
                return null;
            }

            @Override
            public ObservationRegistry getObject() throws BeansException {
                return SpringUtil.getBean(ObservationRegistry.class);
            }

        };
    }

    private static ObjectProvider<VectorStoreObservationConvention> getCustomObservationConvention() {
        return new ObjectProvider<>() {

            @Override
            public VectorStoreObservationConvention getObject(Object... args) throws BeansException {
                return null;
            }

            @Override
            public VectorStoreObservationConvention getIfAvailable() throws BeansException {
                return null;
            }

            @Override
            public VectorStoreObservationConvention getIfUnique() throws BeansException {
                return null;
            }

            @Override
            public VectorStoreObservationConvention getObject() throws BeansException {
                return new DefaultVectorStoreObservationConvention();
            }

        };
    }

    private static BatchingStrategy getBatchingStrategy() {
        return SpringUtil.getBean(BatchingStrategy.class);
    }

    private static ToolCallingManager getToolCallingManager() {
        return SpringUtil.getBean(ToolCallingManager.class);
    }

    private static ObjectProvider<EmbeddingModelObservationConvention> getEmbeddingModelObservationConvention() {
        return new ObjectProvider<>() {

            @Override
            public EmbeddingModelObservationConvention getObject(Object... args) throws BeansException {
                return null;
            }

            @Override
            public EmbeddingModelObservationConvention getIfAvailable() throws BeansException {
                return null;
            }

            @Override
            public EmbeddingModelObservationConvention getIfUnique() throws BeansException {
                return null;
            }

            @Override
            public EmbeddingModelObservationConvention getObject() throws BeansException {
                return SpringUtil.getBean(EmbeddingModelObservationConvention.class);
            }

        };
    }

}