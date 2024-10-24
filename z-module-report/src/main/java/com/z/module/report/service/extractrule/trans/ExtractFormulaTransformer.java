package com.z.module.report.service.extractrule.trans;

import java.util.Map;

/**
 * @Title: CheckFormulaTransformer
 * @Package gov/mof/fasp2/gfbi/common/rule/trans/CheckFormulaTransformer.java
 * @Description: 系统内部公式转换接口, 方便后续扩展
 * @author zhao
 * @date 2024/7/8 上午10:36
 * @version V1.0
 */
public interface ExtractFormulaTransformer {
    String transform(String expression, Map<String, String> formatMapABC);
}