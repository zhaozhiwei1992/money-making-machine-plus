package com.z.framework.cache.web.rest;

import com.z.framework.cache.service.CacheService;
import com.z.framework.cache.service.dto.CacheContentDTO;
import com.z.framework.cache.service.dto.CacheDTO;
import com.z.framework.cache.web.vo.CacheKeyVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: ServerMetricsResource
 * @Package com/example/web/rest/ServerMetricsResource.java
 * @Description: 缓存监控信息
 * @date 2022/6/30 下午10:18
 */
@RestController
public class CacheMetricsResource {

    private final CacheService cacheService;

    public CacheMetricsResource(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/cache/metrics")
    public List<CacheDTO> metrics() throws Exception {
        // 获取缓存列表
        return cacheService.allCacheList();
    }

    @GetMapping("/cache/metrics/keys")
    public List<CacheKeyVO> metrics(String cacheName) throws Exception {
        Set<String> strings = cacheService.cacheKeyList(cacheName);
        return strings.stream().map(s -> {
            CacheKeyVO cacheKeyVO = new CacheKeyVO();
            cacheKeyVO.setName(s);
            return cacheKeyVO;
        }).collect(Collectors.toList());
    }

    @GetMapping("/cache/metrics/content")
    public CacheContentDTO metrics(String cacheName, String key) throws Exception {
        return cacheService.getCacheValue(cacheName, key);
    }
}
