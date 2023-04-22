package com.z.framework.job.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

/**
 * @Title: TaskLog
 * @Package com/longtu/domain/TaskLog.java
 * @Description: 定时任务日志
 * @author zhaozhiwei
 * @date 2022/7/28 上午10:24
 * @version V1.0
 */
@Entity
@Table(name = "sys_task_log")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TaskLog extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
