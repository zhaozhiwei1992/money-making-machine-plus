package com.z.framework.job.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.z.framework.job.domain.TaskLog;
import com.z.framework.job.repository.TaskLogRepository;
import com.z.framework.common.web.rest.vm.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TaskLogResource {

    @Value("${z.app.name}")
    private String applicationName;

    private final TaskLogRepository taskLogRepository;

    public TaskLogResource(TaskLogRepository taskLogRepository) {
        this.taskLogRepository = taskLogRepository;
    }

    @GetMapping("/task/logs")
    public ResponseEntity<ResponseData<HashMap<String, Object>>> getAllTaskLogings(Pageable pageable, String key) {
        log.debug("REST request to get all TaskLogging for an admin");

        // 根据id, 升序
        Sort sort = Sort.by("id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize(), sort);

        Page<TaskLog> taskLogPage;
        // 搜索
        if(StrUtil.isNotEmpty(key)){
            final TaskLog taskLog = new TaskLog();
            final List<String> cols = Arrays.asList("success", "taskName");
            //      2. 将传入属性, 填充给界面显示字段
            final Map<String, String> map = cols.stream().collect(Collectors.toMap(s -> s, key2 -> key));
            //      3. 动态构建查询条件
            BeanUtil.fillBeanWithMap(map, taskLog, true);
            log.info("填充后对象信息 {}", taskLog);

            //创建匹配器，即如何使用查询条件
            //构建对象
            ExampleMatcher matcher = ExampleMatcher
                    .matchingAny()
                    //改变默认字符串匹配方式：模糊查询
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                    //改变默认大小写忽略方式：忽略大小写
                    .withIgnoreCase(true)
                    //忽略属性：是否关注。因为是基本类型，需要忽略掉
                    .withIgnorePaths("id");

            //创建实例
            Example<TaskLog> ex = Example.of(taskLog, matcher);
            taskLogPage = taskLogRepository.findAll(ex, pageable);
        }else{
            taskLogPage = taskLogRepository.findAll(pageable);
        }
        return ResponseData.ok(new HashMap<String, Object>(){{
            put("list", taskLogPage.getContent());
            put("total", Long.valueOf(taskLogPage.getTotalElements()).intValue());
        }});
    }

}
