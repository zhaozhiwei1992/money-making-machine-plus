package com.z.framework.operatelog.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 慢sql记录\n@author zhaozhiwei
 */
@Entity
@Table(name = "sys_slow_sql_log")
@Data
public class SlowSqlLogging extends AbstractAuditingEntity implements Serializable {

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
     * 当前时间
     */
    @Column(name = "jhi_current_time")
    private String currentTime;

    /**
     * 完整sql
     */
    @Column(name = "jhi_sql")
    private String sql;

    /**
     * 运行时间
     */
    @Column(name = "execute_millis")
    private String executeMillis;

    /**
     * 运行时涉及的参数
     */
    @Column(name = "execute_params")
    private String executeParams;

}
