package com.z.module.ai.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: Engine
 * @Package com/z/module/ai/domain/Engine.java
 * @Description: 记录各平台对接配置信息
 * @date 2024/9/12 15:54
 */
@Entity
@Table(name = "ai_platform_config")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PlatformConfig extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("主键")
    private Long id;

    @Comment("应用凭据")
    private String code;

    @Comment("应用名称")
    private String name;

    @Comment("API Key")
    @Column(name = "api_key")
    private String apiKey;

    @Comment("是否启用")
    @Column(name = "status")
    private boolean status;

    @Comment("应用所属平台地址")
    @Column(name = "base_url", length = 200)
    private String baseUrl;

    @Lob
    @Comment("应用摘要")
    private String remark;

    @Comment("应用图标")
    private String picture;

    // 应用id 百炼等使用
    @Comment("应用ID")
    private String appId;

    // 业务类型, 01: 聊天界面展现智能体, 02: 其它，一般作为接口调用
    @Comment("业务类型")
    private String busType;

    // 引擎类型，目前为dify 01 和百炼: 02，可按需后端动态请求接口
    @Comment("平台类型")
    private String type;
}
