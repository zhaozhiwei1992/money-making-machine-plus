package com.z.module.system.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.z.framework.common.domain.AbstractAuditingEntity;
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
// 简单粗暴注解, 前端都是下划线, 后端都用驼峰
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
