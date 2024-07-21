package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Title: SysPermission
 * @Package com/z/module/system/domain/SysPermission.java
 * @Description: 权限配置表, 数据权限(type:1)和其它
 * @author zhaozhiwei
 * @date 2024/7/21 上午1:07
 * @version V1.0
 */
@Entity
@Table(name = "ele_union")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Permission extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private Integer type;
    private String description;
}