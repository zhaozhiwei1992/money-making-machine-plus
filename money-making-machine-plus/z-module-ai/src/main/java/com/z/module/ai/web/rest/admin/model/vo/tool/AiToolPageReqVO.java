package com.z.module.ai.web.rest.admin.model.vo.tool;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.z.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - AI 工具分页 Request VO")
@Data
public class AiToolPageReqVO {

    @Schema(description = "工具名称", example = "王五")
    private String name;

    @Schema(description = "工具描述", example = "你猜")
    private String description;

    @Schema(description = "状态", example = "1")
    
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}