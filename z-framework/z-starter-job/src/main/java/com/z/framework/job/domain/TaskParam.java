package com.z.framework.job.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TaskParam extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 定时任务名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 表达式
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
