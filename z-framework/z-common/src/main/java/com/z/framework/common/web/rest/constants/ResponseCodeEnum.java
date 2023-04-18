package com.z.framework.common.web.rest.constants;

/**
 * @Title: ResponseCodeEnum
 * @Package gov/mof/fasp2/pay/remote/rest/constants/ResponseCodeEnum.java
 * @Description: 对外提供接口业务规范报文标识
 * @author zhaozhiwei
 * @date 2021/7/2 下午2:16
 * @version V1.0
 */
public enum ResponseCodeEnum {

    /**
     * rest请求反馈状态标识, 具体见msg
     */
    SUCCESS("200000","成功"),
    FAILED("400001","失败"),
    SYS_PARAM_NOT_RIGHT("500002","请求参数错误"),
    TOKEN_EXPIRE("500003","token过期"),
    SYSTEM_BUSY("500004","系统繁忙，请稍候重试");

    private final String code;
    private final  String msg;

    ResponseCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
