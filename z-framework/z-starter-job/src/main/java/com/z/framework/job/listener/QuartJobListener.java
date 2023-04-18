package com.z.framework.job.listener;

import cn.hutool.extra.spring.SpringUtil;
import com.z.framework.job.domain.TaskParam;
import com.z.framework.job.repository.TaskParamRepository;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.*;

/**
 * @Title: QuartJobListener
 * @Package com/longtu/aop/QuartJobListener.java
 * @Description: 一些定时任务监控信息(日志)搞到这里
 * @author zhaozhiwei
 * @date 2022/7/27 下午4:42
 * @version V1.0
 */
public class QuartJobListener implements JobListener {

    private static final Logger log = LoggerFactory.getLogger(QuartJobListener.class);

    public static final String LISTENER_NAME = "QuartSchedulerListener";

    private static final ThreadLocal<Map<String, Object>> taskThreadLocal = new ThreadLocal<>();

    private TaskParamRepository taskParamRepository;

    public TaskParamRepository getTaskParamRepository() {
        if (Objects.isNull(taskParamRepository)) {
            taskParamRepository = SpringUtil.getBean(TaskParamRepository.class);
        }
        return taskParamRepository;
    }

    @Override
    public String getName() {
        return LISTENER_NAME;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        //任务被调度前
        String jobName = context.getJobDetail().getKey().toString();
        // 生成traceId
        final String traceId = UUID.randomUUID().toString();
        // *.log中使用
        MDC.put("traceId", traceId);
        final Map<String, Object> taskLog = new HashMap<>();
        //        taskLog.setTraceId(traceId);
        //        taskLog.setStartTime(Instant.now());
        taskLog.put("TraceId", traceId);
        taskLog.put("StartTime", Instant.now());
        taskThreadLocal.set(taskLog);

        log.info("jobToBeExecuted");
        log.info("Job : {} is going to start...", jobName);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        //任务调度被拒了
        log.info("jobExecutionVetoed");
        //可以做一些日志记录原因

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        //任务被调度后
        log.info("jobWasExecuted");
        String jobName = context.getJobDetail().getKey().toString();
        log.info("Job : {} is finished...", jobName);

        //        final TaskLog taskLog = taskThreadLocal.get();
        final Map<String, Object> taskLog = taskThreadLocal.get();
        final String startClass = jobName.substring(jobName.indexOf(".") + 1);
        Optional<TaskParam> taskParamOptional = this.getTaskParamRepository().findOneByStartClass(startClass);
        if (taskParamOptional.isPresent()) {
            taskLog.put("TaskName", taskParamOptional.get().getName());
        } else {
            taskLog.put("TaskName", startClass);
        }
        taskLog.put("Success", "是");
        if (jobException != null && !"".equals(jobException.getMessage())) {
            log.error("Exception thrown by: " + jobName + " Exception: ", jobException);
            taskLog.put("Success", "否");
        }
        final Instant endTime = Instant.now();
        taskLog.put("EndTime", endTime);
        //        taskLog.put("TotalTime", Integer.valueOf(String.valueOf(Duration.between(taskLog.getStartTime(), taskLog.getEndTime()).getSeconds())));

        // 持久化， 这里是简化taskLog为map, 实际为bean
        taskThreadLocal.remove();
    }
}
