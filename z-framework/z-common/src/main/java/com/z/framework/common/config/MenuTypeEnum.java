package com.z.framework.common.config;

import lombok.Getter;

/**
 * @Title: MenuTypeEnum
 * @Package com/z/framework/common/config/MenuTypeEnum.java
 * @Description: 菜单类型: 1:菜单  2:按钮
 * @author zhaozhiwei
 * @date 2024/7/21 上午11:31
 * @version V1.0
 */
@Getter
public enum MenuTypeEnum {

    DICT(0,"目录"),
    MENU(1,"菜单"),
    FUNC(2,"按钮");

    private final Integer code;
    private final  String msg;

    MenuTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
