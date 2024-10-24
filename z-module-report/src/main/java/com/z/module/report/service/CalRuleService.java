package com.z.module.report.service;

import com.z.module.report.domain.ReportData;
import com.z.module.report.domain.ReportField;
import com.z.module.report.domain.ReportFormula;
import com.z.module.report.domain.ReportItem;
import com.z.module.report.repository.ReportDataRepository;
import com.z.module.report.repository.ReportFieldRepository;
import com.z.module.report.repository.ReportFormulaRepository;
import com.z.module.report.repository.ReportItemRepository;
import com.z.module.report.service.checkrule.ext.ELExtMethod;
import com.z.module.report.util.ReportUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.z.module.report.service.CheckRuleService.ITEM_PATTERN;

/**
 * @Title: null.java
 * @Package: gov.mof.fasp2.gfbi.common.calrule
 * @Description: 计算公式服务类
 * 处理财政不下发公式, 原公式不再支持, 如果预算执行报表下发公式表达不一致, 则单独代码处理
 * @author: zhao
 * @date: 2023/7/18 上午9:46
 * @version: V1.0
 */
@Service
@Slf4j
public class CalRuleService {

    private final ReportFormulaRepository reportFormulaRepository;
    private final ReportItemRepository reportItemRepository;
    private final ReportFieldRepository reportFieldRepository;
    private final ReportDataRepository reportDataRepository;

    public CalRuleService(ReportFormulaRepository reportFormulaRepository, ReportItemRepository reportItemRepository, ReportFieldRepository reportFieldRepository, ReportDataRepository reportDataRepository) {
        this.reportFormulaRepository = reportFormulaRepository;
        this.reportItemRepository = reportItemRepository;
        this.reportFieldRepository = reportFieldRepository;
        this.reportDataRepository = reportDataRepository;
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
     * @Description: 计算结果直接回写到数据中, 业务直接保存即可
     */
    public void mathReportFormulaAll(List<ReportData> datas, String reportCode, String type, boolean addFlag){
        if(datas.isEmpty()){
            return;
        }

        // 获取计算公式formula_type 2:计算, 1:取数, 0:校验
        List<ReportFormula> ruleList = reportFormulaRepository.findAllByReportCodeAndFormulaType(reportCode, "2");
        if (!ruleList.isEmpty()) {

            // 根据规则获取依赖的报表编码
            Set<String> reportCodeSet = this.getReportCodesByRuleList(ruleList);
            reportCodeSet.add(reportCode);

            // 适配财政部公式转换, 行列表示, 转换为字段表示
            transFormulaCZB(ruleList, reportCodeSet);

            List<ReportData> fullDataList = reportDataRepository.findAllByReportCodeIn(reportCodeSet);

            if(addFlag){
                // 新增时要填充新增数据进来
                fullDataList.addAll(datas);
            }else{
                // 修改时要用前台调整数据覆盖数据库数据
                for (ReportData map : fullDataList) {
                    for (ReportData data : datas) {
                        if(data.getReportCode().equals(map.getReportCode())
                                && data.getItemCode().equals(map.getItemCode())
                        ){
                            BeanUtils.copyProperties(data, map);
                        }
                    }
                }
            }

            this.calculation(fullDataList, ruleList);
            //兼容新增修改， report_code和report_item_code相同则表示为同一行数据
            // fullDataList会带有计算结果, 需要反馈到list中
            for (ReportData data : datas) {
                for (ReportData map : fullDataList) {
                    if(data.getReportCode().equals(map.getReportCode())
                            && data.getItemCode().equals(map.getItemCode())
                    ){
                        BeanUtils.copyProperties(data, map);
                    }
                }
            }
        }
    }

    /**
     * @param ruleList            :
     * @param reportCodeSet
     * @data: 2023/7/18-上午11:37
     * @User: zhao
     * @method: transFormulaCZB
     * @return: void
     * @Description: 转换为财政部公式
     * <p>
     * !分隔为表, 没有!好的为当前表, :分隔表示区间计算
     * 如: L08!C15+L08!C17+L08!C59-C14
     * SUM(L9:L29)
     * C162+D162
     */
    public void transFormulaCZB(List<ReportFormula> ruleList, Collection<String> reportCodeSet) {
        final Map<String, Map<String, String>> formulaMapABCMap = getCoorFormatMap(reportCodeSet);

        // 获取行列与单元格对照, 不包含!
        for (ReportFormula rule : ruleList) {
            String formulaExp = String.valueOf(rule.getFormulaExp());
            // 将公式中小括号统一转换为中文括号
            formulaExp = formulaExp.replaceAll("\\(", "（").replaceAll("\\)", "）");

            StringBuilder newFormulaExp = new StringBuilder();
            // 只取=号右边部分
            String reg = "=(.*)";
            final Pattern compile = Pattern.compile(reg);
            final Matcher matcher = compile.matcher(formulaExp);
            if (matcher.find()) {
                // 只保留运算符右边部分参与计算
                final String rightFormulaExp = matcher.group(1);
                newFormulaExp.append(splitFormulaWithSymbol(formulaMapABCMap, rule, rightFormulaExp));
            }else{
                // 兼容没有运算符的公式
                String newFormulaExpStr = splitFormulaWithSymbol(formulaMapABCMap, rule, formulaExp);
                newFormulaExp.append(newFormulaExpStr);
            }
            rule.setFormulaExp(newFormulaExp.toString());
            log.info("原公式: {}, 转换后公式: {}", formulaExp, newFormulaExp);
        }
    }

    /**
     * @Description: 获取坐标和行列表示映射
     * @author: zhao
     * @data: 2024/10/24-09:27
     * @param reportCodeSet :
     * @return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.String>>
    */

    @NotNull
    public Map<String, Map<String, String>> getCoorFormatMap(Collection<String> reportCodeSet) {
        final Map<String, Map<String, String>> formulaMapABCMap = new HashMap<>();

        // 根据报表行列坐标表示为字段映射关系
        // 1. 分别获取行和列信息，根据坐标进行转换
        List<ReportItem> reportItemList = reportItemRepository.findAllByReportCodeIn(reportCodeSet);
        List<ReportField> reportFieldList = reportFieldRepository.findAllByReportCodeIn(reportCodeSet);
        Map<String, List<ReportItem>> reportItemGroupByReportCode = reportItemList.stream().collect(Collectors.groupingBy(ReportItem::getReportCode));
        Map<String, List<ReportField>> reportFieldGroupByReportCode = reportFieldList.stream().collect(Collectors.groupingBy(ReportField::getReportCode));
        for (String reportCode : reportCodeSet) {
            final HashMap<String, String> formulaMapABC = new HashMap<>();
            List<ReportField> reportFields = reportFieldGroupByReportCode.get(reportCode);
            for (ReportItem reportItem : reportItemGroupByReportCode.get(reportCode)) {
                for (ReportField reportField : reportFields) {
                    final String format = String.format("#%s['%s:%s']", reportCode,
                            reportItem.getItemCode(), reportField.getFieldCode());
                    formulaMapABC.put(reportField.getColNum() + reportItem.getRowNum(), format);
                }
            }
            formulaMapABCMap.put(reportCode, formulaMapABC);
        }
        return formulaMapABCMap;
    }


    /**
     * @data: 2023/7/25-上午10:08
     * @User: zhao
     * @method: splitFormulaWithSymbol
     * @param formulaMapABCMap :
     * @param rule :
     * @param formulaExp :
     * @return: java.lang.String
     * @Description: 大部分为跨表取数, 可能存在取数表不一致, 拆分分别处理, 根据 逻辑符号分割后再传入
     * 如:
     */
    private String splitFormulaWithSymbol(Map<String, Map<String, String>> formulaMapABCMap, ReportFormula rule, String formulaExp) {
        String newFormulaExp = formulaExp.toUpperCase();
        if (newFormulaExp.contains("!")) {
            // 提取表格, 获取配置信息,进行转换, 可能涉及多表, 需要使用表达式将带!的单元和普通公式分组
            StringBuilder result = new StringBuilder();
            String regex = "\\w+!?\\w+";
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
                        return newFormulaExp;
                    }
                    // 跨表表达式处理,
                    matchedPart = replaceCellReferences(rowCol, formulaMapABC);
                } else {
                    final String reportCode = String.valueOf(rule.getReportCode());
                    Map<String, String> formulaMapABC = formulaMapABCMap.get(reportCode);
                    // 跨表表达式处理,
                    matchedPart = replaceCellReferences(matchedPart, formulaMapABC);
                }
                matcher.appendReplacement(result, matchedPart);
            }
            matcher.appendTail(result);
            newFormulaExp = result.toString();
        } else {
            // 本表, 公式转换 D8>=0 ==> #L01['row':'col'] >=0
            final String reportCode = String.valueOf(rule.getReportCode());
            Map<String, String> formulaMapABC = formulaMapABCMap.get(reportCode);
            newFormulaExp = replaceCellReferences(newFormulaExp, formulaMapABC);
        }
        return newFormulaExp;
    }


    /**
     * @data: 2023/7/19-下午2:00
     * @User: zhao
     * @method: replaceCellReferences
      * @param expression :
 * @param formatMapABC :
     * @return: java.lang.String
     * @Description: 动态处理函数中字段引用, 特殊的继续扩展即可
     *
     * SUM(#CZYBGGYSZYXSZJSLR['CZYBGGYSZYXSZJSLR0192,JE1'],#CZYBGGYSZYXSZJSLR['CZYBGGYSZYXSZJSLR0193,JE1'])
     *
     */
    public String replaceCellReferences(String expression, Map<String, String> formatMapABC) {

        // 处理SUM函数中的动态单元格引用展开
        expression = processSumFormula(expression, formatMapABC);

        // 处理普通的表达式中的动态单元格引用
        expression = processNormalFormula(expression, formatMapABC);

        return expression;
    }

    public String processSumFormula(String expression, Map<String, String> cellMap) {
        if (!expression.contains("SUM")) {
            return expression;
        }
        StringBuilder result = new StringBuilder();

//        Pattern sumPattern = Pattern.compile("SUM（(.*?)）");
//        Matcher matcher = sumPattern.matcher(expression);
        Matcher matcher = SUM_PATTERN.matcher(expression);
        // 如果找到匹配项
        while (matcher.find()) {
            String cellRange = matcher.group(1);
            final List<String> expandedCells = expandCellRanges(cellRange, cellMap);
            // 构建展开后的SUM函数参数, 用中文括号,　方便后续函数内部特殊字符替换
            // gov.mof.fasp2.gfbi.common.calrule.CalRuleService.removeFormulaSpeChar
//            注: 这里不要用sum函数，存在公式sum(a+b+c)形式，展开更方便
//            matcher.appendReplacement(result, "SUM（" + String.join(",", expandedCells) + "）");
            // #122817 【测试】财政总决算录入表中，嵌套公式计算有问题，见截图。---经过解析B12-SUM（D5:D10），转换后的公式减号[-]后面的SUM整体没有加括号，导致计算错误
            matcher.appendReplacement(result, "（"+String.join("+", expandedCells)+"）");
        }
        matcher.appendTail(result);
        return result.toString();
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

    // 使用正则表达式匹配 SUM(E7:E15) 形式, 统一先转换中文括号
    private static final Pattern SUM_PATTERN = Pattern.compile("SUM（(.*?)）");

    public List<String> expandCellRanges(String cellRanges, Map<String, String> cellMap) {
        List<String> expandedCells = new ArrayList<>();

        // 分割不连续范围，使用逗号","作为分隔符
        String[] rangeArray = cellRanges.split(",");
        for (String range : rangeArray) {
            expandedCells.addAll(expandCellRange(range.trim(), cellMap));
        }

        return expandedCells;
    }

    /**
     * @data: 2023/7/18-下午5:31
     * @User: zhao
     * @method: expandCellRange
     * @param cellRange :
     * @return: java.util.List<java.lang.String>
     * @Description: 公式展开
     * SUM(E7:E15,E22) ==> SUM(E7,E8,E9,E10,E11,E12,E13,E14,E15,E22)
     */
    public List<String> expandCellRange(String cellRange, Map<String, String> cellMap) {
        List<String> expandedCells = new ArrayList<>();

        // 分割参数，使用冒号":"作为分隔符
        String[] rangeParts = cellRange.split(":");
        if (rangeParts.length == 1) {
            // 单个单元格
            String cellRef = rangeParts[0];
            String mappedCell = cellMap.getOrDefault(cellRef, cellRef);
            expandedCells.add(mappedCell);
        }else if (rangeParts.length == 2) {
            String startCell = rangeParts[0];
            String endCell = rangeParts[1];

            // 提取起始单元格的列和行信息
            String startColumn = startCell.replaceAll("\\d", "");
            int startRow = Integer.parseInt(startCell.replaceAll("\\D", ""));

            // 提取结束单元格的列和行信息
            String endColumn = endCell.replaceAll("\\d", "");
            int endRow = Integer.parseInt(endCell.replaceAll("\\D", ""));

            // 展开单元格范围并添加到列表中
            for (int row = startRow; row <= endRow; row++) {
                for (int col = columnToNumber(startColumn); col <= columnToNumber(endColumn); col++) {
                    String cellRef = numberToColumn(col) + row;
                    if(!Objects.isNull(cellMap)){
                        String mappedCell = cellMap.getOrDefault(cellRef, cellRef);
                        expandedCells.add(mappedCell);
                    }else{
                        expandedCells.add(cellRef);
                    }
                }
            }
        }else{
            throw new IllegalArgumentException("Invalid cell range format: " + cellRange);
        }

        return expandedCells;
    }

    public int columnToNumber(String column) {
        return ReportUtil.columnToNumber(column);
    }

    /**
     * @data: 2023/7/21-下午4:47
     * @User: zhao
     * @method: numberToColumn
      * @param number :
     * @return: java.lang.String
     * @Description: 将数字转换为A,Ｂ表示, 如:1为A, 27为AA
     */
    public String numberToColumn(int number) {
        return ReportUtil.numberToColumn(number);
    }

    /**
     * @data: 2023/7/18-下午3:43
     * @User: zhao
     * @method: getFormulaMapABC
     * @param reportViewTableInfo : 报表展示信息, 可能存在多个报表的信息
     * @param reportCode : 报表编码
     * @return: java.util.HashMap<java.lang.String,java.lang.String>
     * @Description: 描述
     *
     * 财政部下发公式根据界面展现进行配置, 默认需要增加三行(标题行, 录入xx表, 单位) 再根据最打表头增加行
     */
    public HashMap<String, String> getFormulaMapABC(Map<String, Map> reportViewTableInfo, String reportCode,Map<String, String> reportAbbCodeMap) {
        Map tableInfo = reportViewTableInfo.get(reportCode);
        final List<Map<String, Object>> colList = (List<Map<String, Object>>) tableInfo.get("cols");
        final List<Map<String, Object>> rowList = (List<Map<String, Object>>) tableInfo.get("datas");
        int maxHeadSize = Integer.parseInt(String.valueOf(tableInfo.get("maxHeadSize")));
        // 行列映射配置, 单元格表示转换
        final HashMap<String, String> formulaMapABC = new HashMap<>();
        for (int i = 0; i < rowList.size(); i++) {
            for (int j = 0; j < colList.size(); j++) {
                final Map<String, Object> colMap = colList.get(j);
                String colCode = String.valueOf(colMap.get("colcode"));
                final Map<String, Object> rowMap = rowList.get(i);
                // 如果分栏显示, 后缀为_type[num]
                if (colCode.contains("je")) {
                    // 转成列对应的英文字母,
                    final String colNewCode = this.numberToColumn(j + 1);
                    String reportItemCode;
                    if (colCode.contains("_type")) {
                        // 分栏
                        final String type = colCode.substring(colCode.indexOf("_type"));
                        reportItemCode = String.valueOf(rowMap.get("report_item_code" + type));
                        colCode = colCode.replaceAll(type, "");
                    } else {
                        reportItemCode = String.valueOf(rowMap.get("report_item_code"));
                    }
                    final String format = String.format("#%s['%s:%s']", reportCode,
                            reportItemCode, colCode.toUpperCase());
                    formulaMapABC.put(colNewCode + (i + 1 + maxHeadSize), format);
                }
            }
        }
        return formulaMapABC;
    }

    private static final Pattern JE_PATTERN = Pattern.compile("je[0-9]+");

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * @data: 2024/6/6-下午1:06
     * @User: zhao
     * @method: dataClear
     * @param dataList :
     * @param ruleList :
     * @return: void
     * @Description: 根据计算公式规则，清理data,优化内存占用
     */
    public void dataClear(List<ReportData> dataList, List<ReportFormula> ruleList) {
        final Set<String> matches = ruleList.stream()
                .flatMap(ruleMap -> {
                    final String formulaExp = ruleMap.getFormulaExp().toString();
                    Matcher matcher = ITEM_PATTERN.matcher(formulaExp);
                    List<String> matchList = new ArrayList<>();
                    while (matcher.find()) {
                        String group = matcher.group();
                        matchList.add(group.toLowerCase().substring(1).replace("#", ""));
                    }
                    return matchList.stream();
                })
                .collect(Collectors.toSet());

        final Set<String> requiredColumns = new HashSet<>(Arrays.asList("report_item_code", "report_item_name", "report_code"));

        // 处理数据
        dataList.parallelStream().forEach(objectMap -> {
            final String reportCode = String.valueOf(objectMap.getReportCode()).toLowerCase();
            final String reportItemCode = String.valueOf(objectMap.getItemCode()).toLowerCase();

            // 使用迭代器移除不需要的字段
//            objectMap.entrySet().removeIf(entry -> {
//                String key = entry.getKey();
//                if (!requiredColumns.contains(key)
//                        && !key.contains("je")) {
//                    return true;
//                }
//                // 按照公式清理
//                final String matchUnitItem = String.format("%s['%s:%s']", reportCode, reportItemCode, key);
//                return key.contains("je") && !matches.contains(matchUnitItem);
//            });
        });
    }


    /**
     * @param datas    : 参与计算的所有数据都要正确再传进来, 金额必须为数值类型
     * @param ruleList : 防止公式转换冲突, 公式的配置都会带有表名
     * @data: 2022/8/23-上午11:15
     * @User: zhao
     * @method: calculation
     * @return: void
     * @Description: 根据单元格公式计算
     */
    public List<ReportData> calculation(List<ReportData> datas, List<ReportFormula> ruleList) {

        // 1. 公式涉及嵌套需要先转换为基础公式, 保证原子计算
        transFormula(ruleList);
        // 数据清理
        dataClear(datas, ruleList);

        // 2. 数据转换: list根据传入数据转换成行列表示的map, 如:map.put("row1:col1", xx); 方便SpEL进行解析
        Map<String, Map<String, BigDecimal>> listToMap = listToMap(datas, ruleList);

        // 将上述构建的listMap对象赋值到EvaluationContext中，方便后续公式解析
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
        for (Map.Entry<String, Map<String, BigDecimal>> mapEntry : listToMap.entrySet()) {
            // 这里要塞入校验的数据源对象, 如t01, t02
            standardEvaluationContext.setVariable(mapEntry.getKey(), mapEntry.getValue());
        }

        // 注册事件 校验公式支持聚合函数
        final Method[] declaredMethods = ELExtMethod.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            final String name = declaredMethod.getName();
            standardEvaluationContext.registerFunction(name.toLowerCase(), declaredMethod);
        }

        // 3. 遍历公式进行数据计算
        // 保存数据坐标和计算结果, 方便进行数据填充
        final List<Map<String, Object>> formulaResultList = new ArrayList<>();
        for (ReportFormula ruleMap : ruleList) {
            final String formulaContent = String.valueOf(ruleMap.getFormulaExp()).toLowerCase();
            final Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("report_code", ruleMap.getReportCode());
            fieldMap.put("report_item_code", ruleMap.getItemCode());
            fieldMap.put("field_code", ruleMap.getFieldCode());

            try{
                // 构建规则，校验数据, 所有计算公式返回BigDecimal
                // 例: 公式: t01['001:amt01'] + t01['001:amt02']
                // 公式里不能包含特殊符号如 (), 空格等
                final String formulaContentReplace = removeFormulaSpeChar(formulaContent);
                log.info("公式替换, 单元格: {}, 原: {}, 新: {}", fieldMap, formulaContent, formulaContentReplace);
                final BigDecimal result =
                        Objects.requireNonNull(parser.parseExpression(formulaContentReplace).getValue(standardEvaluationContext,
                                BigDecimal.class)).setScale(2, RoundingMode.HALF_UP);

                fieldMap.put("result", result);
                formulaResultList.add(fieldMap);
            }catch (Throwable e){
                log.error("计算公式解析异常, 单元格 {}, 公式 {}", fieldMap, formulaContent);
                log.error("计算公式解析异常", e);
            }
        }

        // 4. 遍历数据，将计算结果进行单元格填充
        final Map<String, List<Map<String, Object>>> resultGroup =
                formulaResultList.stream().collect(Collectors.groupingBy(map -> map.get("report_code") + "_" + map.get(
                        "report_item_code")));
        for (ReportData data : datas) {
            // 按行填充
            String key = data.getReportCode() + "_" + data.getItemCode();
            if (resultGroup.containsKey(key)) {
                for (Map<String, Object> map : resultGroup.get(key)) {
                    // 相同report_code和item_code 整行的计算结果全部回写data
//                    data.put(String.valueOf(map.get("field_code")).toLowerCase(), map.get("result"));
                    try {
                        BeanInfo beanInfo = Introspector.getBeanInfo(ReportData.class);
                        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                            Method writeMethod = propertyDescriptor.getWriteMethod();
                            if(writeMethod != null && writeMethod.getName().replace("set", "")
                                    .equalsIgnoreCase(String.valueOf(map.get("field_code")))){
                                writeMethod.invoke(data, map.get("result"));
                            }
                        }
                    } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return datas;
    }

    /**
     * @data: 2023/7/20-上午9:37
     * @User: zhao
     * @method: removeFormulaSpeChar
      * @param str :
     * @return: java.lang.String
     * @Description: 描述
     *
    // 公式里不能包含特殊符号如 (), 空格等
     */
    private static String removeFormulaSpeChar(String str) {
        // 如果包含特殊函数，先提取函数内部再替换
        return str.replaceAll("\\(|\\)|\\s", "")
                // 特殊处理函数, 用中文括号方便转换
                .replace("（", "(").replace("）", ")");
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
    public void transFormula(List<ReportFormula> ruleList) {
        final long l = System.currentTimeMillis();
        log.debug("原公式: {}", ruleList);

        // 先将ruleList转换为map, 方便替换
        final Map<String, String> formulaMap = new HashMap<>();
        for (ReportFormula rule : ruleList) {
            String formulaExp = String.valueOf(rule.getFormulaExp());
            if(formulaExp.contains("#")){
                formulaMap.put("#" + rule.getReportCode() + "['" + rule.getItemCode() + ":" + rule.getFieldCode() +
                        "']", formulaExp);
            }
        }

        ruleList
                .parallelStream()
                .forEach(rule -> {
            // 公式原子化
            try{
                String formulaExp = rule.getFormulaExp();
                if(formulaExp.contains("#")){
                    rule.setFormulaExp(replaceFormulaStr2(formulaMap, formulaExp));
                }
            }catch (Throwable e){
                final Map<String, Object> fieldMap = new HashMap<>();
                fieldMap.put("report_code", rule.getReportCode());
                fieldMap.put("report_item_code", rule.getItemCode());
                fieldMap.put("field_code", rule.getFieldCode());
                log.info("存在公式可能循环依赖, 起始单元格存在公式可能循环依赖, 起始单元格: {}, 公式: {}", fieldMap, rule.getFormulaExp());
                // 这里打开, 一个表有问题有序就不会再运行
                throw new RuntimeException("公式转换异常,存在循环依赖");
            }
        });

        log.debug("转换后公式: {}", ruleList);
        log.info("公式原子化转换总条数: {}, 转换耗时: {}", ruleList.size(), (System.currentTimeMillis() - l));
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
     * @data: 2023/8/4-下午3:32
     * @User: zhao
     * @method: replaceFormulaStr2
     * @param formulaMap :
     * @param newFormulaStr :
     * @return: java.lang.String
     * @Description: 循环方式实现公式转换
     * 可能会导致无限循环(抛出异常)，如果存在环形依赖，例如A依赖于B，B依赖于C，C又依赖于A
     */
    private String replaceFormulaStr2(Map<String, String> formulaMap, String newFormulaStr) {
        boolean isReplaced;
        // 记录已经替换过的项, 如果下次又存在说明出现了循环依赖
        Set<String> replacedKeys = new HashSet<>();
        do{
            isReplaced = false;
            StringBuilder result = new StringBuilder(newFormulaStr);
            for (Map.Entry<String, String> formula : formulaMap.entrySet()) {
                String formulaKey = formula.getKey();
                String value = formula.getValue();
                if (newFormulaStr.contains(formulaKey)) {
                    if (replacedKeys.contains(formulaKey)) {
                        // 上次替换过的字符串在以后不可能出现, 出现就说明公式会循环 A=B+C, B=A+D这种不允许
                        log.info("公式存在循环依赖, formulaKey: {} 多次出现, formulaMap: {}", formulaKey, formulaMap);
                        throw new RuntimeException("公式存在循环依赖,检查后台日志");
                    }
                    // 替换所有的匹配字符串
                    int index = 0;
                    while ((index = result.indexOf(formulaKey, index)) != -1) {
                        String replaceStr = "（" + value + "）";
                        result.replace(index, index + formulaKey.length(), replaceStr);
                        index += replaceStr.length(); // 更新索引位置
                    }
                    replacedKeys.add(formulaKey);
                    isReplaced = true;
                }
            }
            newFormulaStr = result.toString();
        }while (isReplaced);

        return newFormulaStr;
    }

    /**
     * @data: 2023/7/21-下午2:44
     * @User: zhao
     * @method: getReportCodesByRuleList
      * @param ruleList : 规则列表
     * @return: java.util.Set<java.lang.String>
     * @Description: 提取规则中涉及的报表编码
     *
     */
    public <T extends ReportFormula> Set<String> getReportCodesByRuleList(List<T> ruleList) {
        Set<String> formulaReportCodeSet = new HashSet<>();
        for (T map : ruleList) {
            String formulaExp = String.valueOf(map.getFormulaExp());
            if(StringUtils.hasText(formulaExp) && !formulaExp.contains("!") && !formulaExp.contains("extractData_")){
                continue;
            }
            if(formulaExp.contains("extractData")){
                final String extractData = formulaExp.substring(formulaExp.indexOf("extractData_"));
                final String[] split = extractData.split("_");
                formulaReportCodeSet.add(split[3]);
            }else{
                // ([A-Z0-9]+)! L08!C15+L08!C17+L08!C59
                String exp = "([A-Z0-9]+)!";
                String[] values = getMatcherValues(formulaExp, exp);
                for (String value : values) {
                    value = value.replace("!", "");
                    formulaReportCodeSet.add(value);
                }
            }
        }
        return formulaReportCodeSet;
    }

    public String[] getMatcherValues(String value, String patternStr) {

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(value);

        List<String> matches = new ArrayList<>(5);
        while (matcher.find()) {
            String group = matcher.group();
            matches.add(group);
        }

        return matches.toArray(new String[0]);
    }

    /**
     * @param dataList :
     * @param matches #表名['行:列']
     * @date: 2022/10/30-下午6:07
     * @author: zhao
     * @method: listToMap
     * @return: java.util.Map<java.lang.String, java.util.Map < java.lang.String, java.lang.Object>>
     * @Description: 数据转换, list根据规则转成map表达
     * 如:
     * list:
     * |report_item_code   |amt01     |amt02     | amt03   |
     * |001        |1         |  2       |  3      |
     * |002        |1         |  2       |  3      |
     * |003        |1         |  2       |  3      |
     * <p>
     * 转map:
     * map['001:amt01'] = 1
     * map['001:amt02'] = 2
     * map['001:amt03'] = 3
     * map['002:amt01'] = 1
     * map['002:amt02'] = 2
     * ......
     * <p>
     * 根据第一列report_item_code作为行标识, 其它作为列，转换为map, 如list中 第一行的金额1转换为   001:amt01的key-value
     * <p>
     * 最终map需要根据report_code区分，多表, 给StandardEvaluationContext使用, 公式可能涉及多表
     */
    public Map<String, Map<String, BigDecimal>> listToMap(List<ReportData> dataList, List<ReportFormula> ruleList) {
        final Set<String> matches = new HashSet<>();
        for (ReportFormula map : ruleList) {
            Matcher matcher = ITEM_PATTERN.matcher(map.getFormulaExp());
            while (matcher.find()) {
                String group = matcher.group();
                matches.add(group.toLowerCase().substring(1));
            }
        }
        final Map<String, Map<String, BigDecimal>> result = new HashMap<>();
        // 多个报表数据源需要拆分开, 方便校验
        final Map<String, List<ReportData>> groupByReportCode =
                dataList.stream().collect(Collectors.groupingBy(m -> m.getReportCode().toLowerCase()));
        for (Map.Entry<String, List<ReportData>> listEntry : groupByReportCode.entrySet()) {
            final String reportCode = String.valueOf(listEntry.getKey());
            final List<ReportData> curReportCodeList = listEntry.getValue();
            final Map<String, BigDecimal> curReportCodeMap = new ConcurrentHashMap<>();
            curReportCodeList.parallelStream().forEach(map -> {
                String reportItemCode = map.getItemCode().toLowerCase();
                BeanInfo beanInfo = null;
                try {
                    beanInfo = Introspector.getBeanInfo(ReportData.class);
                } catch (IntrospectionException e) {
                    throw new RuntimeException(e);
                }
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    String name = propertyDescriptor.getName();
                    if (JE_PATTERN.matcher(name).matches()) {
                        // 将金额列都表示为 项:栏目 的key-value形式
                        // 注: 如果涉及其它列, 调整这里if条件，扩充即可, 减少map的占用量 MAXIMUM_CAPACITY = 1 << 30
                        // 不涉及的单元格就不放到对象里了
                        final String matchUnitItem = String.format("#%s['%s:%s']", reportCode, reportItemCode, name);
                        if(matches.contains(matchUnitItem)){
                            Method readMethod = propertyDescriptor.getReadMethod();
                            try {
                                curReportCodeMap.put(removeFormulaSpeChar(reportItemCode) + ":" + name, (BigDecimal) readMethod.invoke(map));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            });
            result.put(removeFormulaSpeChar(reportCode), curReportCodeMap);
        }

        // 数据补充， 可能存在依赖表还没数据的情况, 要构建一份数据出来, 遍历listToMap, 没有就给0
        for (String match : matches) {
            final String[] split = match.split("\\[");
            String reportCode = split[0].substring(1);
            reportCode = removeFormulaSpeChar(reportCode);
            final String[] itemFieldArray = split[1].split(":");
            String reportItemCode = itemFieldArray[0].substring(1);
            reportItemCode = removeFormulaSpeChar(reportItemCode);
            String fieldCode = itemFieldArray[1].substring(0, itemFieldArray[1].length() - 2);
            if(Objects.isNull(result.get(reportCode))){
                final HashMap<String, BigDecimal> m = new HashMap<>();
                m.put(reportItemCode + ":" + fieldCode, BigDecimal.ZERO);
                result.put(reportCode, m);
            }else{
                final Map<String, BigDecimal> m = result.get(reportCode);
                if(Objects.isNull(m.get(reportItemCode + ":" + fieldCode))){
                    m.put(reportItemCode + ":" + fieldCode, BigDecimal.ZERO);
                }
            }
        }
        return result;
    }
}
