package com.z.module.screen.web.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;

@Schema(description = "GoView 项目创建 Request VO")
@Data
public class GoViewProjectCreateReqVO {

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "项目名称不能为空")
    private String projectName;

}
