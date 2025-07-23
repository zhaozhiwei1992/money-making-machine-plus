package com.z.framework.operatelog.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户请求日志信息\n@author zhaozhiwei
 */
@Entity
@Table(name = "sys_request_log")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RequestLog extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    /**
     * 请求唯一id, 方便问题定位
     */
    @Column(name = "trace_id")
    private String traceId;

    /**
     * 用户名
     */
    @Column(name = "login_name")
    private String loginName;

    /**
     * 请求地址
     */
    @Column(name = "request_uri")
    private String requestURI;

    /**
     * 请求方式 GET POST DELETE等
     */
    @Column(name = "request_method")
    private String requestMethod;

    /**
     * 请求的接口中文描述
     */
    @Column(name = "request_name")
    private String requestName;

    /**
     * 客户端ip
     */
    @Column(name = "client_ip")
    private String clientIp;

    /**
     * 存储请求参数信息
     */
    @Column(name = "params", length = 2000)
    private String params;

    /**
     * @data: 2022/7/26-下午3:19
     * @User: zhaozhiwei
     * @method:
      * @param null :
     * @return:
     * @Description: 是否请求成功
     */
    @Column(name = "success")
    private String success;
}
