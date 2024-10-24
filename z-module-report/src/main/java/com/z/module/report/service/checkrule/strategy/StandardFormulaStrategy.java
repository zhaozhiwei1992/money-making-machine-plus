package com.z.module.report.service.checkrule.strategy;

import com.z.module.report.domain.ReportFormula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StandardFormulaStrategy extends AbstractFormulaStrategy {

    private static final Logger logger = LoggerFactory.getLogger(StandardFormulaStrategy.class);

    @Override
    public boolean validate(ReportFormula ruleMap) {
        String formulaExp = String.valueOf(ruleMap.getFormulaExp()).toLowerCase();
        String formulaContentReplace = removeFormulaSpeChar(formulaExp);
        // <> 号需要替换为!= 才能使用
        formulaContentReplace = formulaContentReplace.replaceAll("<>", "!=");
        logger.debug("校验公式括号替换, 原: {}, 新: {}", formulaExp, formulaContentReplace);
        return Boolean.TRUE.equals(getParser().parseExpression(formulaContentReplace).getValue(getStandardEvaluationContext(),
                Boolean.class));
    }
}