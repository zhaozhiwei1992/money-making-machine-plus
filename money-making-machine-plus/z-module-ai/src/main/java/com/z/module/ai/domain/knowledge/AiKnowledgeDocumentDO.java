package com.z.module.ai.domain.knowledge;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI 知识库-文档 DO
 *
 * @author xiaoxin
 */
@Entity
@Table(name = "ai_knowledge_document")
@Data
@ToString(callSuper = true)

public class AiKnowledgeDocumentDO extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("编号")
    private Long id;

    @Comment("知识库编号")
    @Column(name = "knowledge_id")
    private Long knowledgeId;

    @Comment("文档名称")
    private String name;

    @Comment("文件 URL")
    private String url;

    @Lob
    @Comment("内容")
    private String content;

    @Comment("文档长度")
    @Column(name = "content_length")
    private Integer contentLength;

    @Comment("文档 token 数量")
    private Integer tokens;

    @Comment("分片最大 Token 数")
    @Column(name = "segment_max_tokens")
    private Integer segmentMaxTokens;

    @Comment("召回次数")
    @Column(name = "retrieval_count")
    private Integer retrievalCount;

    @Comment("状态")
    private Integer status;

}
