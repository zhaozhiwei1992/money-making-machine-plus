package com.z.module.ai.service.model;

import com.agentsflex.llm.ollama.OllamaLlm;
import com.agentsflex.llm.ollama.OllamaLlmConfig;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import com.z.framework.common.enums.CommonStatusEnum;
import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.model.AiApiKeyDO;
import com.z.module.ai.domain.model.AiModelDO;
import com.z.module.ai.enums.model.AiPlatformEnum;
import com.z.module.ai.framework.ai.core.model.AiModelFactory;
import com.z.module.ai.framework.ai.core.model.midjourney.api.MidjourneyApi;
import com.z.module.ai.framework.ai.core.model.suno.api.SunoApi;
import com.z.module.ai.repository.AiModelRepository;
import com.z.module.ai.web.rest.admin.model.vo.model.AiModelSaveReqVO;
import dev.tinyflow.core.Tinyflow;
import groovy.util.logging.Slf4j;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.z.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.z.module.ai.enums.ErrorCodeConstants.*;

/**
 * AI 模型 Service 实现类
 *
 * @author fansili
 * @since 2024/4/24 19:42
 */
@lombok.extern.slf4j.Slf4j
@Service
@Validated
@Slf4j
public class AiModelService {

    @Resource
    private AiApiKeyService apiKeyService;

    @Resource
    private AiModelRepository modelRepository;

    @Resource
    private AiModelFactory modelFactory;

    /**
     * 创建模型
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    public Long createModel(@Valid AiModelSaveReqVO createReqVO) {
        // 1. 校验
        AiPlatformEnum.validatePlatform(createReqVO.getPlatform());
        apiKeyService.validateApiKey(createReqVO.getKeyId());

        // 2. 插入
        AiModelDO model = BeanUtils.toBean(createReqVO, AiModelDO.class);
        modelRepository.save(model);
        return model.getId();
    }

    /**
     * 更新模型
     *
     * @param updateReqVO 更新信息
     */
    public void updateModel(@Valid AiModelSaveReqVO updateReqVO) {
        // 1. 校验
        validateModelExists(updateReqVO.getId());
        AiPlatformEnum.validatePlatform(updateReqVO.getPlatform());
        apiKeyService.validateApiKey(updateReqVO.getKeyId());

        // 2. 更新
        AiModelDO updateObj = BeanUtils.toBean(updateReqVO, AiModelDO.class);
        modelRepository.save(updateObj);
    }

    /**
     * 删除模型
     *
     * @param id 编号
     */
    public void deleteModel(Long id) {
        // 校验存在
        validateModelExists(id);
        // 删除
        modelRepository.deleteById(id);
    }

    private AiModelDO validateModelExists(Long id) {
        AiModelDO model = modelRepository.findById(id).orElse(null);
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
        return model;
    }

    /**
     * 获得模型
     *
     * @param id 编号
     * @return 模型
     */
    public AiModelDO getModel(Long id) {
        return modelRepository.findById(id).orElse(null);
    }

    /**
     * 获得默认的模型
     *
     * 如果获取不到，则抛出 {@link cn.iocoder.yudao.framework.common.exception.ServiceException} 业务异常
     *
     * @return 模型
     */
    public AiModelDO getRequiredDefaultModel(Integer type) {
        AiModelDO model = modelRepository.findFirstByStatusAndTypeOrderBySortAsc(
                CommonStatusEnum.ENABLE.getStatus(), type);
        if (model == null) {
            throw exception(MODEL_DEFAULT_NOT_EXISTS);
        }
        return model;
    }

    /**
     * 获得模型分页
     *
     * @param pageable 分页查询
     * @param query 查询条件
     * @return 模型分页
     */
    public PageResult<AiModelDO> getModelPage(Pageable pageable, AiModelDO query) {
        log.debug("REST request to get all AiModel for page {}", pageable);

        // 根据id降序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<AiModelDO> page;
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true)
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        Example<AiModelDO> ex = Example.of(query, matcher);
        page = modelRepository.findAll(ex, pageable);

        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    /**
     * 校验模型是否可使用
     *
     * @param id 编号
     * @return 模型
     */
    public AiModelDO validateModel(Long id) {
        AiModelDO model = validateModelExists(id);
        if (CommonStatusEnum.isDisable(model.getStatus())) {
            throw exception(MODEL_DISABLE);
        }
        return model;
    }

    /**
     * 获得模型列表
     *
     * @param status 状态
     * @param type 类型
     * @param platform 平台，允许空
     * @return 模型列表
     */
    public List<AiModelDO> getModelListByStatusAndType(Integer status, Integer type,
                                                       @Nullable String platform) {
        return modelRepository.findByStatusAndTypeAndPlatform(status, type, platform);
    }

    // ========== 与 Spring AI 集成 ==========

    /**
     * 获得 ChatModel 对象
     *
     * @param id 编号
     * @return ChatModel 对象
     */
    public ChatModel getChatModel(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateChatModel(platform, apiKey.getApiKey(), apiKey.getUrl());
    }

    /**
     * 获得 ImageModel 对象
     *
     * @param id 编号
     * @return ImageModel 对象
     */
    public ImageModel getImageModel(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateImageModel(platform, apiKey.getApiKey(), apiKey.getUrl());
    }

    /**
     * 获得 MidjourneyApi 对象
     *
     * @param id 编号
     * @return MidjourneyApi 对象
     */
    public MidjourneyApi getMidjourneyApi(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        return modelFactory.getOrCreateMidjourneyApi(apiKey.getApiKey(), apiKey.getUrl());
    }

    /**
     * 获得 SunoApi 对象
     *
     * @return SunoApi 对象
     */
    public SunoApi getSunoApi() {
        AiApiKeyDO apiKey = apiKeyService.getRequiredDefaultApiKey(
                AiPlatformEnum.SUNO.getPlatform(), CommonStatusEnum.ENABLE.getStatus());
        return modelFactory.getOrCreateSunoApi(apiKey.getApiKey(), apiKey.getUrl());
    }

    /**
     * 获得 VectorStore 对象
     *
     * @param id 编号
     * @param metadataFields 元数据的定义
     * @return VectorStore 对象
     */
    public VectorStore getOrCreateVectorStore(Long id, Map<String, Class<?>> metadataFields) {
        // 获取模型 + 密钥
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());

        // 创建或获取 EmbeddingModel 对象
        EmbeddingModel embeddingModel = modelFactory.getOrCreateEmbeddingModel(
                platform, apiKey.getApiKey(), apiKey.getUrl(), model.getModel());

        // 创建或获取 VectorStore 对象
         return modelFactory.getOrCreateVectorStore(SimpleVectorStore.class, embeddingModel, metadataFields);
//         return modelFactory.getOrCreateVectorStore(QdrantVectorStore.class, embeddingModel, metadataFields);
//         return modelFactory.getOrCreateVectorStore(RedisVectorStore.class, embeddingModel, metadataFields);
//         return modelFactory.getOrCreateVectorStore(MilvusVectorStore.class, embeddingModel, metadataFields);
    }

    /**
     * 获取 TinyFlow 所需 LLm Provider
     *
     * @param tinyflow tinyflow
     * @param modelId AI 模型 ID
     */
    public void getLLmProvider4Tinyflow(Tinyflow tinyflow, Long modelId) {
        AiModelDO model = validateModel(modelId);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        switch (platform) {
            // TODO @lesan 考虑到未来不需要使用agents-flex 现在仅测试通义千问
            // TODO @lesan：【重要】是不是可以实现一个 SpringAiLlm，这样的话，内部全部用它就好了。只实现 chat 部分；这样，就把 flex 作为一个 agent 框架，内部调用，还是 spring ai 相关的。成本可能低一点？！
            case TONG_YI:
                QwenLlmConfig qwenLlmConfig = new QwenLlmConfig();
                qwenLlmConfig.setApiKey(apiKey.getApiKey());
                qwenLlmConfig.setModel(model.getModel());
                // TODO @lesan：这个有点奇怪。。。如果一个链式里，有多个模型，咋整呀。。。
                tinyflow.setLlmProvider(id -> new QwenLlm(qwenLlmConfig));
                break;
            case OLLAMA:
                OllamaLlmConfig ollamaLlmConfig = new OllamaLlmConfig();
                ollamaLlmConfig.setEndpoint(apiKey.getUrl());
                ollamaLlmConfig.setModel(model.getModel());
                tinyflow.setLlmProvider(id -> new OllamaLlm(ollamaLlmConfig));
                break;
        }
    }

}
