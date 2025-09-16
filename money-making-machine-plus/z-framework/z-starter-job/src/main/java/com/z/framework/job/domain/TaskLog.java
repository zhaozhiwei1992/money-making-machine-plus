package com.z.framework.job.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: TaskLog
 * @Package com/longtu/domain/TaskLog.java
 * @Description: 定时任务日志
 * @date 2022/7/28 上午10:24
 */
@Entity
@Table(name = "sys_task_log")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TaskLog extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    /**
     * 定时任务名称
     */
    @Column(name = "task_name")
    private String taskName;

    @Column(name = "start_time", updatable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    /**
     * 总耗时
     */
    @Column(name = "total_time")
    private Integer totalTime;

    /**
     * 一些细节内容, 如 总共处理多少条数据 dataCount
     */
    @Column(name = "detail", length = 1000)
    private String detail;

    @Column(name = "trace_id")
    private String traceId;

    /**
     * 任务是否执行成功, 是/否
     */
    @Column(name = "success")
    private String success;

}
