package com.z.module.report.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 报表信息
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rpt_t_report")
@Data
public class Report extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_num")
    private Integer orderNum;

    private String reportCode;

    private String reportName;

    /**
     * @data: 2023/1/4-下午4:01
     * @User: zhao
     * @method:
      * @param null :
     * @return:
     * @Description: 报表对应数据来源表
     */
    private String dataSource;

    /**
     * @data: 2023/1/4-下午4:00
     * @User: zhao
     * @method:
      * @param null :
     * @return:
     * @Description: 一个类型可以包含多个表,如007草案下可以有很多表
     */
    private String type;

}
