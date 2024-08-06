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
 * @Description: 用户和岗位关系(多对多),一个用户可以有多个岗位
 * @date 2024/7/21 上午1:09
 */
@Entity
@Table(name = "sys_user_position")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPosition extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 岗位id
     */
    @Column(name = "position_id")
    private Long positionId;

}
