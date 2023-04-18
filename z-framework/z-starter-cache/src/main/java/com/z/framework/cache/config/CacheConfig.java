package com.z.framework.cache.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @Title: CacheConfig
 * @Package com/longtu/config/CacheConfig.java
 * @Description: 缓存配置, 尽量简化, 直接使用本地服务缓存
 * @author zhaozhiwei
 * @date 2022/7/12 下午9:49
 * @version V1.0
 */
@Configuration
@EnableCaching
@EnableScheduling
@Slf4j
public class CacheConfig {

    /**
     * 自定义方式会覆盖原有实现cachemanager接口的管理器，可用缓存容器中必须包含{@see PersonRepository}
     * 中cacheable#names中要使用的容器
     * {@see https://stackoverflow.com/questions/27968157/expiry-time-cacheable-spring-boot}
     * @return
     */
    @Bean
    public CacheManager simpleCacheManager(){
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        // 登录用户菜单缓存
        ConcurrentMapCache loginMenuCache = new ConcurrentMapCache("loginMenuCache");
        ConcurrentMapCache userByLoginCache = new ConcurrentMapCache("usersByLogin");
        ConcurrentMapCache sysParamCache = new ConcurrentMapCache("sysParamCache");
        simpleCacheManager.setCaches(Arrays.asList(loginMenuCache, userByLoginCache, sysParamCache));
        return simpleCacheManager;
    }

    /**
     * @data: 2021/9/21-下午11:57
     * @User: zhaozhiwei
     * @method: simpleCacheEvict

     * @return: void
     * @Description:
     * 10分钟
     * 清理本身不支持expire的缓存实现
     * 时间可自己指定
     *
     * 如果用redis, ehcache不需要这玩意儿
     */
    @CacheEvict(allEntries = true, value = {"usersByLogin", "loginMenuCache"})
    @Scheduled(fixedDelay = 6 * 1000 * 100 ,  initialDelay = 500)
    public void simpleCacheEvict() {
        log.info("Flush Cache at : {}", new SimpleDateFormat().format(new Date()));
    }

}
