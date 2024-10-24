package com.z.module.report.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 报表列信息
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rpt_t_data")
@Data
public class ReportData extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String reportCode;
    private String fieldCode;
    private String fieldName;
    private String itemCode;
    private String itemName;

    private BigDecimal je1;
    private BigDecimal je2;
    private BigDecimal je3;
    private BigDecimal je4;
    private BigDecimal je5;
    private BigDecimal je6;
    private BigDecimal je7;
    private BigDecimal je8;
    private BigDecimal je9;
    private BigDecimal je10;

}
