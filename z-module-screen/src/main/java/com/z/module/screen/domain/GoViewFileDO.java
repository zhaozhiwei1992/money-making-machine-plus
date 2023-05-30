package com.z.module.screen.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *
 * </p>
 *
 * @author fc
 * @since 2022-12-22
 */
@Table(name = "t_go_view_file")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GoViewFileDO extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    private String fileName;

    private Integer fileSize;

    private String fileSuffix;

    /**
     * 虚拟路径
     */
    private String virtualKey;

    /**
     * 相对路径
     */
    private String relativePath;

    /**
     * 绝对路径
     */
    private String absolutePath;

}
