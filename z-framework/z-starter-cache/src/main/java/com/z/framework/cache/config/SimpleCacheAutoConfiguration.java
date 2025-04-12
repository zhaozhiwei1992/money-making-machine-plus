package com.z.framework.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Title: SimpleCacheAutoConfiguration
 * @Package com/longtu/config/SimpleCacheAutoConfiguration.java
 * @Description: 缓存配置, 尽量简化, 直接使用本地服务缓存
 * 条件化配置, 需要在application.yaml中配置z.cache==simple
 *
 * 配置单一缓存使用
 * @author zhaozhiwei
 * @date 2022/7/12 下午9:49
 * @version V1.0
 */
@AutoConfiguration
@EnableCaching
@EnableScheduling
@Slf4j
@ComponentScan(value = {"com.z.framework.cache"})
@ConditionalOnProperty(prefix = "z", name = "cache", havingValue = "simple")
public class SimpleCacheAutoConfiguration {

    /**
     * 自定义方式会覆盖原有实现cachemanager接口的管理器，可用缓存容器中必须包含{@see PersonRepository}
     * 中cacheable#names中要使用的容器
     * 该方式缓存清理需要配合定时器，不够灵活
     * {@see https://stackoverflow.com/questions/27968157/expiry-time-cacheable-spring-boot}
     *
     * @return
     */
//    @Bean
    public CacheManager simpleCacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        // 登录用户菜单缓存
        ConcurrentMapCache loginMenuCache = new ConcurrentMapCache("loginMenuCache");
        ConcurrentMapCache userByLoginCache = new ConcurrentMapCache("usersByLogin");
        ConcurrentMapCache sysParamCache = new ConcurrentMapCache("sysParamCache");
        // 登录后token写入到cache中, 方便登出控制, 仅界面使用
        ConcurrentMapCache tokenCache = new ConcurrentMapCache("tokenCache");
        // 存储一体化登录后的token信息, key为: 用户名 value为一体化token
        ConcurrentMapCache ifmisTokenCache = new ConcurrentMapCache("ifmisTokenCache");
        simpleCacheManager.setCaches(Arrays.asList(loginMenuCache, userByLoginCache, sysParamCache, tokenCache));
        return simpleCacheManager;
    }

    /**
     * @data: 2021/9/21-下午11:57
     * @User: zhaozhiwei
     * @method: simpleCacheEvict
     * @return: void
     * @Description: 10分钟
     * 清理本身不支持expire的缓存实现
     * 时间可自己指定
     * <p>
     * 如果用redis, ehcache不需要这玩意儿
     */
//    @CacheEvict(allEntries = true, value = {"usersByLogin", "loginMenuCache"})
//    @Scheduled(fixedDelay = 6 * 1000 * 100, initialDelay = 500)
    public void simpleCacheEvict() {
        log.info("Flush Cache at : {}", new SimpleDateFormat().format(new Date()));
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }

    private Map<String, CacheProperties.CacheSpec> specs;

    private CacheProperties cacheProperties;

    public SimpleCacheAutoConfiguration(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
        specs = cacheProperties.getSpecs();
    }

    /**
     * @data: 2021/9/21-下午11:57
     * @User: zhaozhiwei
     * @method: cacheManager
     * @return: org.springframework.cache.CacheManager
     * @Description: 使用caffeine实现缓存
     */
    @Bean
    public CacheManager cacheManager(Ticker ticker) {
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
}
