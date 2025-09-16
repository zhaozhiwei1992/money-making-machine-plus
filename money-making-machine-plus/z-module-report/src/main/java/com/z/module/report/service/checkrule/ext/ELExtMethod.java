package com.z.module.report.service.checkrule.ext;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @Title: null.java
 * @Package: gov.mof.fasp2.gfbi.common.rule.ext
 * @Description: 增加el扩展函数实现
 * @author: zhao
 * @date: 2023/6/29 上午10:17
 * @version: V1.0
 */
public class ELExtMethod {

    /**
     * @data: 2023/6/29-上午10:18
     * @User: zhao
     * @method: sum
      * @param param :
     * @return: java.math.BigDecimal
     * @Description: 求和公式, 任意个数字
     *    @Value("#{T(gov.mof.fasp2.gfbi.common.rule.ext.ELExtMethod).sum(1.0,20,30,1.11)}")
     *     private BigDecimal sumNum;
     */
    public static BigDecimal sum(BigDecimal ...param){
        return Arrays.stream(param).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @data: 2023/7/24-下午2:43
     * @User: zhao
     * @method: abs
      * @param value :
     * @return: java.math.BigDecimal
     * @Description: 计算绝对值
     */
    public static BigDecimal abs(BigDecimal value){
        return value.abs();
    }

    /**
     * @data: 2023/7/22-下午2:45
     * @User: zhao
     * @method: AVSUBSTR
     * @param source :
     * @param start :
     * @param end :
     * @return: java.lang.String
     * @Description: 截取字符串
     */
    public static String substr(String source, int start, int end){
        return source.substring(start, end);
    }

    /**
     * @data: 2023/7/22-下午2:44
     * @User: zhao
     * @method: SCOPE
     * @param preExp : 前条件结果
     * @param endExp : 后条件结果
     * @return: boolean
     * @Description:
     * 需求调整, 如果preExp为真才校验后边的, 前条件为假则直接返回
     * 122092 校验公式功能逻辑调整
     */
    public static Boolean scope(Boolean preExp, Boolean endExp){
        if(Boolean.FALSE.equals(preExp)){
            return Boolean.TRUE;
        }else{
            return endExp;
        }
    }

    /**
     * @data: 2023/7/25-上午10:35
     * @User: zhao
     * @method: xif
      * @param exp :
 * @param v1 :
 * @param v2 :
     * @return: java.math.BigDecimal
     * @Description: 处理公式中if函数,
     * 如: I8<>IF(J8<>0,0,(I8+1))        决算数不等于零时，调整预算数也不等于零
     */
    public static BigDecimal xif(Boolean exp, BigDecimal v1, BigDecimal v2){
        return exp ? v1 : v2;
    }

    /**
     * @User: zhao
     * @method: mod
     * @param v1 :
     * @param v2 :
     * @return: java.math.BigDecimal
     * @Description: 取余
     */
    public static BigDecimal mod(BigDecimal v1, BigDecimal v2){
        return v1.divideAndRemainder(v2)[1];
    }

    /**
     * @data: 2023/8/4-上午10:41
     * @User: zhao
     * @method: max
      * @param param :
     * @return: java.math.BigDecimal
     * @Description: 求最大值
     */
    public static BigDecimal max(BigDecimal ...param){
        return Arrays.stream(param).max(BigDecimal::compareTo).get();
    }

    /**
     * @data: 2023/8/4-上午10:41
     * @User: zhao
     * @method: max
     * @param param :
     * @return: java.math.BigDecimal
     * @Description: 求最小值
     */
    public static BigDecimal min(BigDecimal ...param){
        return Arrays.stream(param).min(BigDecimal::compareTo).get();
    }
}
