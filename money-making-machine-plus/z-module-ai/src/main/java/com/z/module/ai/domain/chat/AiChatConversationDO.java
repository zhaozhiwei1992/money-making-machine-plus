package com.z.module.ai.domain.chat;

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
import java.time.LocalDateTime;

/**
 * AI Chat 对话 DO
 *
 * 用户每次发起 Chat 聊天时，会创建一个 AiChatConversationDO 对象，将它的消息关联在一起
 *
 * @author fansili
 * @since 2024/4/14 17:35
 */
@Entity
@Table(name = "ai_chat_conversation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)

public class AiChatConversationDO extends AbstractAuditingEntity implements Serializable {

    public static final String TITLE_DEFAULT = "新对话";

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("ID 编号")
    private Long id;

    @Comment("用户编号")
    private Long userId;

    @Comment("对话标题")
    private String title;

    @Comment("是否置顶")
    @Column(name = "pinned")
    private Boolean pinned;

    @Comment("置顶时间")
    @Column(name = "pinned_time")
    private LocalDateTime pinnedTime;

    @Comment("角色编号")
    @Column(name = "role_id")
    private Long roleId;

    @Comment("模型编号")
    @Column(name = "model_id")
    private Long modelId;

    @Comment("模型标志")
    private String model;

    // ========== 对话配置 ==========

    @Comment("角色设定")
    @Column(name = "system_message")
    private String systemMessage;

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
