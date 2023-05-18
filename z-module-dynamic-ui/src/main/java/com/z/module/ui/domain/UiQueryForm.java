package com.z.module.ui.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * A UiQueryform.
 */
@Entity
@Table(name = "ui_t_queryform")
@Data
public class UiQueryForm extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @Column(name = "requirement")
    private Boolean requirement;

    @Column(name = "type")
    private String type;

    @Column(name = "placeholder")
    private String placeholder;

    @Column(name = "config")
    private String config;
}
