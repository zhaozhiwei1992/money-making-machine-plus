package com.z.module.template.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 模板条目实体（演示最小 CRUD 的数据模型）。
 */
@Entity
@Table(name = "tpl_t_item", uniqueConstraints = @UniqueConstraint(name = "uk_item_code", columnNames = "code"))
@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateItem extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "remark", length = 500)
    private String remark;
}