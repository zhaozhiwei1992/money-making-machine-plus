package com.z.module.ui.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 页签配置
 * @author zhaozhiwei
 */
@Entity
@Table(name = "ui_t_tab")
@Data
public class UiTab extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
