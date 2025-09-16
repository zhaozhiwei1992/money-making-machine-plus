package com.z.module.report.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 报表公式配置
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rpt_t_formula")
@Data
public class ReportFormula extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String reportCode;

    private String fieldCode;

    private String itemCode;

    // 公式
    private String formulaExp;

    // 公式描述
    private String formulaDesc;

    private String formulaType;

}
