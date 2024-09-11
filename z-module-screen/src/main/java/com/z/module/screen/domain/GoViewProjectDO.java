package com.z.module.screen.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * GoView 项目表
 * 每个大屏图表，对应一个项目
 * @author zhaozhiwei
 * @author 芋道源码
 */
@Table(name = "t_go_view_project")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GoViewProjectDO extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 项目名称
     */
    private String name;
    /**
     * 预览图片 URL
     */
    private String picUrl;
    /**
     * 报表内容
     *
     * JSON 配置，使用字符串存储
     */
    @Lob
//    @Basic(fetch = FetchType.LAZY)
    private String content;
    /**
     * 发布状态
     *
     * 0 - 已发布
     * 1 - 未发布
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    /**
     * 项目备注
     */
    private String remark;
}
