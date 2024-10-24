package com.z.module.report.service.checkrule.trans;

import com.z.module.report.service.extractrule.trans.ExtractFormulaTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IfFormulaTransformer implements CheckFormulaTransformer, ExtractFormulaTransformer {
    @Override
    public String transform(String expression, Map<String, String> formatMapABC) {
        if (!expression.contains("IF")) {
            return expression;
        }
        expression = expression.replaceAll("IF", "#xif");
        return expression;
    }
}
