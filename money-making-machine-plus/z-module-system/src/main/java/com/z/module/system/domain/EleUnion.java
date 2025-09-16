package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 基础数据总表\n
 * 数据量小的可以统一使用总表创建即可\n
 * 单独的表可以使用ele_001001类似的表名
 */
@Entity
@Table(name = "sys_ele_union")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class EleUnion extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 代码集分类编码\n如: 001001\n如果在该表不存在数据, 则通过ele_eleCatCode方式来查其它表
     */
    @Column(name = "ele_cat_code")
    private String eleCatCode;

    /**
     * 代码集分类名称\n如: 单位
     */
    @Column(name = "ele_cat_name")
    private String eleCatName;

    /**
     * 代码集代码
     */
    @Column(name = "ele_code")
    private String eleCode;

    /**
     * 代码集名称
     */
    @Column(name = "ele_name")
    private String eleName;

    /**
     * 父级节点主键
     * 父级id使用eleCode
     */
    @Column(name = "parent_id")
    private String parentId;

    /**
     * 级次
     */
    @Column(name = "level_no")
    private Integer levelNo;

    /**
     * 是否末级
     */
    @Column(name = "is_leaf")
    private Boolean isLeaf;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled;
}
