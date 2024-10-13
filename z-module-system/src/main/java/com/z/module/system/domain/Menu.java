package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import com.z.module.system.domain.listener.MenuListener;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * A Menu.
 */
@Entity
@Table(name = "sys_menu")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
// 简单粗暴注解, 前端都是下划线, 后端都用驼峰
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@EntityListeners(MenuListener.class)
public class Menu extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    @Column(name = "icon_cls")
    private String iconCls;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "keep_alive")
    private Boolean keepAlive;

    @Column(name = "require_auth")
    private Boolean requireAuth;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "config")
    private String config;

    // 路由相关
    @Column(name = "component")
    private String component;

    // 动态ui页面使用, 指定模板, 不指定默认为TemplateDefault
    private String template;

    // 菜单类型 1:菜单 2:按钮
    @Column(name = "menu_type")
    private Integer menuType;

    // system:user:view
    @Column(name = "permission_code")
    private String permissionCode;

}
