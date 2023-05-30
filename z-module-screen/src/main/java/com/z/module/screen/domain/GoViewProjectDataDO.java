package com.z.module.screen.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.*;
import lombok.experimental.Accessors;
import org.jeecgframework.minidao.annotation.id.TableId;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Title: GoViewProjectDataDO
 * @Package com/z/module/screen/domain/GoViewProjectDataDO.java
 * @Description: 项目数据信息
 * @author zhaozhiwei
 * @date 2023/5/30 下午11:11
 * @version V1.0
 */
//@Table(name = "t_go_view_project_data")
//@Data
//@Entity
//@EqualsAndHashCode(callSuper = true)
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Accessors(chain = true)
public class GoViewProjectDataDO extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    private String projectId;

    private String content;
}