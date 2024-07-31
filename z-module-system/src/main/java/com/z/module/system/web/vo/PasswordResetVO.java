package com.z.module.system.web.vo;

import lombok.Data;

@Data
public class PasswordResetVO {

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;
}
