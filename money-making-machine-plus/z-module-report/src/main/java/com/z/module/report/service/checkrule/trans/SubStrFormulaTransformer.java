package com.z.module.report.service.checkrule.trans;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Title: SubStrFormulaTransformer
 * @Package com/z/module/report/service/rule/trans/SubStrFormulaTransformer.java
 * @Description: 字符串截取
 * @author zhao
 * @date 2024/10/23 14:01
 * @version V1.0
 */
@Component
public class SubStrFormulaTransformer implements CheckFormulaTransformer{
    @Override
    public String transform(String expression, Map<String, String> formatMapABC) {
        //SUBSTR(mofDivCode,0,2)
        if (!expression.contains("SUBSTR")) {
            return expression;
        }
        expression = expression.replaceAll("SUBSTR", "#substr");
        return expression;
    }
}
