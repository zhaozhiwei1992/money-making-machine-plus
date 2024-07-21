package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: RolePermission
 * @Package com/z/module/system/domain/RolePermission.java
 * @Description: 用户和部门关系(1对多)
 * @date 2024/7/21 上午1:09
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user_permission")
@Data
public class UserDepartment extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 岗位id
     */
    @Column(name = "dept_id")
    private Long deptId;

}
