package com.z.module.report.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 报表列信息
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rpt_t_field")
@Data
public class ReportField extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column
    private String reportCode;

    /**
     * @data: 2023/1/4-下午4:07
     * @User: zhao
     * @method:
     * @param null :
     * @return:
     * @Description: 如果多表头，用下划线分割, 一个下划线表示两层表头， 两个表示三层...
     */
    @Column
    private String fieldCode;

    /**
     * @data: 2023/1/4-下午4:07
     * @User: zhao
     * @method:
      * @param null :
     * @return:
     * @Description: 如果多表头，用下划线分割, 一个下划线表示两层表头， 两个表示三层...
     */
    @Column
    private String fieldName;

    /**
     * @data: 2023/1/6-下午11:53
     * @User: zhao
     * @method:
      * @param null :
     * @return:
     * @Description: 配置可编辑列, 如:edit: 'text' 表示可以输入文本
     */
    @Column
    private String edit;

    /**
     * @data: 2023/1/12-下午4:33
     * @User: zhao
     * @method:
      * @param null :
     * @return:
     * @Description: 单元格排列方式。可选值有：left（默认）、center（居中）、right（居右）
     */
    @Column
    private String align;

    /**
     * @data: 2023/2/16-上午11:07
     * @User: zhao
     * @method:
      * @param null :
     * @return:
     * @Description: 增加排序列, 填 asc, desc
     */
    @Column
    private String sort;

    // 行坐标
    private String rowNum;

    // 列坐标
    private String colNum;
}
