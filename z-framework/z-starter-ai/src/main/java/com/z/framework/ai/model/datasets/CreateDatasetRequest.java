package com.z.framework.ai.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建知识库请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDatasetRequest {
    /**
     * 知识库名称（必填）
     */
    private String name;

    /**
     * 知识库描述（选填）
     */
    private String description;

    /**
     * 索引模式（选填，建议填写）
     * high_quality 高质量
     * economy 经济
     */
    private String indexingTechnique;

    /**
     * 权限（选填，默认 only_me）
     * only_me 仅自己
     * all_team_members 所有团队成员
     * partial_members 部分团队成员
     */
    private String permission;

    /**
     * Provider（选填，默认 vendor）
     * vendor 上传文件
     * external 外部知识库
     */
    private String provider;

    /**
     * 外部知识库 API_ID（选填）
     */
    private String externalKnowledgeApiId;

    /**
     * 外部知识库 ID（选填）
     */
    private String externalKnowledgeId;
}
