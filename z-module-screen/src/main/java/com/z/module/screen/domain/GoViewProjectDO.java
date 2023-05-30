package com.z.module.screen.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Table;

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
