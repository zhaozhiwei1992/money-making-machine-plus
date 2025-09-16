package com.z.module.bpm.domain.definition;

import com.z.framework.common.domain.AbstractAuditingEntity;
import com.z.module.bpm.domain.convert.JpaSetLongJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Bpm 用户组
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "bpm_user_group")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmUserGroupDO extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    /**
     * 组名
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 成员用户编号数组
     */
//    @TableField(typeHandler = JsonLongSetTypeHandler.class)
    @Convert(converter = JpaSetLongJsonConverter.class)
    private Set<Long> memberUserIds;

}
