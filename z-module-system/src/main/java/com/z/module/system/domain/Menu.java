package com.z.module.system.domain;

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
public class Menu extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    @Column(name = "icon_cls")
    private String iconCls;

    @Column(name = "ordernum")
    private Integer ordernum;

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

}
