package com.z.module.system.web.vm;

import lombok.Data;

import java.io.Serializable;

/**
 * @Title: UserVM
 * @Package com/longtu/web/vm/UserVM.java
 * @Description: 前后端交互User转换
 * @author zhaozhiwei
 * @date 2022/7/27 下午3:19
 * @version V1.0
 */
@Data
public class UserVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;

    private String password;

    private String name;

    private String password2;

    private boolean activated = false;

    private String imageUrl;

    private String appid;

    private String mofDivCode;

    private String role;
}
