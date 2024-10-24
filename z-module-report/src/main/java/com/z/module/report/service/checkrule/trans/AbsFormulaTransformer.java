package com.z.module.report.service.checkrule.trans;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AbsFormulaTransformer implements CheckFormulaTransformer{
    @Override
    public String transform(String expression, Map<String, String> formatMapABC) {
        if (!expression.contains("ABS")) {
            return expression;
        }
        expression = expression.replaceAll("ABS", "#abs");
        return expression;
    }
}
