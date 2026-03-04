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
 * AI 知识库-文档分段 DO
 *
 * @author xiaoxin
 */
@Entity
@Table(name = "ai_knowledge_segment")
@Data
@ToString(callSuper = true)

public class AiKnowledgeSegmentDO extends AbstractAuditingEntity implements Serializable {

    /**
     * 向量库的编号 - 空值
     */
    public static final String VECTOR_ID_EMPTY = "";

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

    @Comment("文档编号")
    @Column(name = "document_id")
    private Long documentId;

    @Lob
    @Comment("切片内容")
    private String content;

    @Comment("切片内容长度")
    @Column(name = "content_length")
    private Integer contentLength;

    @Comment("向量库的编号")
    @Column(name = "vector_id")
    private String vectorId;

    @Comment("token 数量")
    private Integer tokens;

    @Comment("召回次数")
    @Column(name = "retrieval_count")
    private Integer retrievalCount;

    @Comment("状态")
    private Integer status;

}
