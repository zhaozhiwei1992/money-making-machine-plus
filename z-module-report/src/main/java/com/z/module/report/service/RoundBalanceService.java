package com.z.module.report.service;

import com.z.module.report.domain.ReportData;
import com.z.module.report.domain.ReportFormula;
import com.z.module.report.domain.ReportRoundBalance;
import com.z.module.report.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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

/**
 * @Title: null.java
 * @Package: gov.mof.fasp2.gfbi.common.roundbalance
 * @Description: TODO
 * @author: zhao
 * @date: 2023/10/14 上午10:12
 * @version: V1.0
 */
@Service
@Slf4j
public class RoundBalanceService {

    private final CalRuleService calRuleService;
    private final ReportFormulaRepository reportFormulaRepository;
    private final ReportItemRepository reportItemRepository;
    private final ReportFieldRepository reportFieldRepository;
    private final ReportDataRepository reportDataRepository;
    private final CheckRuleService checkRuleService;

    private final ReportRoundBalanceRepository reportRoundBalanceRepository;

    public RoundBalanceService(CalRuleService calRuleService, ReportFormulaRepository reportFormulaRepository, ReportItemRepository reportItemRepository, ReportFieldRepository reportFieldRepository, ReportDataRepository reportDataRepository, CheckRuleService checkRuleService, ReportRoundBalanceRepository reportRoundBalanceRepository) {
        this.calRuleService = calRuleService;
        this.reportFormulaRepository = reportFormulaRepository;
        this.reportItemRepository = reportItemRepository;
        this.reportFieldRepository = reportFieldRepository;
        this.reportDataRepository = reportDataRepository;
        this.checkRuleService = checkRuleService;
        this.reportRoundBalanceRepository = reportRoundBalanceRepository;
    }

    /**
     * @param map1 : 根据单据id, 获取报表类型， 从而来确定要计算的报表范围
     * @param type : 预算执行/总决算
     * @date: 2022/11/18-上午11:12
     * @author: zhao
     * @method: roundBalance
     * @return: void
     * @Description: 根据计算公式层次进行舍位平衡
     * 19164 舍位平衡优化改进 20231014
     * 任务描述
     * 1、按照报表的计算公式去分析判断出根节点的公式；
     * 2、再由根节点公式去查找其子项进行逐层舍位平衡；
     * 3、其间被舍位平衡后的单元格，不允许再被改动（通过打标记进行判断）；
     * 4、对于舍位平衡前就是整数的，不允许参与平衡；
     * 5、优先选取舍位前后差异最小的金额项进行平衡；
     * 6、舍位平衡是基于整套报表的；
     * 没有作为计算单元的不进行舍位平衡
     */
    public void roundBalance(HashMap map1, String type) {

        List<String> reportCodeList = Collections.singletonList(map1.get("report_code").toString());

        List<ReportRoundBalance> list = reportRoundBalanceRepository.findAllByReportCodeIn(reportCodeList);
        if (!list.isEmpty()) {
            throw new RuntimeException("已经进行过舍位平衡！");
        }

        // 记录舍位前原始数据, 最原始数据(不允许重复舍位平衡)
        List<ReportData> datas = reportDataRepository.findAllByReportCodeIn(reportCodeList);
        List<ReportRoundBalance> roundBalanceDatas = datas.stream().map(reportData -> {
            ReportRoundBalance reportRoundBalance = new ReportRoundBalance();
            BeanUtils.copyProperties(reportData, reportRoundBalance);
            reportRoundBalance.setId(null);
            return reportRoundBalance;
        }).toList();
        reportRoundBalanceRepository.saveAll(roundBalanceDatas);
        long lst = System.currentTimeMillis();
        // 2. 获取所有计算公式,根据计算公式层级进行平衡
        // 获取计算公式formula_type 2:计算
        List<ReportFormula> calRuleList = reportFormulaRepository.findAllByReportCodeInAndFormulaType(reportCodeList, "2");
        calRuleService.transFormula(calRuleList);

        // 增加map表示, 记录单元格及其公式
        final Map<String, String> fieldCalRuleMap = new HashMap<>();
        for (ReportFormula map : calRuleList) {
            final String reportCode = map.getReportCode();
            final String reportItemCode = map.getItemCode();
            final String fieldCode = map.getFieldCode();
            final String key = String.format("#%s['%s:%s']", reportCode, reportItemCode, fieldCode);
            fieldCalRuleMap.put(key, map.getFormulaExp());
        }
        // 2.2 查找所有没有作为计算单元, 并且是计算结果的项(顶层), 分别对每一个进行平衡, 如果被平衡项存在下级则对下级也进行平衡　
        final long l = System.currentTimeMillis();
        final Map<String, BigDecimal> datasConcurrentMap = listToMap(datas);
        // 记录已经参与过平衡的, 打标记为1, 默认为null
        final Map<String, String> roundBalancedMap = new HashMap<>();
        log.info("构建listToMap耗时: {}", (System.currentTimeMillis() - l));
        Set<String> valueSet = new HashSet<>(fieldCalRuleMap.values());
        for (ReportFormula calRuleMap : calRuleList) {
            final Object reportCode = calRuleMap.getReportCode();
            final Object reportItemCode = calRuleMap.getItemCode();
            final Object fieldCode = calRuleMap.getFieldCode();
            final String key = String.format("#%s['%s:%s']", reportCode, reportItemCode, fieldCode);
            // 如果不是计算单项(key不在fieldCalRuleMap的value中)则当前单元格为最顶层,开始进行平衡
            boolean containsValue = valueSet.contains(key);
            if(!containsValue){
                doRoundBalance(datasConcurrentMap, fieldCalRuleMap, roundBalancedMap, calRuleMap);
            }
        }

        log.info("舍位平衡计算耗时: {}", (System.currentTimeMillis() - l));

        // 2.4 数据库更新
        //3. 平衡后的数据回写到data中
        datas.parallelStream().forEach(data -> {
            // 金额列进行数据更新
            final Object reportCode = data.getReportCode();
            final Object reportItemCode = data.getItemCode();
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(ReportData.class);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    String key = propertyDescriptor.getName();
                    if(key.contains("je")){
                        final String s = String.format("#%s['%s:%s']", reportCode, reportItemCode, key);
                        // 19164 舍位平衡后不允许存在含有小数位的项目金额(不在计算单元中数据直接四舍五入)
                        final BigDecimal result = datasConcurrentMap.get(s.toLowerCase());
                        // 舍位平衡已经平衡了大部分数据, 全部四舍五入不影响舍位后数据
                        final BigDecimal halfUpJe = result.setScale(0, RoundingMode.HALF_UP);
                        Method writeMethod = propertyDescriptor.getWriteMethod();
                        writeMethod.invoke(data, halfUpJe);
                    }
                }

            } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        reportDataRepository.saveAll(datas);

        // 2.3 全部处理完成后, 将数据回写业务表, 并进行全局计算
        long ls = System.currentTimeMillis();
        log.info("舍位平衡计算总耗时: {}", ls - lst);
    }

    /**
     * @param dataList :
     * @data: 2023/10/14-下午3:00
     * @User: zhao
     * @method: listToMap
     * @return: java.util.Map<java.lang.String, java.util.Map < java.lang.String, java.lang.Object>>
     * @Description: 将datas转换为map表示
     */
    private Map<String, BigDecimal> listToMap(List<ReportData> dataList) {
        final Map<String, BigDecimal> result = new ConcurrentHashMap<>();

        dataList.parallelStream().forEach(map -> {
            String reportCode = map.getReportCode().toLowerCase();
            String reportItemCode = map.getItemCode().toLowerCase();

            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(ReportData.class);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    String name = propertyDescriptor.getName();
                    if (JE_PATTERN.matcher(name).matches()) {
                        final String matchUnitItem = String.format("#%s['%s:%s']", reportCode, reportItemCode, name);
                        result.put(matchUnitItem, (BigDecimal) propertyDescriptor.getReadMethod().invoke(map));
                    }
                }
            } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        return result;
    }


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
     * SUM(#CZYBGGYS (JB )ZCJSJJFLLR['CZYBGGYS (JB )ZCJSJJFLLR0013:JE3'],#CZYBGGYS (JB )ZCJSJJFLLR['CZYBGGYS (JB )
     * ZCJSJJFLLR0014:JE3'])
     */
    private static final Pattern ITEM_PATTERN = Pattern.compile("(\\#.*?\\[[^*+-/!=<>]+\\])");

    private void doRoundBalance(Map<String, BigDecimal> dataMap, Map<String, String> fieldCalRuleMap,
                                Map<String, String> roundBalancedMap, ReportFormula calRuleMap) {
        // 获取当前公式, 获取当前单元格对应的数据
        final Object reportCode = calRuleMap.getReportCode();
        final Object reportItemCode = calRuleMap.getItemCode();
        final Object fieldCode = calRuleMap.getFieldCode();
        final String key = "#" + reportCode + "['" + reportItemCode + ":" + fieldCode + "']";
        // 1. 根据运算符将公式拆分,分别对数据进行四舍五入并进行平衡, 如果参与平衡项存在下级,则同时对下级进行平衡
        BigDecimal je = dataMap.get(key.toLowerCase());
        if(Objects.isNull(je)){
            log.error("数据项 {} 缺少金额值, 检查数据是否保存, 或注意nonadmindiv值", key);
            je = BigDecimal.ZERO;
        }
        final BigDecimal halfUpJe = je.setScale(0, RoundingMode.HALF_UP);
        // 父项进行舍位平衡
        if (!roundBalancedMap.containsKey(key)) {
            dataMap.put(key.toLowerCase(), halfUpJe);
            roundBalancedMap.put(key, "1");
        }

        final String formulaExp = String.valueOf(calRuleMap.getFormulaExp());
        // 解析出公式涉及的所有对象, 平衡的子项
        final Set<String> formulaItems = new HashSet<>();
        Matcher matcher = ITEM_PATTERN.matcher(formulaExp);
        while (matcher.find()) {
            String group = matcher.group();
            formulaItems.add(group.toLowerCase());
        }

        // 计算子节点四舍五入后的金额合计
        BigDecimal childHalfUpJeSum = new BigDecimal(0);
        // 参与平衡的子项
        final Map<String, BigDecimal> childReportItemMap = new LinkedHashMap<>();
        for (String formulaItem : formulaItems) {
            BigDecimal bigDecimal = dataMap.get(formulaItem);
            if(Objects.isNull(bigDecimal)){
                log.error("数据项 {} 缺少金额值, 检查数据是否保存, 或注意nonadmindiv值", formulaItem);
                bigDecimal = BigDecimal.ZERO;
            }
            // 子节点四舍五入
            final BigDecimal bigDecimalHalfUp = bigDecimal.setScale(0, RoundingMode.HALF_UP);
            childHalfUpJeSum = childHalfUpJeSum.add(bigDecimalHalfUp);
            // 0和整数(不包含小数点)不参与平衡
            if (bigDecimal.compareTo(BigDecimal.ZERO) != 0 && String.valueOf(bigDecimal).contains(".")) {
                childReportItemMap.put(formulaItem, bigDecimal);
            }
        }

//          4. 根据原合计四舍五入与 子节点四舍五入的差值作为平衡差
        boolean parentGtSubCount = true;
        BigDecimal subtract = halfUpJe.subtract(childHalfUpJeSum);
        if (subtract.compareTo(BigDecimal.ZERO) < 0) {
            // 合计小于明细
            parentGtSubCount = false;
        }

        // 优先选取舍位前后差异最小的金额项进行平衡 (根据舍位后差值正向排序, 差异最小优先舍位)
        // 23013 舍位平衡变更计算逻辑：使用用加1或者减1以后，再去计算偏离度, 差异小的优先舍位(核心为结果跟原值差异最小)
        boolean finalParentGtSubCount = parentGtSubCount;
        List<Map.Entry<String, BigDecimal>> sortedList = childReportItemMap.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> {
                    BigDecimal value = entry.getValue();
                    BigDecimal roundedValue = value.setScale(0, RoundingMode.HALF_UP);
                    if(finalParentGtSubCount){
                        // 明细需+1的情况, 先增加跟原值比较，变化较小优先处理
                        return roundedValue.add(BigDecimal.ONE).subtract(value).abs();
                    }
                    // 明细需-1的情况, 反之
                    return value.subtract(roundedValue.subtract(BigDecimal.ONE)).abs();
                }))
                .peek(m -> {
                    // 排序后对结果直接进行四舍五入, 后续就不需要再去舍位
                    final BigDecimal value = m.getValue();
                    final BigDecimal bigDecimalHalfUp = value.setScale(0, RoundingMode.HALF_UP);
                    m.setValue(bigDecimalHalfUp);
                })
                .collect(Collectors.toList());
//            4. 计算出最小调整值 这里取整, 调整值为1
//            5. 处理数据, 将平衡差拆分到记录中前几个数据中, 处理当前级次下数据(除了金额为0，不进行处理)
        final Map<String, BigDecimal> childBalancedLinkedMap = new LinkedHashMap<>();
        for (int i = 0; i < subtract.abs().intValue(); i++) {
            if (parentGtSubCount) {
                for (Map.Entry<String, BigDecimal> m : sortedList) {
                    if (!roundBalancedMap.containsKey(m.getKey().toUpperCase())) {
                        // 差值大于0, 分别给每个要素去 +1
                        childBalancedLinkedMap.put(m.getKey(), m.getValue().add(BigDecimal.ONE));
                        roundBalancedMap.put(m.getKey().toUpperCase(), "1");
                        break;
                    }
                }
            } else {
                for (Map.Entry<String, BigDecimal> m : sortedList) {
                    if (!roundBalancedMap.containsKey(m.getKey().toUpperCase())) {
                        // 差值小于0, 分别给每个要素去 -1
                        // 128317 【安徽-刘丽莉-15375345053】2月财政收支月报舍位平衡问题,舍位出了负数, 0.03变成-1bug
                        // 131979 【测试2.0】财政总决算>>录入表，项目金额含有负数的，舍位平衡金额不对，跟节点舍位后不应该再计算
                        if(m.getValue().compareTo(BigDecimal.ZERO) != 0){
                            childBalancedLinkedMap.put(m.getKey(), m.getValue().subtract(BigDecimal.ONE));
                            //BUG--121417 舍位平衡应该平衡子项，对于最顶端的值应该只是进行舍位
                            //子相平衡后需要打个已舍位的标记。要不然按照差值次数循环执行，都是重复针对同一个子项，并且差值分配下来，实际分配的值=1
                            roundBalancedMap.put(m.getKey().toUpperCase(),"1");
                            break;
                        }
                    }
                }
            }
        }

        // 平衡后的数据回写dataMap
        dataMap.putAll(childBalancedLinkedMap);

        // 2. 平衡完成后对单元格进行标记,下次不再进行平衡
        for (String formulaItem : formulaItems) {
            roundBalancedMap.put(formulaItem.toUpperCase(), "1");
        }

        for (String formulaItem : formulaItems) {
            if (fieldCalRuleMap.containsKey(formulaItem.toUpperCase()) && childReportItemMap.containsKey(formulaItem)) {
                // 如果包含子公式, 并且子项要参与平衡的继续去遍历
                final String[] split = formulaItem.toUpperCase().split("\\[");
                final String[] split1 = split[1].split(":");
                // 对明细项进行平衡(存在于fieldCalRuleMap.key中的)
                final ReportFormula subCalRuleMap = new ReportFormula();
                subCalRuleMap.setReportCode(split[0].replaceAll("#", "").toUpperCase());
                subCalRuleMap.setItemCode(split1[0].replaceAll("'", "").toUpperCase());
                subCalRuleMap.setFieldCode(split1[1].replaceAll("'", "").replaceAll("\\]", "").toUpperCase());
                subCalRuleMap.setFormulaExp(fieldCalRuleMap.get(formulaItem.toUpperCase()));
                doRoundBalance(dataMap, fieldCalRuleMap, roundBalancedMap, subCalRuleMap);
            }
        }
    }

    private static final Pattern JE_PATTERN = Pattern.compile("je[0-9]+");

    public void cancelRoundBalance(HashMap map2, String type){

        // TODO 获取所有需舍位平衡的报表
        List<String> reportCodeList = new ArrayList<>();
        List<ReportRoundBalance> oldList = reportRoundBalanceRepository.findAllByReportCodeIn(reportCodeList);
        if (oldList.isEmpty()) {
            throw new RuntimeException("未进行过舍位平衡操作！");
        }

        reportDataRepository.deleteAllByReportCodeIn(reportCodeList);
        List<ReportData> list = oldList.stream().map(reportRoundBalance -> {
            ReportData reportData = new ReportData();
            BeanUtils.copyProperties(reportRoundBalance, reportData);
            return reportData;
        }).toList();
        reportDataRepository.saveAll(list);
        // 删除舍位平衡数据
        reportRoundBalanceRepository.deleteAllByReportCodeIn(reportCodeList);
    }

}