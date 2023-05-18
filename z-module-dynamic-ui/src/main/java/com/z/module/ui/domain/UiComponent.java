package com.z.module.ui.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * A UiComponent.
 */
@Entity
@Table(name = "ui_t_component")
@Data
public class UiComponent extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "order_num")
    private Integer orderNum;

    /**
     * 自定义组件描述路径
     */
    @Column(name = "component")
    private String component;

    /**
     * 扩展配置
     */
    @Column(name = "config")
    private String config;

}
