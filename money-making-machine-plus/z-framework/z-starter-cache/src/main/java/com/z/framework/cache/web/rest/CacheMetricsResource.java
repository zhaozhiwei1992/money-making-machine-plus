package com.z.framework.cache.web.rest;

import com.z.framework.cache.service.CacheService;
import com.z.framework.cache.service.dto.CacheContentDTO;
import com.z.framework.cache.service.dto.CacheDTO;
import com.z.framework.cache.web.vo.CacheKeyVO;
import com.z.framework.cache.web.vo.RedisMonitorRespVO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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

    private final StringRedisTemplate stringRedisTemplate;

    public CacheMetricsResource(CacheService cacheService, StringRedisTemplate stringRedisTemplate) {
        this.cacheService = cacheService;
        this.stringRedisTemplate = stringRedisTemplate;
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

    @GetMapping("/cache/metrics/redis")
    public RedisMonitorRespVO getRedisMonitorInfo() {
        // 获得 Redis 统计信息
        Properties info = stringRedisTemplate.execute((RedisCallback<Properties>) RedisServerCommands::info);
        Long dbSize = stringRedisTemplate.execute(RedisServerCommands::dbSize);
        Properties commandStats = stringRedisTemplate.execute((
                RedisCallback<Properties>) connection -> connection.serverCommands().info("commandstats"));
        assert commandStats != null; // 断言，避免警告

        RedisMonitorRespVO respVO = RedisMonitorRespVO.builder().info(info).dbSize(dbSize)
                .commandStats(new ArrayList<>(commandStats.size())).build();
        commandStats.forEach((key, value) -> {
            respVO.getCommandStats().add(RedisMonitorRespVO.CommandStat.builder()
                    .command(subAfter((String) key, "cmdstat_", false))
                    .calls(Long.valueOf(subBetween((String) value, "calls=", ",")))
                    .usec(Long.valueOf(subBetween((String) value, "usec=", ",")))
                    .build());
        });
        return respVO;
    }

    public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (!StringUtils.hasText(string)) {
            return null == string ? null : "";
        } else if (separator == null) {
            return "";
        } else {
            String str = string.toString();
            String sep = separator.toString();
            int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
            return -1 != pos && string.length() - 1 != pos ? str.substring(pos + separator.length()) : "";
        }
    }

    public static String subBetween(CharSequence str, CharSequence before, CharSequence after) {
        if (str != null && before != null && after != null) {
            String str2 = str.toString();
            String before2 = before.toString();
            String after2 = after.toString();
            int start = str2.indexOf(before2);
            if (start != -1) {
                int end = str2.indexOf(after2, start + before2.length());
                if (end != -1) {
                    return str2.substring(start + before2.length(), end);
                }
            }

            return null;
        } else {
            return null;
        }
    }

}
