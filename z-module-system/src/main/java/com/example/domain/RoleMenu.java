package com.example.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 角色对菜单\n@author zhaozhiwei
 */
@Entity
@Table(name = "sys_role_menu")
@Data
public class RoleMenu extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
