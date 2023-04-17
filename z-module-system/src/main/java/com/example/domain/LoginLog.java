package com.example.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 用户请求日志信息\n@author zhaozhiwei
 */
@Entity
@Table(name = "sys_login_log")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LoginLog extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Column(name = "login_name")
    private String loginName;

    /**
     * 浏览器
     */
    @Column(name = "browser")
    private String browser;

    /**
     * 使用的操作系统
     */
    @Column(name = "os")
    private String os;

    /**
     * 客户端ip
     */
    @Column(name = "client_ip")
    private String clientIP;
}
