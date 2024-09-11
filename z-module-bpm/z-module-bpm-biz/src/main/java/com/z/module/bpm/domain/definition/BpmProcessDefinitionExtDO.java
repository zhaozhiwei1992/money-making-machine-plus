package com.z.module.bpm.domain.definition;

import com.z.framework.common.domain.AbstractAuditingEntity;
import com.z.module.bpm.domain.convert.JpaListStringJsonConverter;
import com.z.module.bpm.enums.definition.BpmModelFormTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Bpm 流程定义的拓展表
 * 主要解决 Activiti {@link ProcessDefinition} 不支持拓展字段，所以新建拓展表
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "bpm_process_definition_ext")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BpmProcessDefinitionExtDO extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 流程定义的编号
     * <p>
     * 关联 ProcessDefinition 的 id 属性
     */
    private String processDefinitionId;

    /**
     * 流程模型的编号
     * <p>
     * 关联 Model 的 id 属性
     */
    private String modelId;
    /**
     * 描述
     */
    private String description;

    /**
     * 表单类型
     * <p>
     * 关联 {@link BpmModelFormTypeEnum}
     */
    private Integer formType;
    /**
     * 动态表单编号
     * 在表单类型为 {@link BpmModelFormTypeEnum#NORMAL} 时
     * <p>
     * 关联 {@link BpmFormDO#getId()}
     */
    private Long formId;
    /**
     * 表单的配置
     * 在表单类型为 {@link BpmModelFormTypeEnum#NORMAL} 时
     * <p>
     * 冗余 {@link BpmFormDO#getConf()}
     */
    private String formConf;
    /**
     * 表单项的数组
     * 在表单类型为 {@link BpmModelFormTypeEnum#NORMAL} 时
     * <p>
     * 冗余 {@link BpmFormDO#getFields()} ()}
     */
    @Convert(converter = JpaListStringJsonConverter.class)
    private List<String> formFields;
    /**
     * 自定义表单的提交路径，使用 Vue 的路由地址
     * 在表单类型为 {@link BpmModelFormTypeEnum#CUSTOM} 时
     */
    private String formCustomCreatePath;
    /**
     * 自定义表单的查看路径，使用 Vue 的路由地址
     * 在表单类型为 {@link BpmModelFormTypeEnum#CUSTOM} 时
     */
    private String formCustomViewPath;


}
