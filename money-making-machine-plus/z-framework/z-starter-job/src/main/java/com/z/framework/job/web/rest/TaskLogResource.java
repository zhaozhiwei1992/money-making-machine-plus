package com.z.framework.job.web.rest;

import com.z.framework.job.domain.TaskLog;
import com.z.framework.job.repository.TaskLogRepository;
import com.z.framework.common.web.rest.vm.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
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
    public HashMap<String, Object> getAllTaskLog(Pageable pageable, TaskLog taskLog) {
        log.debug("REST request to get all TaskLogging for an admin");

        // 根据id, 升序
        Sort sort = Sort.by("id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<TaskLog> taskLogPage;
        // 搜索

        //创建匹配器，即如何使用查询条件
        //构建对象
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                //改变默认字符串匹配方式：模糊查询
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                //改变默认大小写忽略方式：忽略大小写
                .withIgnoreCase(true)
                .withIgnoreNullValues()
                //忽略属性：是否关注。因为是基本类型，需要忽略掉
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        //创建实例
        Example<TaskLog> ex = Example.of(taskLog, matcher);
        taskLogPage = taskLogRepository.findAll(ex, pageable);
        return new HashMap<String, Object>() {{
            put("list", taskLogPage.getContent());
            put("total", Long.valueOf(taskLogPage.getTotalElements()).intValue());
        }};
    }

}
