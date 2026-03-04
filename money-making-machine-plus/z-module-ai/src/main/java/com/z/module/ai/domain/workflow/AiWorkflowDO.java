package com.z.module.ai.domain.workflow;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI 工作流 DO
 *
 * @author lesan
 */
@Entity
@Table(name = "ai_workflow")
@Data
@ToString(callSuper = true)

public class AiWorkflowDO extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("编号")
    private Long id;

    @Comment("工作流名称")
    private String name;

    @Comment("工作流标识")
    private String code;

    @Lob
    @Comment("工作流模型 JSON 数据")
    private String graph;

    @Lob
    @Comment("备注")
    private String remark;

    @Comment("状态")
    private Integer status;

}
