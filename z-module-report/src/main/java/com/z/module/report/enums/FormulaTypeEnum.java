package com.z.module.report.enums;

/**
 * @Title: FormulaTypeEnum
 * @Package gov/mof/fasp2/gfbi/common/enums/FormulaTypeEnum.java
 * @Description: 公式类型
 * formula_type 2:计算, 1:取数, 0:校验
 * @author zhao
 * @date 2024/6/29 下午4:19
 * @version V1.0
 */
public enum FormulaTypeEnum {
    EXTRACT_FORMULA(1, "取数公式"),
    CHECK_FORMULA(2, "计算公式"),
    CAL_FORMULA(0, "校验公式")
    ;
    private final int num;
    private final String des;

    FormulaTypeEnum(int num, String des) {
        this.num = num;
        this.des = des;
    }
}
