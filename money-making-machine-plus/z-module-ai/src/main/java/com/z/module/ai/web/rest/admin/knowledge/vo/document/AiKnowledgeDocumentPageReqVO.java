package com.z.module.ai.web.rest.admin.knowledge.vo.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI 知识库文档的分页 Request VO")
@Data
public class AiKnowledgeDocumentPageReqVO {

    @Schema(description = "知识库编号", example = "1")
    private Long knowledgeId;

    @Schema(description = "文档名称", example = "Java 开发手册")
    private String name;

}
