package com.z.module.ai.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Title: History
 * @Package com/z/module/ai/domain/History.java
 * @Description: 记录当前用户所有的搜索记录, 前台按照id倒排
 * @author zhaozhiwei
 * @date 2024/9/12 15:55
 * @version V1.0
 */
@Entity
@Table(name = "ai_history")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class History extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 历史摘要信息
    private String remark;

    // 使用的哪个引擎
    private Long engineId;

}
