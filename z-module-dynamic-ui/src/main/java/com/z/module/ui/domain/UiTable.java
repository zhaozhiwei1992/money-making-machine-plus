package com.z.module.ui.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * A UiTable.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ui_t_table")
@Data
public class UiTable extends AbstractAuditingEntity implements Serializable {

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

    @Column(name = "is_source")
    private Boolean isSource;

    @Column(name = "is_edit")
    private Boolean isEdit;

    @Column(name = "requirement")
    private Boolean requirement;

    @Column(name = "type")
    private String type;

    @Column(name = "config")
    private String config;

}
