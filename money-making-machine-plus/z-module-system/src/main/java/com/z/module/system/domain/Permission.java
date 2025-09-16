package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: SysPermission
 * @Package com/z/module/system/domain/SysPermission.java
 * @Description: 权限配置表, 数据权限(type:1)和其它
 * @date 2024/7/21 上午1:07
 */
@Entity
@Table(name = "ele_union")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Permission extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    private String code;
    private String name;
    private Integer type;
    private String description;
}