package com.z.framework.common.web.rest.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "通用响应结果")
@Data
public class CommonResult<T> implements Serializable {

    @Schema(description = "业务状态码", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer code;

    @Schema(description = "返回消息", requiredMode = Schema.RequiredMode.REQUIRED)
    private String msg;

    @Schema(description = "返回数据")
    private T data;

    public CommonResult() {
    }

    public CommonResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public CommonResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "操作成功", data);
    }

    public static <T> CommonResult<T> success() {
        return new CommonResult<>(200, "操作成功");
    }

    public static <T> CommonResult<T> error(Integer code, String msg) {
        return new CommonResult<>(code, msg);
    }

    public static <T> CommonResult<T> error(String msg) {
        return new CommonResult<>(500, msg);
    }

    public static <T> CommonResult<T> error(Integer code) {
        return new CommonResult<>(code, "操作失败");
    }

}
