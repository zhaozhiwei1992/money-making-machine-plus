package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: SysPosition
 * @Package com/z/module/system/domain/SysPosition.java
 * @Description: 岗位
 * @date 2024/7/21 上午2:03
 */
@Data
@Entity
@Table(name = "sys_positions")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Position extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "order_num", nullable = false)
    private Integer orderNum;

    @Column(name = "activated", nullable = false)
    private boolean activated = true;

    @Column(name = "remark", length = 500)
    private String remark;
}