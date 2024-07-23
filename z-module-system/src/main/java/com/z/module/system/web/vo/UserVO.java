package com.z.module.system.web.vo;

import lombok.Data;

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

    private boolean activated = false;

    private String imageUrl;

    // 所属岗位名称
    private String positionName;

    // 所属部门名称
    private String departmentName;

    // 角色id集合
    private List<Long> roleIdList;

    // 岗位id集合
    private List<Long> positionIdList;

    // 部门id集合
    private List<Long> departmentIdList;

}
