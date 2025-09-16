package com.z.module.report.service;

import com.z.framework.common.repository.CommonSqlRepository;
import com.z.module.report.domain.Report;
import com.z.module.report.domain.ReportData;
import com.z.module.report.domain.ReportFormula;
import com.z.module.report.repository.*;
import com.z.module.report.service.checkrule.ext.ELExtMethod;
import com.z.module.report.service.checkrule.strategy.AbstractFormulaStrategy;
import com.z.module.report.service.checkrule.strategy.SqlFormulaStrategy;
import com.z.module.report.service.checkrule.strategy.StandardFormulaStrategy;
import com.z.module.report.service.checkrule.trans.CheckFormulaTransformerChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Title: null.java
 * @Package: gov.mof.fasp2.gfbi.common.rule
 * @Description: 校验规则实现类
 * @author: zhao
 * @date: 2023/2/27 下午2:13
 * @version: V1.0
 */
@Service
@Slf4j
public class CheckRuleService {


    private final ReportFormulaRepository reportFormulaRepository;
    private final ReportItemRepository reportItemRepository;
    private final ReportFieldRepository reportFieldRepository;
    private final ReportDataRepository reportDataRepository;
    private final ReportRepository reportRepository;

    public CheckRuleService(ReportFormulaRepository reportFormulaRepository, ReportItemRepository reportItemRepository, ReportFieldRepository reportFieldRepository, ReportDataRepository reportDataRepository, ReportRepository reportRepository, CalRuleService calRuleService, CommonSqlRepository commonSqlRepository) {
        this.reportFormulaRepository = reportFormulaRepository;
        this.reportItemRepository = reportItemRepository;
        this.reportFieldRepository = reportFieldRepository;
        this.reportDataRepository = reportDataRepository;
        this.reportRepository = reportRepository;
        this.calRuleService = calRuleService;
        this.commonSqlRepository = commonSqlRepository;
    }

    public Map checkReportAllByExpression(String type) {
        final List<Report> currTypeReportList = reportRepository.findAllByType(type);

        final List<String> currTypeReportCodeList =
                currTypeReportList.stream().map(m -> String.valueOf(m.getReportCode())).collect(Collectors.toList());

        // 校验公式, formula_type == '0'
        List<ReportFormula> ruleList = reportFormulaRepository.findAllByReportCodeInAndFormulaType(currTypeReportCodeList, "0");

        Map map = new HashMap();
        map.put("flag", "1");//通过
        if (ruleList.isEmpty()) {
            return map;
        }

        // 涉及表间公式, 将多表数据提取放到list中， 参与校验
        String fullDataSql = "";
        // 公式可能跨类型,获取数据需要跨类提取
        // 获取公式中所有涉及的表(当前类型表+公式涉及外部表), 构建成数据提取sql
        final Set<String> reportCodeListByRule = calRuleService.getReportCodesByRuleList(ruleList);
        List<ReportData> fullDataList = reportDataRepository.findAllByReportCodeIn(reportCodeListByRule);
        if (fullDataList.isEmpty()) {
            log.info("未查到数据, 查询sql: {}", fullDataSql);
            throw new RuntimeException("请先保存！");
        }

        // 适配财政部公式转换, 行列表示, 转换为字段表示
        calRuleService.transFormulaCZB(ruleList, reportCodeListByRule);

        List<Map<String, Object>> errorList = this.checkDataByRule(fullDataList, ruleList);
        if (!errorList.isEmpty()) {
            log.debug("校验失败信息: {}", errorList);
            map.put("flag", "2");
            map.put("data", errorList);
        }
        return map;
    }

    private final CalRuleService calRuleService;

    public void transFormulaCZB(List<ReportFormula> ruleList, Collection<String> reportCodeSet) {

        final Map<String, Map<String, String>> formulaMapABCMap = calRuleService.getCoorFormatMap(reportCodeSet);

        // 获取行列与单元格对照, 不包含!
        for (ReportFormula rule : ruleList) {
            String formulaExp = rule.getFormulaExp();
            // 将所有英文括号转换为中文, 方便后续统一处理
            formulaExp = formulaExp.replaceAll("\\(", "（").replaceAll("\\)", "）");
            StringBuilder newFormulaExp = new StringBuilder();
            if(formulaExp.contains("!")){
                // 校验公式分左右两边, 分别处理 a>b a>=b
                String reg = "(.*)(==|>=|<=|>|<)(.*)";
                final Pattern compile = Pattern.compile(reg);
                final Matcher matcher = compile.matcher(formulaExp);
                if (matcher.find()) {
                    final String leftFormulaExp = matcher.group(1);
                    newFormulaExp.append(splitFormulaWithSymbol(formulaMapABCMap, rule, leftFormulaExp));
                    // 运算符
                    newFormulaExp.append(matcher.group(2));
                    final String rightFormulaExp = matcher.group(3);
                    newFormulaExp.append(splitFormulaWithSymbol(formulaMapABCMap, rule, rightFormulaExp));
                }
            }else{
                String newFormulaExpStr = splitFormulaWithSymbol(formulaMapABCMap, rule, formulaExp);
                newFormulaExp.append(newFormulaExpStr);
            }
            rule.setFormulaExp(newFormulaExp.toString());
            log.info("原公式: {}, 转换后公式: {}", formulaExp, newFormulaExp);
        }
    }

    /**
     * @param formulaMapABCMap :
     * @param rule             :
     * @param formulaExp       :
     * @data: 2023/7/25-上午10:08
     * @User: zhao
     * @method: splitFormulaWithSymbol
     * @return: java.lang.String
     * @Description: 可能存在跨表校验, 左右两边表不一致, 拆分分别处理, 根据 逻辑符号分割后再传入
     * 如: ABS(K42)<=ABS(L10!P26)	应小于等于L10表相关科目数据
     */
    private String splitFormulaWithSymbol(Map<String, Map<String, String>> formulaMapABCMap, ReportFormula rule, String formulaExp) {
        String newFormulaExp = formulaExp;
        if (newFormulaExp.contains("!")) {
            // 跨表: L01!C163 ==> #L01['row':'col']
            // N56<=O56+L10!Z31, 涉及当前表和L10表
            // N56<=O56+abs(L10!Z31), 可能还有函数
            // 提取表格, 获取配置信息,进行转换, 可能涉及多表, 需要使用表达式将带!的单元和普通公式分组
            StringBuffer result = new StringBuffer();
            String regex = "([a-zA-Z0-9]+(-\\d+)?![a-zA-Z0-9]+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(newFormulaExp);
            while (matcher.find()) {
                String matchedPart = matcher.group();
                if (matchedPart.contains("!")) {
                    String[] parts = matchedPart.split("!");
                    final String reportCode = parts[0];
                    String rowCol = parts[1];
                    Map<String, String> formulaMapABC = formulaMapABCMap.get(reportCode);
                    if(Objects.isNull(formulaMapABC)){
                        log.error("找不到表{}的对应关系,规则id:{}", reportCode, rule.getId());
                        throw new RuntimeException("未找到表" + reportCode + "的对应关系");
                    }
                    // 跨表表达式处理,
                    matchedPart = doTransFormulaCZB(rowCol, formulaMapABC, formulaMapABCMap);
                } else {
                    final String reportCode = String.valueOf(rule.getReportCode());
                    Map<String, String> formulaMapABC = formulaMapABCMap.get(reportCode);
                    if(Objects.isNull(formulaMapABC)){
                        log.error("找不到表{}的对应关系,规则id:{}", reportCode, rule.getId());
                        throw new RuntimeException("未找到表" + reportCode + "的对应关系");
                    }
                    // 跨表表达式处理,
                    matchedPart = doTransFormulaCZB(matchedPart, formulaMapABC, formulaMapABCMap);
                }
                matcher.appendReplacement(result, matchedPart);
            }
            matcher.appendTail(result);
            // 特殊情况处理, SCOPE（dwLevel=='08',JB01!D27==0）, 这里会丢失Scope部分
            //bug号 121753 【测试】总决算简表中，校验公式SCOPE(dwLevel=='04',JB01!D27>=0*0.02)不起效
            // 兜底处理, 存在丢失函数替换部分，重新替换
            result = new StringBuffer(this.transMethodFormula(result.toString(), null));
            newFormulaExp = result.toString();
        } else {
            // 本表, 公式转换 D8>=0 ==> #L01['row':'col'] >=0
            final String reportCode = String.valueOf(rule.getReportCode());
            Map<String, String> formulaMapABC = formulaMapABCMap.get(reportCode);
            newFormulaExp = doTransFormulaCZB(newFormulaExp, formulaMapABC, formulaMapABCMap);
        }
        return newFormulaExp;
    }

    /**
     * @param expression       :
     * @param formatMapABC     :
     * @data: 2023/7/22-下午2:29
     * @User: zhao
     * @method: replaceCellReferences
     * @return: java.lang.String
     * @Description: 公式替换
     */
    public String doTransFormulaCZB(String expression, Map<String, String> formatMapABC, Map<String, Map<String, String>> formulaMapABCMap) {

        expression = transMethodFormula(expression, formatMapABC);

        //*** L24!B57==extractData_2021_null_L24_null_B58_null_null	应与上年期末数一致
        // 外部数据公式转换
        expression = this.processExtractFormula(expression,formulaMapABCMap);

        // 处理SUM函数中的动态单元格引用展开
        expression = this.calRuleService.processSumFormula(expression, formatMapABC);

        // 处理普通的表达式中的动态单元格引用
        expression = this.calRuleService.processNormalFormula(expression, formatMapABC);

        return expression;
    }

    @Autowired
    private CheckFormulaTransformerChain checkFormulaTransformerChain;

    /*
    *  替换公式中方法, 固定格式
     */
    private String transMethodFormula(String expression, Map<String, String> formatMapABC) {
        return checkFormulaTransformerChain.transform(expression, formatMapABC);
    }

    private static final Pattern METHOD_PATTERN = Pattern.compile("(\\w+)\\(([^)]*)\\)");

    /**
     * @param expression       :
     * @param formulaMapABCMap     :
     * @data: 2023/7/22-下午3:22
     * @User: zhao
     * @method: processExtractFormula
     * @return: java.lang.String
     * @Description: 外部数据公式适配
     * *** extractData_2021_null_L24_null_B58_null_null	应与上年期末数一致
     * *** extractData_2021_null_L24_null_B58_null_null*0.02 (2024公式变化)
     */
    private String processExtractFormula(String expression, Map<String, Map<String, String>> formulaMapABCMap) {
        if (!expression.contains("extractData_")) {
            return expression;
        }
        Map<String, String> formatMapABC = new HashMap<>();
        StringBuilder newExpression = new StringBuilder();
        newExpression.append(expression.substring(0,expression.indexOf("extractData_")));
        final String[] split1 = expression.split("_");
        for (int i = 0; i < split1.length; i++) {
            if(expression.contains("PREYEAR")){
                if (i == 1){
//              #_YYYY_PREMONTH_J02['J104:JE1']
                    String preYear = String.format("%04d", Integer.parseInt("2024") - 1);
                    newExpression.append("#_").append(preYear).append("_MM_");
                }
            }else{
                if (i == 1) {
//              #_YYYY_PREMONTH_J02['J104:JE1']
                    newExpression.append("#_").append(split1[i]).append("_MM_");
                }
            }
            if (i == 3){
                String reportCode = split1[i];
                formatMapABC = formulaMapABCMap.get(reportCode);
                if(Objects.isNull(formatMapABC)){
                    return expression;
                }
            }
            if (i == 5) {
                String s = formatMapABC.get(split1[i]);
                if(!Objects.isNull(s) && s.contains("#")){
                    s = s.replaceAll("#", "");
                }
                newExpression.append(s);
            }
            // 141997 【测试2.0】财政总决算>>简表，录入界面保存数据数据校验，涉及跨年的公式又跨表的公式，数据校验公式解析异常
            // SCOPE(dwLevel=='04',D27>=extractData_2023_null_L18_null_C11_null_null*0.02)
            // 需要补充运算符部分
            if (i == 7) {
                final String s = split1[i];
                if(s.contains("*")){
                    newExpression.append(s.substring(s.indexOf("*")));
                }
            }
        }

        return newExpression.toString();
    }

    private static final Pattern JE_PATTERN = Pattern.compile("je[0-9]+");

    /**
     * @date: 2022/11/23-下午6:23
     * @author: zhao
     * @method:
     * @param null :
     * @return:
     * @Description: 描述
     * 根据规则提取出出需要的数据单元, 如果存在其它逻辑符再增加即可
     * 计算公式: #J09['J102:JE1']+#J09['J103:JE1']+#J09['J104:JE1']
     * 校验公式: #J09['J102:JE1']+#J09['J103:JE1']+#J09['J104:JE1'] >=<!= #J09['J104:JE1']
     * 财政部公式转换后(20230719)
     * SUM(#CZYBGGYS (JB )ZCJSJJFLLR['CZYBGGYS (JB )ZCJSJJFLLR0013:JE3'],#CZYBGGYS (JB )ZCJSJJFLLR['CZYBGGYS (JB )ZCJSJJFLLR0014:JE3'])
     * <p>
     * 20230227 跨表校验  #J09['J102:JE1']+#J09['J103:JE1']+#J09['J104:JE1'] >=<!= #_YYYY_MM_J09['J104:JE1']
     * 20230629 跨表校验  #sum(#J09['J102:JE1'],#J09['J103:JE1'],#J09['J104:JE1']) >=<!= #_YYYY_MM_J09['J104:JE1']
     */
    public static final Pattern ITEM_PATTERN = Pattern.compile("(\\#[^#]*?\\[[^*+-/!=<>]*\\])");
    /**
     * 用于匹配校验公式，拆分逻辑运算符左右两边的公式部分
     */
    private static final Pattern RULE_PATTERN = Pattern.compile("(.*)(==|>=|<=|>|<|!=)(.*)");

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * @param dataList : 如果是总决算需要传入多年数据(当前年度+上年), 如果是月报需传入上月数据 (禅道:  16101 增加 跨年度、跨月、跨表项目校验公式)
     * @param ruleList : 涉及所有的规则信息
     * @data: 2023/2/27-下午2:22
     * @User: zhao
     * @method: checkDataByRule
     * @return: java.util.List<gov.mof.fasp2.busgl.dto.IVoucherDTO>
     * @Description: 描述
     */
    public List<Map<String, Object>> checkDataByRule(List<ReportData> dataList, List<ReportFormula> ruleList) {
//        System.setProperty("java.vm.name","Java HotSpot(TM) ");
//        final long objectSize = ObjectSizeCalculator.getObjectSize(fullDataList);
        calRuleService.dataClear(dataList, ruleList);
//        final long objectSize1 = ObjectSizeCalculator.getObjectSize(fullDataList);

        // 如果有异常信息, 塞到这里
        final List<Map<String, Object>> errorList = new ArrayList<>();
        // 内存占用太大, 清空等回收

        // 数据转换: list根据传入数据转换成行列表示的map, 如:map.put("row1:col1", xx);
        Map<String, Map<String, BigDecimal>> listToMap = calRuleService.listToMap(dataList, ruleList);
        log.debug("listToMap转化之后：{}", listToMap);
        if(listToMap.isEmpty()){
            return errorList;
        }
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
        for (Map.Entry<String, Map<String, BigDecimal>> mapEntry : listToMap.entrySet()) {
            // 这里要塞入校验的数据源对象, 如t01, t02
            standardEvaluationContext.setVariable(mapEntry.getKey(), mapEntry.getValue());
        }

        // 注册事件 #109653 校验公式支持聚合函数
        final Method[] declaredMethods = ELExtMethod.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            final String name = declaredMethod.getName();
            standardEvaluationContext.registerFunction(name, declaredMethod);
        }

        // 遍历校验传入公式
        ruleList.parallelStream().forEach(ruleMap -> {
            // 规则字符串, 数据为小写， 公式也转小写
            String formulaExp = String.valueOf(ruleMap.getFormulaExp()).toLowerCase();
            final Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("report_code", ruleMap.getReportCode());
            fieldMap.put("report_item_code", ruleMap.getItemCode());
            fieldMap.put("field_code", ruleMap.getFieldCode());
            // 构建规则，校验数据, 所有校验规则返回应该都是bool类型
            // 例: 公式: #t1.['a'] != null
            //     数据: map["a"] == ?
            log.debug("原校验公式: {}", formulaExp);
            // 公式转换 #_yyyy_mm_t1['row:col']形式公式根据当前月或者年度进行动态转换
            log.debug("跨年月校验公式: {}", formulaExp);
            try {
                boolean validate;
                validate = validateRule(ruleMap, formulaExp, standardEvaluationContext);
                if (Boolean.FALSE.equals(validate)) {
                    errorList.add(fieldMap);
                }
            } catch (Exception e) {

//                final Boolean value = parser.parseExpression("#czzfxjjyssrysbdqklr['czzfxjjyssrysbdqklr0001:je3']!=#czzfxjjyssrjslr['czzfxjjyssrjslr0001:je1']").getValue(standardEvaluationContext,
//                        Boolean.class);
//                final BigDecimal value2 = parser.parseExpression("#czzfxjjyssrjslr['czzfxjjyssrjslr0001:je1']").getValue(standardEvaluationContext, BigDecimal.class);
                log.error("校验公式解析异常, 单元格 {}, 公式 {}", fieldMap, formulaExp);
                log.error("校验公式解析异常", e);
            }
        });

        return errorList;
    }

    private final CommonSqlRepository commonSqlRepository;

    private boolean validateRule(ReportFormula ruleMap, String formulaExp, StandardEvaluationContext standardEvaluationContext) {
        boolean validate;
        if(formulaExp.contains("SELECT") || formulaExp.contains("select")){
            final AbstractFormulaStrategy sqlFormulaStrategy = new SqlFormulaStrategy();
            sqlFormulaStrategy.setCommonSqlRepository(commonSqlRepository);
            validate = sqlFormulaStrategy.validate(ruleMap);
        }else{
            // 标准公式处理
            final AbstractFormulaStrategy standardFormulaStrategy = new StandardFormulaStrategy();
            standardFormulaStrategy.setStandardEvaluationContext(standardEvaluationContext);
            validate = standardFormulaStrategy.validate(ruleMap);
        }
        return validate;
    }

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
    private static String removeFormulaSpeChar(String str) {
        // 如果包含特殊函数，先提取函数内部再替换
        return str.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\s*", "")
                // 特殊处理函数, 用中文括号方便转换
                .replaceAll("（", "(").replaceAll("）", ")");
    }

    /**
     * @data: 2023/9/27-下午2:15
     * @User: zhao
     * @method: checkReportByExpression
      * @param map :
     * @param type :
     * @return: java.util.Map
     * @Description: 单表校验
     */
    public Map checkReportByExpression(HashMap map, String type) {
        final String reportCode = String.valueOf(map.get("reportcode"));

        List<String> reportCodeList = Collections.singletonList(reportCode);

        List<ReportFormula> ruleList = reportFormulaRepository.findAllByReportCodeAndFormulaType(reportCode, "0");

        Map resultMap = new HashMap();
        resultMap.put("flag", "1");//通过
        if (ruleList.size() == 0) {
            return resultMap;
        }

        // 清理掉非必要公式, 如跨年, 跨表
        final Iterator<ReportFormula> iterator = ruleList.iterator();
        while(iterator.hasNext()){
            final ReportFormula next = iterator.next();
            final String formulaExp = String.valueOf(next.getFormulaExp());
            if(formulaExp.contains("!")){
                iterator.remove();
            } else if (formulaExp.contains("extract")) {
                iterator.remove();
            }
        }

        List<ReportData> fullDataList = reportDataRepository.findAllByReportCodeIn(reportCodeList);
        if (fullDataList.isEmpty()) {
            throw new RuntimeException("请先保存！");
        }

        // 适配财政部公式转换, 行列表示, 转换为字段表示
        transFormulaCZB(ruleList, reportCodeList);

        List<Map<String, Object>> errorList = this.checkDataByRule(fullDataList, ruleList);
        log.debug("校验失败信息: {}", errorList.toString());
        if (!errorList.isEmpty()) {
            resultMap.put("flag", "2");
            resultMap.put("data", errorList);
        }
        return resultMap;
    }
    /**
     * 通过公式获取逻辑运算符左右两边的具体数值，用作校验弹窗的具体数据展示 20240626
     */
    private Map<String, Object> setValuetoErrorList(ExpressionParser parser,  StandardEvaluationContext standardEvaluationContext,Map<String, Object> ruleMap) throws Exception {
        String formulaExp = String.valueOf(ruleMap.get("formula_exp")).toLowerCase();
        String formulaContentReplace1 = ruleMap.get("formulaContentReplace")+"";
        String formula_exp_origin1 = String.valueOf(ruleMap.get("formula_exp_origin"));

        //Scope函数需要预先处理一下 --141541 【测试2.0】财政总决算>>简表，数据校验弹框，涉及“PAY@202312@ysszyb”的公式不展示单元格行/列、数据描述
        if (formulaExp.contains("scope")) {
            if (formulaExp.contains("avsubstr")) {
                formulaExp = formulaExp.substring(formulaExp.lastIndexOf(",")+1, formulaExp.length() - 1);
            } else {
                String[] split = formulaExp.split(",");
                formulaExp = split[1];
                formulaExp = formulaExp.substring(0, formulaExp.length() - 1);
            }
        }
        if (formulaContentReplace1.contains("scope")) {
            if (formulaContentReplace1.contains("avsubstr")) {
                formulaContentReplace1 = formulaContentReplace1.substring(formulaContentReplace1.lastIndexOf(",")+1, formulaContentReplace1.length() - 1);
            } else {
                String[] split = formulaContentReplace1.split(",");
                formulaContentReplace1 = split[1];
                formulaContentReplace1 = formulaContentReplace1.substring(0, formulaContentReplace1.length() - 1);
            }
        }
        if (formula_exp_origin1.contains("SCOPE")) {
            if (formula_exp_origin1.contains("AVSUBSTR")) {
                formula_exp_origin1 = formula_exp_origin1.substring(formula_exp_origin1.lastIndexOf(",")+1, formula_exp_origin1.length() - 1);
            } else {
                String[] split = formula_exp_origin1.split(",");
                formula_exp_origin1 = split[1];
                formula_exp_origin1 = formula_exp_origin1.substring(0, formula_exp_origin1.length() - 1);
            }
        }

        String formulaContentReplace = removeFormulaSpeChar(formulaExp);
        // <> 号需要替换为!= 才能使用
        formulaContentReplace = formulaContentReplace.replaceAll("<>", "!=");
        log.debug("校验公式括号替换, 原: {}, 新: {}", formulaExp, formulaContentReplace);
        Matcher matcher = RULE_PATTERN.matcher(formulaContentReplace);
        Matcher formula_exp_origin = RULE_PATTERN.matcher(formula_exp_origin1);
        if(matcher.find()){
            String groupleft = matcher.group(1);
            //String groupleft = matcher.group(2);逻辑运算符部分
            String groupright = matcher.group(3);
            BigDecimal leftbigDecimal = parser.parseExpression(groupleft).getValue(standardEvaluationContext, BigDecimal.class);
            log.info("公式{}左边值{}",formula_exp_origin1,leftbigDecimal);
            BigDecimal rightbigDecimal = null;
            if(formula_exp_origin1.contains("PAY@")||formula_exp_origin1.contains("DTFPERM")||formula_exp_origin1.contains("PERM_MT$")||formula_exp_origin1.contains("AVSUBSTR")){
                Matcher matcher1 = RULE_PATTERN.matcher(formulaContentReplace1);
                String right = "";
                if(matcher1.find()){
                    right=matcher1.group(3);
                }
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("JavaScript");
                String result = null;
                try {
                    result = engine.eval(right)+"";
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
                rightbigDecimal = new BigDecimal(result);
            }else{
                rightbigDecimal = groupright.contains("#") ? parser.parseExpression(groupright).getValue(standardEvaluationContext, BigDecimal.class) : new BigDecimal(groupright);
            }
            StringBuffer sb = new StringBuffer();
            String append = sb.append("左边值:")
                    .append(leftbigDecimal)
                    .append(" 右边值:")
                    .append(rightbigDecimal)
                    .append(" 差值:")
                    .append(leftbigDecimal.subtract(rightbigDecimal)).toString();
            ruleMap.put("decimal_value_des",append);
        }
        if(formula_exp_origin.find()){
            String group = formula_exp_origin.group(1);
            if(group.contains("!")){
                group = group.substring(group.indexOf("!")).toUpperCase();
            }
            Pattern compile1 = Pattern.compile("[A-Z]+");
            Pattern compile2 = Pattern.compile("\\d+");
            Matcher matcher1 = compile1.matcher(group);
            Matcher matcher2 = compile2.matcher(group);
            if(matcher1.find()){
                String group1 = matcher1.group();
                if(group.startsWith("ABS(")) {
                     group1 = group.substring(group.indexOf("(")+1,group.indexOf(")")-1);
                }
                ruleMap.put("cell_colnum",group1);
            }
            if(matcher2.find()){
                String group2 = matcher2.group();
                ruleMap.put("cell_rownum",group2);
            }
        }
        return ruleMap;
    }
}
