package com.z.framework.job.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

/**
 * 定时任务配置信息\n@author zhaozhiwei
 */
@Entity
@Table(name = "sys_task_param")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskParam extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 定时任务名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 表达式
     Cron表达式的格式：秒 分 时 日 月 周 年(可选)。
     字段名              允许的值                    允许的特殊字符
     秒                     0-59                           , - * /
     分                     0-59                           , - * /
     小时                  0-23                           , - * /
     日                     1-31                            , - * ? / L W C
     月                     1-12 or JAN-DEC        , - * /
     周几                  1-7 or SUN-SAT         , - * ? / L C #
     年(可选字段)     empty                         1970-2099 , - * /
     表达式例子：

     0 * * * * ? 每1分钟触发一次
     0 0 * * * ? 每天每1小时触发一次
     0 0 10 * * ? 每天10点触发一次
     0 * 14 * * ? 在每天下午2点到下午2:59期间的每1分钟触发
     0 30 9 1 * ? 每月1号上午9点半
     0 15 10 15 * ? 每月15日上午10:15触发

    0 0 5-15 * * ? 每天5-15点整点触发
    0 0/3 * * * ? 每三分钟触发一次
    0 0-5 14 * * ? 在每天下午2点到下午2:05期间的每1分钟触发
    0 0/5 14 * * ? 在每天下午2点到下午2:55期间的每5分钟触发
    0 0/5 14,18 * * ? 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
    0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时
    0 0 10,14,16 * * ? 每天上午10点，下午2点，4点
     */
    @Column(name = "cron_expression")
    private String cronExpression;

    /**
     * 定时任务入口
     */
    @Column(name = "start_class")
    private String startClass;

    /**
     * 是否启用
     */
    @Column(name = "enable")
    private Boolean enable;

}
