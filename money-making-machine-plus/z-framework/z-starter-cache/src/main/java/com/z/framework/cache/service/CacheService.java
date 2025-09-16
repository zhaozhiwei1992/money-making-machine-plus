package com.z.framework.cache.service;

import com.z.framework.cache.config.CacheProperties;
import com.z.framework.cache.service.dto.CacheContentDTO;
import com.z.framework.cache.service.dto.CacheDTO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Title: null.java
 * @Package: com.z.framework.cache.service.dto
 * @Description: 获取缓存各个维度信息
 * @author: zhaozhiwei
 * @date: 2024/8/28 上午10:52
 * @version: V1.0
 */
@Service
@Slf4j
public class CacheService {

    private final CacheManager cacheManager;
    private final CacheProperties cacheProperties;

    public CacheService(CacheManager cacheManager, CacheProperties cacheProperties) {
        this.cacheManager = cacheManager;
        this.cacheProperties = cacheProperties;
    }

    public List<CacheDTO> allCacheList(){

        Map<String, CacheProperties.CacheSpec> specs = cacheProperties.getSpecs();

        return cacheManager.getCacheNames().stream().map(s -> {
            CacheDTO cacheDTO = new CacheDTO();
            cacheDTO.setName(s);
            cacheDTO.setRemark(specs.get(s).getRemark());
            return cacheDTO;
        }).collect(Collectors.toList());
    }

    public Set<String> cacheKeyList(String cacheName){
        Cache cache = cacheManager.getCache(cacheName);
        if(cache instanceof CaffeineCache){
            final CaffeineCache caffeineCache = (CaffeineCache) cache;
            return caffeineCache.getNativeCache().asMap().keySet().stream().map(String::valueOf).collect(Collectors.toSet());
        }else if(cache instanceof RedissonCache){
            final RedissonCache redissonCache = (RedissonCache) cache;
            return redissonCache.getNativeCache().keySet().stream().map(String::valueOf).collect(Collectors.toSet());
        }else{
            log.error("不支持的缓存类型");
        }
        return null;
    }

    public CacheContentDTO getCacheValue(String cacheName, String key){
        Cache cache = cacheManager.getCache(cacheName);
        CacheContentDTO cacheContentDTO = new CacheContentDTO();
        cacheContentDTO.setName(cacheName);
        cacheContentDTO.setKey(key);
        cacheContentDTO.setContent(cache.get(key).toString());
        return cacheContentDTO;
    }

    public void evictCache(String cacheName){
        cacheManager.getCache(cacheName).clear();
    }

    public void evictAllCache(){
        cacheManager.getCacheNames().forEach(this::evictCache);
    }

    public void evictCache(String cacheName, Object key){
        cacheManager.getCache(cacheName).evict(key);
    }

}
