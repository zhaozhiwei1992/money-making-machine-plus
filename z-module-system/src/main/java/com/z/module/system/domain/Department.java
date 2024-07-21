package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name = "sys_departments")
public class Department extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer parentId;

    @NotEmpty(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过50")
    private String name;

    private Integer orderNum;

    private String leader;

    private String phone;

    private String email;

    @Column(name = "status", length = 1, nullable = false)
    @NotEmpty(message = "状态不能为空")
    private String status;
}