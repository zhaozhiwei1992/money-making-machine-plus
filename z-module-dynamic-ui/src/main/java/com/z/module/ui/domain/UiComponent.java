package com.z.module.ui.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * A UiComponent.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ui_t_component")
@Data
public class UiComponent extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "order_num")
    private Integer orderNum;

    /**
     * 组件名称: 跟组件文件名对应, 如xx.vue
     */
    @Column(name = "component")
    private String component;

    /**
     * 自定义名称, 可能存在多个列表, 分别用不同的名字
     */
    private String name;

    /**
     * 扩展配置
     */
    @Column(name = "config")
    private String config;

}
