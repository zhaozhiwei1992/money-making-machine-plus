package com.z.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 *
 * @author zhaozhiwei
 */
@Getter
@AllArgsConstructor
public enum CommonStatusEnum {

    /**
     * 启用
     */
    ENABLE(0, "启用"),
    /**
     * 禁用
     */
    DISABLE(1, "禁用");

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    /**
     * 判断是否开启
     *
     * @param status 状态
     * @return 是否开启
     */
    public static boolean isEnabled(Integer status) {
        return ENABLE.getStatus().equals(status);
    }

    /**
     * 判断是否禁用
     *
     * @param status 状态
     * @return 是否禁用
     */
    public static boolean isDisable(Integer status) {
        return DISABLE.getStatus().equals(status);
    }

    /**
     * 判断是否禁用
     *
     * @param status 状态
     * @return 是否禁用
     */
    public static boolean isEnable(Integer status) {
        return ENABLE.getStatus().equals(status);
    }

}
