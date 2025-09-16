package com.z.module.system.web.vo;

import lombok.Data;

import jakarta.persistence.Column;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: UserVM
 * @Package com/longtu/web/vm/UserVM.java
 * @Description: 前后端交互User转换
 * @date 2022/7/27 下午3:19
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private String login;

    private String password;

    private String name;

    private String password2;

    private boolean activated = true;

    private String imageUrl;

    // 角色id集合
    private String roleIdListStr;

    // 岗位id集合
    private String positionIdListStr;

    // 部门id集合
    private String departmentIdListStr;

    private String email;

    private String phoneNumber;

    private Long avatar;
}
