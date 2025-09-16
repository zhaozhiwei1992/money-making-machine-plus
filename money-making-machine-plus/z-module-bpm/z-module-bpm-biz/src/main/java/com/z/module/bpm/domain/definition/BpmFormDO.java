package com.z.module.bpm.domain.definition;

import com.z.framework.common.domain.AbstractAuditingEntity;
import com.z.module.bpm.domain.convert.JpaListStringJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 工作流的表单定义
 * 用于工作流的申请表单，需要动态配置的场景
 */
@Entity
@Table(name = "bpm_form")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmFormDO extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 表单名
     */
    private String name;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 表单的配置
     */
    private String conf;

    /**
     * 表单项的数组
     */
    @Convert(converter = JpaListStringJsonConverter.class)
    private List<String> fields;
    /**
     * 备注
     */
    private String remark;

}
