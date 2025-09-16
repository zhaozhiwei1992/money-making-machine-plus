package com.z.module.report.service;

import com.z.module.report.domain.ReportData;
import com.z.module.report.domain.ReportFormula;
import com.z.module.report.repository.ReportDataRepository;
import com.z.module.report.repository.ReportFormulaRepository;
import com.z.module.report.service.extractrule.trans.ExtractFormulaTransformerChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Title: null.java
 * @Package: gov.mof.fasp2.gfbi.common.extractrule
 * @Description: 取数公式服务类
 * @author: zhao
 * @date: 2023/7/18 上午9:46
 * @version: V1.0
 */
@Service
@Slf4j
public class ExtractRuleService {

    private final ReportFormulaRepository reportFormulaRepository;
    private final ReportDataRepository reportDataRepository;

    public ExtractRuleService(ReportFormulaRepository reportFormulaRepository, ReportDataRepository reportDataRepository, CalRuleService calRuleService) {
        this.reportFormulaRepository = reportFormulaRepository;
        this.reportDataRepository = reportDataRepository;
        this.calRuleService = calRuleService;
    }

    /**
     * @param datas      : 待校验数据, 计算结果会回写到相应字段中
     * @param reportCode : 报表编码
     * @param type       : 业务类型1:执行报表, 2:决算
     * @param addFlag    : 新增标识， 新增则表示数据还未入库
     * @date: 2022/11/11-下午5:58
     * @author: zhao
     * @method: mathReportFormulaAll
     * @return: void
     * @Description: 取数结果直接回写到数据中, 业务直接保存即可
     */
    public void extractDataFormula(List<ReportData> datas, String reportCode, String type, boolean addFlag) {

        // 获取取数公式formula_type 2:计算, 1取数, 0:校验
        List<ReportFormula> ruleList = reportFormulaRepository.findAllByReportCodeAndFormulaType(reportCode, "1");
        if (!ruleList.isEmpty()) {

            // 根据规则获取依赖的报表编码
            Set<String> reportCodeList = this.getReportCodesByRuleList(ruleList);
            reportCodeList.add(reportCode);

            String wheresql = " 1=1 ";

            // 适配财政部公式转换, 行列表示, 转换为字段表示
            calRuleService.transFormulaCZB(ruleList, reportCodeList);

            List<ReportData> fullDataList = reportDataRepository.findAllByReportCodeIn(reportCodeList);

            if (addFlag) {
                // 新增时要填充新增数据进来
                fullDataList.addAll(datas);
            } else {
                // 修改时要用前台调整数据覆盖数据库数据
                for (ReportData map : fullDataList) {
                    for (ReportData data : datas) {
                        if (data.getReportCode().equals(map.getReportCode())
                                && data.getItemCode().equals(map.getItemCode())
                        ) {
                            BeanUtils.copyProperties(data, map);
                        }
                    }
                }
            }

            calRuleService.calculation(fullDataList, ruleList);
            //兼容新增修改， report_code和report_item_code相同则表示为同一行数据
            // fullDataList会带有计算结果, 需要反馈到list中
            for (ReportData map : fullDataList) {
                for (ReportData data : datas) {
                    if (data.getReportCode().equals(map.getReportCode())
                            && data.getItemCode().equals(map.getItemCode())
                    ) {
                        BeanUtils.copyProperties(data, map);
                    }
                }
            }
        }
    }

    private final CalRuleService calRuleService;

    @Autowired
    private ExtractFormulaTransformerChain extractFormulaTransformerChain;

    /**
     * @param expression       :
     * @param formatMapABC     :
     * @param reportAbbCodeMap : report_abb_code和report_code对照
     * @data: 2023/7/22-下午2:29
     * @User: zhao
     * @method: replaceCellReferences
     * @return: java.lang.String
     * @Description: 公式替换
     */
    public String doTransFormulaCZB(String expression, Map<String, String> formatMapABC,
                                    Map<String, String> reportAbbCodeMap) {
        //*** extractData_2021_null_L24_null_B58_null_null
        // 外部数据公式转换
        try {
            expression = this.processExtractFormula(expression, formatMapABC, reportAbbCodeMap);
        } catch (Exception e) {
            log.error("外部公式转换异常, 公式: {}", expression);
        }

        expression = extractFormulaTransformerChain.transform(expression, formatMapABC);

        // 处理普通的表达式中的动态单元格引用
        expression = this.calRuleService.processNormalFormula(expression, formatMapABC);

        return expression;
    }

    /**
     * @param expression       :
     * @param formatMapABC     :
     * @param reportAbbCodeMap
     * @data: 2023/7/22-下午3:22
     * @User: zhao
     * @method: processExtractFormula
     * @return: java.lang.String
     * @Description: 外部数据公式适配
     * C28=extractData_2021_null_L17_null_E28_L170023_JE2  L17
     */
    private String processExtractFormula(String expression, Map<String, String> formatMapABC,
                                         Map<String, String> reportAbbCodeMap) {
        if (!expression.contains("extractData_")) {
            return expression;
        }
        StringBuilder newExpression = new StringBuilder();
        final String[] split1 = expression.split("_");
        for (int i = 0; i < split1.length; i++) {
            if (i == 1) {
//              #_YYYY_PREMONTH_J02['J104:JE1']
                newExpression.append("#_").append(split1[i]).append("_MM_");
            } else if (i == 5) {
                // 跨表公式 #要提前, 不然解析会有问题
                newExpression.append(formatMapABC.get(split1[i]).replaceAll("#", ""));
            }
        }
        return newExpression.toString();
    }

    private static final Pattern METHOD_PATTERN = Pattern.compile("(\\w+)\\(([^)]*)\\)");

    /**
     * @param expression :
     * @data: 2023/7/25-下午5:03
     * @User: zhao
     * @method: replaceMethodBracketToZH
     * @return: java.lang.StringBuffer
     * @Description: 函数括号替换为中文括号
     */
    private static StringBuffer replaceMethodBracketToZH(String expression) {
        Matcher matcher = METHOD_PATTERN.matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1) + "（" + matcher.group(2) + "）");
        }
        matcher.appendTail(sb);
        return sb;
    }

    // 财政部公式匹配单元格表达式
    private static final Pattern CZB_FORMULA_COL_ITEM = Pattern.compile("[A-Z]+\\d+");

    public static String processNormalFormula(String expression, Map<String, String> formatMapABC) {
        Matcher matcher = CZB_FORMULA_COL_ITEM.matcher(expression);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String cellRef = matcher.group();
            String replacement = formatMapABC.getOrDefault(cellRef, cellRef);
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static final Pattern JE_PATTERN = Pattern.compile("je[0-9]+");

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * @param str :
     * @data: 2023/7/20-上午9:37
     * @User: zhao
     * @method: removeFormulaSpeChar
     * @return: java.lang.String
     * @Description: 描述
     * <p>
     * // 公式里不能包含特殊符号如 (), 空格等
     */
    private static String removeFormulaSpeChar(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '(':
                case ')':
                case ' ':
                    // Skip these characters
                    break;
                case '（':
                    sb.append('(');
                    break;
                case '）':
                    sb.append(')');
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
        // 如果包含特殊函数，先提取函数内部再替换
//        return str.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\s*", "")
//                // 特殊处理函数, 用中文括号方便转换
//                .replaceAll("（", "(").replaceAll("）", ")");
    }

    /**
     * @param ruleList :
     * @date: 2022/11/7-下午8:31
     * @author: zhao
     * @method: transFormula
     * @return: void
     * @Description: 复合公式转换为基础公式
     * <p>
     * // t01['001:amt03'] = t01['001:amt01'] + t01['001:amt02']
     * // t02['002:amt02'] = t01['001:amt03'] + t02['002:amt01']
     * // t02['002:amt03'] = t02['002:amt01'] + t02['002:amt02']
     */
    private void transFormula(List<Map<String, Object>> ruleList) {
        log.debug("原公式: {}", ruleList);

        // 先将ruleList转换为map, 方便替换
        final Map<String, String> formulaMap = new HashMap<>();
        for (Map<String, Object> rule : ruleList) {
            formulaMap.put("#" + rule.get("report_code") + "['" + rule.get("report_item_code") + ":" + rule.get("field_code") +
                    "']", String.valueOf(rule.get("formula_exp")));
        }

        for (Map<String, Object> rule : ruleList) {
            // 公式原子化
            try {
//                rule.put("formula_exp", replaceFormulaStr(formulaMap, String.valueOf(rule.get("formula_exp"))));
                rule.put("formula_exp", replaceFormulaStr2(formulaMap, String.valueOf(rule.get("formula_exp"))));
            } catch (Throwable e) {
                log.error("公式转换异常", e);
                log.error("存在公式循环依赖, {}", rule.get("formula_exp"));
                throw new RuntimeException("公式转换异常");
            }
        }

        log.debug("转换后公式: {}", ruleList);
    }

    private String replaceFormulaStr(Map<String, String> formula, String newFormulaStr) {
        for (String formulaKey : formula.keySet()) {
            if (newFormulaStr.contains(formulaKey)) {
                // 原子公式: t01['001:amt03'] = #t01['001:amt01'] + #t01['001:amt02']
                // 转换前: t02['002:amt02'] = #t01['001:amt03'] + #t02['002:amt01']
                // 转换后: t02['002:amt02'] = (#t01['001:amt01'] + #t01['001:amt02']) + #t02['002:amt01']
                newFormulaStr = newFormulaStr.replace(formulaKey, "(" + formula.get(formulaKey) + ")");

                // 替换后的可能还存在包含formula的部分, 继续替换
                return this.replaceFormulaStr(formula, newFormulaStr);
            }
        }
        return newFormulaStr;
    }

    /**
     * @param formulaMap    :
     * @param newFormulaStr :
     * @data: 2023/8/4-下午3:32
     * @User: zhao
     * @method: replaceFormulaStr2
     * @return: java.lang.String
     * @Description: 循环方式实现公式转换
     * 可能会导致无限循环，如果存在环形依赖，例如A依赖于B，B依赖于C，C又依赖于A
     */
    private String replaceFormulaStr2(Map<String, String> formulaMap, String newFormulaStr) {
        Queue<String> queue = new LinkedList<>();
        queue.add(newFormulaStr);
        int iterations = 0;
        // 限制最大循环次数, 如果超过2000基本上这个公式有问题, 这里最大可以写报表最大的极限行数
        // 极限情况下, 公式newFormulaStr存在2000个计算单元, 并且每个单元都存在与formulaMap的key中(这是正常情况:D)
        // 如果只有三五个单元, 但是一直循环超过2000那说明g了
        int maxIterations = 2000;

        while (!queue.isEmpty()) {
            if (iterations > maxIterations) {
                throw new RuntimeException("Possible infinite loop detected. Check for cyclic dependencies.");
            }

            String currentFormulaStr = queue.poll();
            boolean isReplaced = false;

            for (Map.Entry<String, String> formula : formulaMap.entrySet()) {
                String formulaKey = formula.getKey();
                if (currentFormulaStr.contains(formulaKey)) {
                    currentFormulaStr = currentFormulaStr.replace(formulaKey, "(" + formula.getValue() + ")");
                    queue.add(currentFormulaStr);
                    isReplaced = true;
                    break;
                }
            }

            if (!isReplaced) {
                return currentFormulaStr;
            }

            iterations++;
        }

        throw new RuntimeException("Unreachable code. This should never happen.");
    }

    /**
     * @param ruleList : 规则列表
     * @data: 2023/7/21-下午2:44
     * @User: zhao
     * @method: getReportCodesByRuleList
     * @return: java.util.Set<java.lang.String>
     * @Description: 提取规则中涉及的报表编码
     */
    public <T extends ReportFormula> Set<String> getReportCodesByRuleList(List<T> ruleList) {
        return calRuleService.getReportCodesByRuleList(ruleList);
    }
}