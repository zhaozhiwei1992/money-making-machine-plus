package com.z.framework.cache.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Title: SimpleCacheAutoConfiguration
 * @Package com/longtu/config/SimpleCacheAutoConfiguration.java
 * @Description: 配置多级缓存
 * @author zhaozhiwei
 * @date 2022/7/12 下午9:49
 * @version V1.0
 */
@AutoConfiguration
@EnableCaching
@EnableScheduling
@Slf4j
@ComponentScan(value = {"com.z.framework.cache"})
@ConditionalOnProperty(prefix = "z", name = "cache", havingValue = "multi")
public class MultiCacheAutoConfiguration {

    private Map<String, CacheProperties.CacheSpec> specs;

    private CacheProperties cacheProperties;

    public MultiCacheAutoConfiguration(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
        specs = cacheProperties.getSpecs();
    }


    @Bean
    public CacheManager cacheManager(Ticker ticker, RedissonClient redissonClient){
        // 多级缓存通过开关控制吧，redis这个缓存跟springsecurity结合还得好好测试，经常出现403
//        CompositeCacheManager compositeCacheManager = new CompositeCacheManager(localCacheManager(ticker), distributedCacheManager(redissonClient));
        // 先用单个缓存
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager(localCacheManager(ticker));
        // 防止缓存未命中时抛出异常
        compositeCacheManager.setFallbackToNoOpCache(true);
        return  compositeCacheManager;
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }

    /**
     * @data: 2021/9/21-下午11:57
     * @User: zhaozhiwei
     * @method: cacheManager
     * @return: org.springframework.cache.CacheManager
     * @Description: 使用caffeine实现缓存
     */
    public CacheManager localCacheManager(Ticker ticker) {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        if (specs != null) {
            List<CaffeineCache> caches =
                    specs.entrySet().stream()
                            .map(entry -> buildCache(entry.getKey(),
                                    entry.getValue(),
                                    ticker))
                            .collect(Collectors.toList());
            simpleCacheManager.setCaches(caches);
        }
        return simpleCacheManager;
    }

    private CaffeineCache buildCache(String name, CacheProperties.CacheSpec cacheSpec, Ticker ticker) {
        final Caffeine<Object, Object> caffeineBuilder
                = Caffeine.newBuilder()
                .expireAfterWrite(cacheSpec.getExpireTime(), TimeUnit.SECONDS)
                .maximumSize(cacheSpec.getMaxSize())
                .ticker(ticker);
        return new CaffeineCache(name, caffeineBuilder.build());
    }

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.database}")
    private int database;


    /**
     * @data: 2021/9/21-下午11:57
     * @User: zhaozhiwei
     * @method: redissonClient
     * @return: org.redisson.api.RedissonClient
     * @Description: 创建RedissonClient
     *
     * 问题的完整链条
     * Redisson Spring Boot Starter自动创建默认的RedissonClient
     * 默认使用Kryo5Codec进行序列化（Kryo版本5.5.0）
     * Spring Security的User对象包含不可修改的authorities集合
     * Kryo反序列化时调用add()方法向不可修改集合添加元素
     * 抛出UnsupportedOperationException
     * 跟org/redisson/spring/starter/RedissonAutoConfiguration.class冲突，需要用primary或者配置来覆盖codec即可，保持原有redissionClient初始化逻辑
     * 使用自定义RedissonClient，配置JSON序列化以避免Kryo序列化Spring Security User对象时的问题
     * 添加JavaTimeModule支持Java 8日期时间类型序列化
     */
    @Bean
    @Primary
    public RedissonClient redissonClient(){
        Config config = new Config();

        // 创建自定义ObjectMapper，添加JavaTimeModule支持Java 8日期时间类型
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 使用配置好的ObjectMapper创建JsonJacksonCodec
        config.setCodec(new JsonJacksonCodec(objectMapper));

        final SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://" + host + ":" + port);
        singleServerConfig.setDatabase(database);
        return Redisson.create(config);
    }

    /**
     * @Description: 创建分布式缓存
     * @author: zhaozhiwei
     * @data: 2025/4/12-14:48

     * @return: org.springframework.cache.CacheManager
    */

    private CacheManager distributedCacheManager(RedissonClient redissonClient) {
        if (specs != null) {

            Map<String, CacheConfig> collect = specs.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> {
                CacheProperties.CacheSpec value = entry.getValue();
                // 创建一个名称为"testMap"的缓存，过期时间ttl为24分钟，同时最长空闲时maxIdleTime为12分钟。
//                config.put("testMap", new CacheConfig(24*60*1000, 12*60*1000));
                return new CacheConfig(value.getExpireTime(), value.getExpireTime());
            }));
            return new RedissonSpringCacheManager(redissonClient, collect);
        }else{
            return null;
        }
    }
}
