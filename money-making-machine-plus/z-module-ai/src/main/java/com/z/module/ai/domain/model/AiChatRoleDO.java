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
import java.util.List;

/**
 * AI 聊天角色 DO
 *
 * @author fansili
 * @since 2024/4/24 19:39
 */
@Entity
@Table(name = "ai_chat_role")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)

public class AiChatRoleDO extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("编号")
    private Long id;

    @Comment("角色名称")
    private String name;

    @Comment("角色头像")
    private String avatar;

    @Comment("角色分类")
    private String category;

    @Comment("角色描述")
    private String description;

    @Comment("角色设定")
    private String systemMessage;

    @Comment("用户编号")
    private Long userId;

    @Comment("模型编号")
    private Long modelId;

    @Comment("引用的知识库编号列表")
    @Column(name = "knowledge_ids")
    private String knowledgeIds;

    @Comment("引用的工具编号列表")
    @Column(name = "tool_ids")
    private String toolIds;

    @Comment("引用的 MCP Client 名字列表")
    @Column(name = "mcp_client_names")
    private String mcpClientNames;

    @Comment("是否公开")
    @Column(name = "public_status")
    private Boolean publicStatus;

    @Comment("排序值")
    private Integer sort;

    @Comment("状态")
    private Integer status;

}
