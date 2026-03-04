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
 * AI API 秘钥 DO
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "ai_api_key")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)

public class AiApiKeyDO extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("编号")
    private Long id;

    @Comment("名称")
    private String name;

    @Comment("密钥")
    @Column(name = "api_key")
    private String apiKey;

    @Comment("平台")
    private String platform;

    @Comment("API 地址")
    private String url;

    @Comment("状态")
    private Integer status;

}
