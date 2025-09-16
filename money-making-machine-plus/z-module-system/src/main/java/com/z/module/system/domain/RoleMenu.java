package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 角色对菜单\n@author zhaozhiwei
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role_menu")
@Data
public class RoleMenu extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    /**
     * 角色id
     */
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 菜单id
     */
    @Column(name = "menu_id")
    private Long menuId;

}
