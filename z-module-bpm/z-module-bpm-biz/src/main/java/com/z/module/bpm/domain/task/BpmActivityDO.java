package com.z.module.bpm.domain.task;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 任务流程关联表
 *
 * @author kemengkai
 * @create 2022-05-09 10:33
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bpm_activity")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BpmActivityDO extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 审批结果
     */
    private Integer rev;

    /**
     * 任务流程部署id
     */
    private String procDefId;

    /**
     * 任务流程id
     */
    private String processInstanceId;

    /**
     * 任务执行id
     */
    private String executionId;

    /**
     * 任务key
     */
    private String activityId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 调用流程id
     */
    private String callProcInstId;

    /**
     * 任务名称
     */
    private String activityName;

    /**
     * 任务类型
     */
    private String activityType;

    /**
     * 任务审批人id
     */
    private String assignee;

    /**
     * 任务开始时间
     */
//    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
//    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = TIME_ZONE_DEFAULT)
    private LocalDateTime startTime;

    /**
     * 任务结束时间
     */
//    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
//    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = TIME_ZONE_DEFAULT)
    private LocalDateTime endTime;

    private Integer transactionOrder;

    private LocalDateTime duration;

    /**
     * 删除结果
     */
    private String deleteReason;

    /**
     * 租户id
     */
    private String tenantId;
}
