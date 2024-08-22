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
 * @Description: 保存用户login和openId关系, 因为该表变化频繁，独立存放
 * @date 2024/7/21 上午1:09
 */
@Entity
@Table(name = "sys_user_login_openid")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserOpenId extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户login
     */
    private String login;

    /**
     * 微信openId
     */
    @Column(name = "open_id")
    private String openId;

}
