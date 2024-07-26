package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 通知公告信息存储
 */
@Schema(description = "通知公告和用户关联关系")
@Data
@Entity
@Table(name = "sys_user_notice")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserNotice extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "notice_id", nullable = false)
    private Long noticeId;

    @Column(name = "read_flag", nullable = false)
    private boolean readFlag;
}
