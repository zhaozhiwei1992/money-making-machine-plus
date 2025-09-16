package com.z.module.report.service.extractrule.trans;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DivClassFormulaTransformer implements ExtractFormulaTransformer{
    @Override
    public String transform(String expression, Map<String, String> formatMapABC) {
        // 拆分以后可能存在divClass作为一个单元传递
        if (!expression.contains("divClass")) {
            return expression;
        }
        expression = expression.replaceAll("divClass", "#divclass");
        return expression;
    }
}
