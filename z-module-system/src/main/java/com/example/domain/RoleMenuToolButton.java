package com.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 角色,菜单,按钮权限\n@author zhaozhiwei
 */
@Entity
@Table(name = "sys_role_menu_button")
public class RoleMenuToolButton extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    @Column(name = "role_id")
    private String roleId;

    /**
     * 菜单id
     */
    @Column(name = "menu_id")
    private String menuId;

    @Column(name = "tool_button_id")
    private Long toolButtonId;

}
