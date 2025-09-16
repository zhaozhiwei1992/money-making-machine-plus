package com.z.module.ai.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Title: Engine
 * @Package com/z/module/ai/domain/Engine.java
 * @Description: 记录所有的ai引擎, 包括大语言模型, 工具模型等
 * @author zhaozhiwei
 * @date 2024/9/12 15:54
 * @version V1.0
 */
@Entity
@Table(name = "ai_engine")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Engine extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 引擎名称, 如:ChatGPT, 通义千问等
    private String name;

    @Column(name = "api_key")
    private String apiKey;

    // 0: 大语言模型(搜索聊天页面使用) 1:工具模型等(如文生图等, 待定)
    @Column(name = "type")
    private int type;

    // 是否启用
    @Column(name = "status")
    private boolean status;

}
