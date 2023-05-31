package com.z.module.screen.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "GoView 项目 Response VO")
@Data
public class GoViewProjectRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18993")
    private Long id;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String projectName;

    /**
     * 项目状态:\
     * -1: 未发布\
     * 1: 已发布
     */
    @Schema(description = "发布状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer state;

    @Schema(description = "报表内容") // JSON 格式
    private String content;

    @Schema(description = "预览图片 URL", example = "")
    private String indexImage;

    @Schema(description = "项目备注", example = "你猜")
    private String remarks;

    @Schema(description = "创建人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String createUserId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
