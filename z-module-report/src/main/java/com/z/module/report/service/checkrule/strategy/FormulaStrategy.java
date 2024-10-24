package com.z.module.report.service.checkrule.strategy;

import com.z.module.report.domain.ReportFormula;

import java.util.Map;

/**
 * @Title: FormulaStrategy
 * @Package gov/mof/fasp2/gfbi/common/rule/strategy/FormulaStrategy.java
 * @Description: 增加公式执行策略, 特殊公式处理
 * @author zhao
 * @date 2024/7/9 上午9:23
 * @version V1.0
 */
public interface FormulaStrategy {
    boolean validate(ReportFormula ruleMap);
}
