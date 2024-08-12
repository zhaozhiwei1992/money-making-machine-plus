package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: RolePermission
 * @Package com/z/module/system/domain/RolePermission.java
 * @Description: 角色和权限关系
 * 功能权限
 * 1. 角色对菜单/按钮
 * 2. 角色对接口
 * 数据权限
 * @date 2024/7/21 上午1:09
 */
@Entity
@Table(name = "sys_role_permission")
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePermission extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 权限id
     */
    @Column(name = "permission_id")
    private Long permissionId;

}
