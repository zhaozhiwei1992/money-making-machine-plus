package com.z.module.system.web.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Title: null.java
 * @Package: com.z.module.system.web.vo
 * @Description: 登录成功后返回信息, 适配vue3-element-admin
 * 参考mock数据
 * const List: {
 * username: string
 * password: string
 * role: string
 * roleId: string
 * permissions: string | string[]
 * }[] = [
 * {
 * username: 'admin',
 * password: 'admin',
 * role: 'admin',
 * roleId: '1',
 * permissions: ['*.*.*']
 * },
 * @author: zhaozhiwei
 * @date: 2023/4/21 下午1:51
 * @version: V1.0
 */

@Data
@ToString
public class AuthedRespVO {
    private String username;
    private String role;
    private String roleId;
    private List<String> permissions;
    private String token;
    private String avatar;
}
