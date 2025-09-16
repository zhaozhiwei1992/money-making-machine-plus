package com.z.module.system.web.rest;

import com.z.framework.common.util.date.DateUtils;
import com.z.framework.operatelog.domain.RequestLog;
import com.z.framework.operatelog.repository.RequestLogRepository;
import com.z.framework.security.util.SecurityUtils;
import com.z.module.system.domain.LoginLog;
import com.z.module.system.repository.LoginLogRepository;
import com.z.module.system.repository.UserRepository;
import com.z.module.system.repository.mapper.LoginLogCount;
import com.z.module.system.repository.mapper.LoginLogMonthlyGroupCount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Tag(name = "分析页API")
@RestController
@RequestMapping("/system/analysis")
@Slf4j
public class AnalysisResource {

    private final LoginLogRepository loginLogRepository;

    private final CacheManager cacheManager;

    private final UserRepository userRepository;

    private final RequestLogRepository requestLogRepository;

    public AnalysisResource(LoginLogRepository loginLogRepository, CacheManager cacheManager,
                            UserRepository userRepository, RequestLogRepository requestLogRepository) {
        this.loginLogRepository = loginLogRepository;
        this.cacheManager = cacheManager;
        this.userRepository = userRepository;
        this.requestLogRepository = requestLogRepository;
    }

    @Operation(description = "用户访问来源")
    @GetMapping("/userAccessSource")
    public List<Map<String, Object>> userAccessSource() {
        // 通过登录日志, 构建访问来源
        final List<LoginLogCount> countByBrowserAndOs = loginLogRepository.getCountByBrowserAndOs();
        final List<Map<String, Object>> collect = countByBrowserAndOs.stream().map(m -> {
            final Map<String, Object> map = new HashMap<>();
            map.put("name", m.getOs() + "-" + m.getBrowser());
            map.put("value", m.getCount());
            return map;
        }).collect(Collectors.toList());
        return collect;
    }

    @Operation(description = "每周用户活跃")
    @GetMapping("/weeklyUserActivity")
    public List<Map<String, Object>> weeklyUserActivity() {
        // 获取本周, 1-7用户每天登录数
        final Instant startDayOfWeek = DateUtils.getStartDayOfWeek(LocalDateTime.now());
        final Instant endDayOfWeek = DateUtils.getEndDayOfWeek(LocalDateTime.now());
        List<LoginLog> loginLogList = loginLogRepository.findAllByCreatedDateBetween(startDayOfWeek, endDayOfWeek);
        // 数据根据日期分组汇总, 转换为周一到周天
        final Map<LocalDate, Long> collect = loginLogList.stream()
                .collect(Collectors.groupingBy(m -> LocalDateTime.ofInstant(m.getCreatedDate(),
                        ZoneId.systemDefault()).toLocalDate(), Collectors.counting()));
        if (collect.keySet().size() > 7) {
            throw new RuntimeException("每周数据统计错误");
        }

        final List<Map<String, Object>> maps = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> dateLongEntry : collect.entrySet()) {
            final LocalDate key = dateLongEntry.getKey();
            final DayOfWeek dayOfWeek = key.getDayOfWeek();
            final Long value = dateLongEntry.getValue();
            final Map<String, Object> map = new HashMap<>();
            map.put("dayOfWeek", dayOfWeek);
            map.put("name", "analysis." + dayOfWeek.toString().toLowerCase());
            map.put("value", value);
            maps.add(map);
        }

        final List<Map<String, Object>> collect1 = maps.stream().sorted(Comparator.comparing(a -> ((DayOfWeek) a.get(
                "dayOfWeek")))).collect(Collectors.toList());

        // 排序
        return collect1;
    }

    @Operation(description = "每月用户活跃")
    @GetMapping("/monthlyUserActivity")
    public List<Map<String, Object>> monthlyUserActivity() {
        // 获取本年, 用户每月登录数
        final Integer year = SecurityUtils.getYear();
        final List<LoginLogMonthlyGroupCount> allByYearGroupByMonth =
                loginLogRepository.findAllByYearGroupByMonth(year);
        final List<Map<String, Object>> collect =
                allByYearGroupByMonth.stream().sorted(Comparator.comparing(LoginLogMonthlyGroupCount::getMonth)).map(m -> {
                    final Map<String, Object> map = new HashMap<>();
                    map.put("estimate", 0);
                    map.put("actual", m.getCount());
                    final Month month = LocalDate.of(year, m.getMonth(), 1).getMonth();
                    map.put("name", "analysis." + month.toString().toLowerCase());
                    return map;
                }).collect(Collectors.toList());
        return collect;
    }

    /**
     * @data: 2023/6/1-上午10:51
     * @User: zhaozhiwei
     * @method: getOneDict
     * @return: org.springframework.http.com.z.framework.common.web.rest.vm.ResponseData < java.util.List < java.util.Map < java.lang.String, java.lang.Object>>>
     * @Description: 返回: 当前登录用户数, 注册用户数, 总接口访问量, 当日接口访问量
     */
    @Operation(description = "统计数据")
    @GetMapping("/total")
    public Map<String, Object> getOneDict() {
        final Map<String, Object> result = new HashMap<>();
        // 当前用户数
        final Cache tokenBlackCache = cacheManager.getCache("tokenWriteListCache");
        List<String> tokenWriteList;
        if (!Objects.isNull(tokenBlackCache.get("tokenWriteList"))) {
            tokenWriteList = (List<String>) tokenBlackCache.get("tokenWriteList").get();
            result.put("loginCount", tokenWriteList.size());
        }

        // 注册用户数
        final long count = userRepository.count();
        result.put("userCount", count);

        // 总接口访问量
        final long requestLogCount = requestLogRepository.count();
        result.put("requestLogCount", requestLogCount);

        // 当日访问量
        ExampleMatcher matcher = ExampleMatcher.matching();
        final RequestLog requestLog = new RequestLog();
        requestLog.setCreatedDate(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
        final Example<RequestLog> of = Example.of(requestLog, matcher);
        final long todayRequestLogCount = requestLogRepository.count(of);
        result.put("todayRequestLogCount", todayRequestLogCount);

        return result;
    }

}
