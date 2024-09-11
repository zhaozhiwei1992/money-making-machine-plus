package com.z.module.ui.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * A UiToolButton.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ui_t_toolbutton")
@Data
public class UiToolButton extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "action")
    private String action;

    @Column(name = "config")
    private String config;

    @Column(name = "permission_code")
    private String permissionCode;
}
