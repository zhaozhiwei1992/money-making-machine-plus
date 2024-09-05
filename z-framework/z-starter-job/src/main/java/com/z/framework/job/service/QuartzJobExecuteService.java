package com.z.framework.job.service;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 自定义定时任务
 * 站在job角度来说这里就是业务，当然也可以在内部封装处理自己的业务,累死business下的方式，实际包名只与jobName有关
 */
@Slf4j
public class QuartzJobExecuteService implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("执行自定义定时任务");

        final JobDetail jobDetail = jobExecutionContext.getJobDetail();
        final String jobName = jobDetail.getKey().getName();
        try {
            final String[] split = jobName.split("#");
//            final Class<?> forName = Class.forName(split[0]);
//            final Method method = forName.getMethod(split[1], null);
//            method.invoke(forName.newInstance(), null);
            CustomJobInterface bean = SpringUtil.getBean(split[0]);
            long l = System.currentTimeMillis();
            bean.execute();
            log.info("定时任务执行耗时: {}", System.currentTimeMillis() - l);
        } catch (Throwable throwable) {
            log.error("定时任务触发异常", throwable);
        }
    }
}
