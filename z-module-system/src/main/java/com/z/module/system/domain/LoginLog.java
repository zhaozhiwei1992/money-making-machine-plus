package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * 用户请求日志信息\n@author zhaozhiwei
 */
@Entity
@Table(name = "sys_login_log")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
// 简单粗暴注解, 前端都是下划线, 后端都用驼峰
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoginLog extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


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
    private String clientIp;

    // 不加这个, hibernate6.5.x下无法处理 com.z.module.system.repository.LoginLogRepository#findAllByYearGroupByMonth
    @Column(name = "created_date", updatable = false)
    private Instant createdDate = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));

    /**
     * 登录成功失败: SUCCESS/FAILURE
     */
    private String result;

    /**
     * 登录失败要备注下原因
     */
    private String remark;
}
