package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 通知公告信息存储
 */
@Schema(description = "通知公告信息")
@Data
@Entity
@Table(name = "sys_notice")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Notice extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    /**
     * 标题
     */
    @Schema(description = "标题")
    @Column(name = "title")
    private String title;

    /**
     * 公告内容
     */
    @Schema(description = "公告内容")
    @Column(name = "content")
    private String content;

    /**
     * 选择不同的类型，　使用不同的接收者值集
     * 所有人
     * 按用户/显示所有用户
     * 按角色/显示所有角色
     * 按单位/显示所有单位
     * 下拉值集通过 cascade展现, 显示末级
     */
    @Schema(description = "可以是 所有人/单个或多个用户/角色/单位\n选择不同的类型，　使用不同的接收者值集")
    @Column(name = "rec_type")
    private String recType;

    /**
     * 紧急程度 (紧急的同时短信通知) (是/否)
     */
    @Column(name = "urgent")
    private Boolean urgent;

    /**
     * 通知类型 通知公告/规章制度/政策文件
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 通知状态
     * true 发布
     * false 未发布
     */
    @Column(name = "status", nullable = false)
    private boolean status;

}
