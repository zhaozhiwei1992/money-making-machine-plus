package com.z.module.report.service.checkrule.strategy;

import com.z.module.report.domain.ReportFormula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SqlFormulaStrategy extends AbstractFormulaStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SqlFormulaStrategy.class);

    @Override
    public boolean validate(ReportFormula ruleMap) {
        // sql校验公式兼容, i > 0 则表示通过
        // 注: 特殊情况使用,否则影响性能
        String formulaExpSql = String.valueOf(ruleMap.getFormulaExp());
        formulaExpSql = formulaExpSql.replaceAll("（", "(").replaceAll("）", ")");
        // 变量替换
        final Map<String, Object> replaceMap = new HashMap<>();
//        replaceMap.put("#param1", ruleMap.get("param1"));
        logger.info("跨表select脚本原始公式：{}", formulaExpSql);
        for (Map.Entry<String, Object> entry : replaceMap.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            formulaExpSql = formulaExpSql.replaceAll(key, String.valueOf(value));
        }
        logger.info("跨表select脚本公式替换结果：{}", formulaExpSql);
        final int i = this.getCommonSqlRepository().queryForInt(formulaExpSql);
        // 不满足条件则返回小于1, 为false
        return i >= 1;
    }
}