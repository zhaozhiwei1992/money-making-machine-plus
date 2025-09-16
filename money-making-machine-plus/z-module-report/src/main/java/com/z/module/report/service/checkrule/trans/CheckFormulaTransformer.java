package com.z.module.report.service.checkrule.trans;

import java.util.Map;

/**
 * @Title: CheckFormulaTransformer
 * @Package com/z/module/report/service/rule/trans/CheckFormulaTransformer.java
 * @Description: 公式转换接口，用户前台录入公式转换为程序可解析形式
 * 最好是公式使用时再进行解析，如果保存时解析那么修改时候还得逆向回转
 * @author zhao
 * @date 2024/10/23 14:02
 * @version V1.0
 */
public interface CheckFormulaTransformer {
    String transform(String expression, Map<String, String> formatMapABC);
}