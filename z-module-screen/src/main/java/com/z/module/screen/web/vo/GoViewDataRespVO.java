package com.z.module.screen.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "GoView 数据 Response VO")
@Data
public class GoViewDataRespVO {

    @Schema(description = "数据维度", requiredMode = Schema.RequiredMode.REQUIRED, example = "['product', 'data1', 'data2']")
    private List<String> dimensions;

    @Schema(description = "数据明细列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Map<String, Object>> source;

}