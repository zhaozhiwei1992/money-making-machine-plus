package com.z.module.screen.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "GoView 项目更新 Request VO")
@Data
public class GoViewProjectUpdateReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18993")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String projectName;

    @Schema(description = "发布状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer state;

    @Schema(description = "预览图片 URL")
    private String indexImage;

    @Schema(description = "项目备注", example = "你猜")
    private String remarks;

    @Schema(description = "报表id")
    private Long projectId;

    // 项目明细信息
    @Schema(description = "报表内容")
    private String content;

}
