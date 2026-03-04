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
 * AI 知识库 DO
 *
 * @author xiaoxin
 */
@Entity
@Table(name = "ai_knowledge")
@Data
@ToString(callSuper = true)

public class AiKnowledgeDO extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("编号")
    private Long id;

    @Comment("知识库名称")
    private String name;

    @Comment("知识库描述")
    private String description;

    @Comment("向量模型编号")
    @Column(name = "embedding_model_id")
    private Long embeddingModelId;

    @Comment("模型标识")
    @Column(name = "embedding_model")
    private String embeddingModel;

    @Comment("topK")
    private Integer topK;

    @Comment("相似度阈值")
    @Column(name = "similarity_threshold")
    private Double similarityThreshold;

    @Comment("状态")
    private Integer status;

}
