package com.z.module.report.service.extractrule.strategy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Getter
public abstract class AbstractFormulaStrategy implements FormulaStrategy {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Setter
    private StandardEvaluationContext standardEvaluationContext;

    /**
     * @param str :
     * @data: 2023/7/20-上午9:37
     * @User: zhao
     * @method: removeFormulaSpeChar
     * @return: java.lang.String
     * @Description: 描述
     * <p>
     * // 公式里报表编码项目编码不能包含特殊符号如 (), 空格等
     * // 函数本身的括号有不能去掉
     */
    protected static String removeFormulaSpeChar(String str) {
        // 如果包含特殊函数，先提取函数内部再替换
        return str.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\s*", "")
                // 特殊处理函数, 用中文括号方便转换
                .replaceAll("（", "(").replaceAll("）", ")");
    }
}