package com.z.module.template.api;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * 模板条目 DTO：模块对外契约（api 层）。
 * <p>
 * 只承载展示/传输所需字段，不依赖 JPA 实体，杜绝实体泄漏到 REST 边界。
 */
@Data
public class TemplateItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** 条目名称 */
    private String name;

    /** 条目编码（唯一） */
    private String code;

    /** 备注 */
    private String remark;

    /** 创建时间（auditing 字段，只读露出） */
    private Instant createdDate;
}