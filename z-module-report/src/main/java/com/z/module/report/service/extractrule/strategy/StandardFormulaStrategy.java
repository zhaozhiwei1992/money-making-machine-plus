package com.z.module.report.service.extractrule.strategy;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

public class StandardFormulaStrategy extends AbstractFormulaStrategy {

    @Override
    public BigDecimal calculate(Map<String, Object> ruleMap) {
        final StandardEvaluationContext standardEvaluationContext = getStandardEvaluationContext();
        String formulaExp = String.valueOf(ruleMap.get("formula_exp")).toLowerCase();
        return Objects.requireNonNull(getParser().parseExpression(formulaExp).getValue(standardEvaluationContext,
                        BigDecimal.class)).setScale(2, RoundingMode.HALF_UP);
    }
}