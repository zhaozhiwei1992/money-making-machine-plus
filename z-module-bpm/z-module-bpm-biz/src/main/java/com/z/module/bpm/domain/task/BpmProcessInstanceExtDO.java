package com.z.module.bpm.domain.task;

import com.z.framework.common.domain.AbstractAuditingEntity;
import com.z.module.bpm.domain.convert.JpaMapJsonConverter;
import com.z.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.z.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Bpm 流程实例的拓展表
 * 主要解决 Activiti ProcessInstance 和 HistoricProcessInstance 不支持拓展字段，所以新建拓展表
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "bpm_process_instance_ext")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class BpmProcessInstanceExtDO extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 发起流程的用户编号
     * <p>
     * 冗余 HistoricProcessInstance 的 startUserId 属性
     */
    private Long startUserId;
    /**
     * 流程实例的名字
     * <p>
     * 冗余 ProcessInstance 的 name 属性，用于筛选
     */
    private String name;
    /**
     * 流程实例的编号
     * <p>
     * 关联 ProcessInstance 的 id 属性
     */
    private String processInstanceId;
    /**
     * 流程定义的编号
     * <p>
     * 关联 ProcessDefinition 的 id 属性
     */
    private String processDefinitionId;
    /**
     * 流程分类
     * <p>
     * 冗余 ProcessDefinition 的 category 属性
     * 数据字典 bpm_model_category
     */
    private String category;
    /**
     * 流程实例的状态
     * <p>
     * 枚举 {@link BpmProcessInstanceStatusEnum}
     */
    private Integer status;
    /**
     * 流程实例的结果
     * <p>
     * 枚举 {@link BpmProcessInstanceResultEnum}
     */
    private Integer result;
    /**
     * 结束时间
     * <p>
     * 冗余 HistoricProcessInstance 的 endTime 属性
     */
    private LocalDateTime endTime;

    /**
     * 提交的表单值
     */
//    @TableField(typeHandler = JacksonTypeHandler.class)
    @Convert(converter = JpaMapJsonConverter.class)
    private Map<String, Object> formVariables;

}
