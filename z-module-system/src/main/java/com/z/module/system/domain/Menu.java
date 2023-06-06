package com.z.module.system.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Menu extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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

}
