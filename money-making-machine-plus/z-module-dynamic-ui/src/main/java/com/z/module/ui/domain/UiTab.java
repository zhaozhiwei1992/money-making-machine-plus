package com.z.module.ui.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 页签配置
 *
 * @author zhaozhiwei
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ui_t_tab")
@Data
public class UiTab extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "menu_id")
    private Long menuId;

    /**
     * 页签的显示编码, 唯一,根据code业务做自己的处理
     */
    @Column(name = "code")
    private String code;

    /**
     * 页签显示的标题
     */
    @Column(name = "name")
    private String name;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "config")
    private String config;
}
