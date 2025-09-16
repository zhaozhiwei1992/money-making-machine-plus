package com.z.framework.job.service;

import com.z.framework.job.domain.TaskParam;
import com.z.framework.job.repository.TaskParamRepository;
import com.z.framework.job.listener.QuartJobListener;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.List;

@Component
@Transactional
public class QuartzJobManagerService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private JobListener jobListener;

    @Autowired
    private TaskParamRepository taskParamRepository;

    /**
     * @data: 2022/6/2-下午4:30
     * @User: zhaozhiwei
     * @method: startJobAll
     * @return: java.lang.String
     * @Description: 启动所有定时任务
     *
     *  # 每月的最后1天
     *     @Scheduled(cron = "0 0 18 L * ?")
     *     public void doAtLastDayOfMonth() {
     *         // do something here...
     *     }
     *
     *     说明：
     *     Java(Quartz)
     *     *    *    *    *    *    *    *
     *     -    -    -    -    -    -    -
     *     |    |    |    |    |    |    |
     *     |    |    |    |    |    |    + year [optional]
     *     |    |    |    |    |    +----- day of week (1 - 7) sun,mon,tue,wed,thu,fri,sat
     *     |    |    |    |    +---------- month (1 - 12) OR jan,feb,mar,apr ...
     *     |    |    |    +--------------- day of month (1 - 31)
     *     |    |    +-------------------- hour (0 - 23)
     *     |    +------------------------- min (0 - 59)
     *     +------------------------------ second (0 - 59)
     */
    public String startJobAll() {
        //        final HashMap<String, String> job1 = new HashMap<>();
        //        job1.put("cron", "0/5 * * * * ?");
        //        job1.put("jobName", "com.lx.demo.springbootschedulerquartz.business.Job1#execute");
        //        job1.put("jobGroup", "group1");

        //       查询启用的任务
        final List<TaskParam> taskParams = taskParamRepository.findAllByEnable(true);

        taskParams.forEach(taskParam -> {
            try {
                this.startJob(taskParam.getCronExpression(), taskParam.getStartClass(), "defaultGroup", QuartzJobExecuteService.class);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });
        return "启动定时器成功";
    }

    /**
     * 开始定时任务
     *
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException
     */
    public void startJob(String cron, String jobName, String jobGroup, Class<? extends Job> jobClass) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (jobListener == null) {
            jobListener = new QuartJobListener();
            scheduler.getListenerManager().addJobListener(jobListener);
        }
        JobKey jobKey = new JobKey(jobName, jobGroup);
        if (!scheduler.checkExists(jobKey)) {
            scheduleJob(cron, scheduler, jobName, jobGroup, jobClass);
        }
    }

    /**
     * 移除定时任务
     *
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException
     */
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = new JobKey(jobName, jobGroup);
        scheduler.deleteJob(jobKey);
    }

    /**
     * 暂停定时任务
     *
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException
     */
    public void pauseJob(String jobName, String jobGroup) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = new JobKey(jobName, jobGroup);
        scheduler.pauseJob(jobKey);
    }

    /**
     * 恢复定时任务
     *
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException
     */
    public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey triggerKey = new JobKey(jobName, jobGroup);
        scheduler.resumeJob(triggerKey);
    }

    /**
     * 清空所有当前scheduler对象下的定时任务【目前只有全局一个scheduler对象】
     *
     * @throws SchedulerException
     */
    public void clearAll() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.clear();
    }

    /**
     * 动态创建Job
     *
     * @param scheduler
     * @throws SchedulerException
     */
    private void scheduleJob(String cron, Scheduler scheduler, String jobName, String jobGroup, Class<? extends Job> jobClass)
        throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    @PostConstruct
    public void init() throws InterruptedException {
        this.startJobAll();
    }
}
