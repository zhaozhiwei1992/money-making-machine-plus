package com.z.module.ai.domain.model;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI 模型 DO
 *
 * 默认模型：status 为开启，并且 sort 排序第一
 *
 * @author fansili
 * @since 2024/4/24 19:39
 */
@Entity
@Table(name = "ai_model")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)

public class AiModelDO extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("编号")
    private Long id;

    @Comment("API 秘钥编号")
    @Column(name = "key_id")
    private Long keyId;

    @Comment("模型名称")
    private String name;

    @Comment("模型标志")
    private String model;

    @Comment("平台")
    private String platform;

    @Comment("类型")
    private Integer type;

    @Comment("排序值")
    private Integer sort;

    @Comment("状态")
    private Integer status;

    // ========== 对话配置 ==========

    @Comment("温度参数")
    @Column(name = "temperature")
    private Double temperature;

    @Comment("单条回复的最大 Token 数量")
    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Comment("上下文的最大 Message 数量")
    @Column(name = "max_contexts")
    private Integer maxContexts;

}
