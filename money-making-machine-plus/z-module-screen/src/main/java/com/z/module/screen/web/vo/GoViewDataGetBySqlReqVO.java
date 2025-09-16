package com.z.module.screen.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "GoView 使用 SQL 查询数据 Request VO")
@Data
public class GoViewDataGetBySqlReqVO {

    @Schema(description = "SQL 语句", requiredMode = Schema.RequiredMode.REQUIRED, example = "SELECT * FROM user")
    @NotEmpty(message = "SQL 语句不能为空")
    private String sql;

}
