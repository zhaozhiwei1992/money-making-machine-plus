package com.z.module.report.service.checkrule.trans;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Title: ModFormulaTransformer
 * @Package com/z/module/report/service/rule/trans/ModFormulaTransformer.java
 * @Description: 取余
 * @author zhao
 * @date 2024/10/23 14:24
 * @version V1.0
 */
@Component
public class ModFormulaTransformer implements CheckFormulaTransformer{
    @Override
    public String transform(String expression, Map<String, String> formatMapABC) {
        if (!expression.contains("MOD")) {
            return expression;
        }
        expression = expression.replaceAll("MOD", "#mod");
        return expression;
    }
}
