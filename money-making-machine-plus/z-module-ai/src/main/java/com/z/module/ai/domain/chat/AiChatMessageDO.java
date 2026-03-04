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
import org.springframework.ai.chat.messages.MessageType;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI Chat 消息 DO
 *
 * @since 2024/4/14 17:35
 */
@Entity
@Table(name = "ai_chat_message")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)

public class AiChatMessageDO extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("编号")
    private Long id;

    @Comment("对话编号")
    @Column(name = "conversation_id")
    private Long conversationId;

    @Comment("回复消息编号")
    @Column(name = "reply_id")
    private Long replyId;

    @Comment("消息类型")
    private String type;

    @Comment("用户编号")
    private Long userId;

    @Comment("角色编号")
    @Column(name = "role_id")
    private Long roleId;

    @Comment("模型标志")
    private String model;

    @Comment("模型编号")
    @Column(name = "model_id")
    private Long modelId;

    @Lob
    @Comment("聊天内容")
    private String content;

    @Lob
    @Comment("推理内容")
    @Column(name = "reasoning_content")
    private String reasoningContent;

    @Comment("是否携带上下文")
    @Column(name = "use_context")
    private Boolean useContext;

    @Comment("知识库段落编号数组")
    @Column(name = "segment_ids")
    private String segmentIds;

    @Lob
    @Comment("联网搜索的网页内容数组")
    @Column(name = "web_search_pages")
    private String webSearchPages;

    @Comment("附件 URL 数组")
    @Column(name = "attachment_urls")
    private String attachmentUrls;

}
