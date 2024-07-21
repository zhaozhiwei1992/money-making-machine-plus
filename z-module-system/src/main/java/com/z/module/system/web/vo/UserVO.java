package com.z.module.system.web.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Title: UserVM
 * @Package com/longtu/web/vm/UserVM.java
 * @Description: 前后端交互User转换
 * @author zhaozhiwei
 * @date 2022/7/27 下午3:19
 * @version V1.0
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;

    private String password;

    private String name;

    private String password2;

    private boolean activated = false;

    private String imageUrl;

    // 角色id集合
    private List<Long> roleIdList;

    // 岗位id集合
    private List<Long> positionIdList;

    // 部门id集合
    private List<Long> departmentIdList;

}
