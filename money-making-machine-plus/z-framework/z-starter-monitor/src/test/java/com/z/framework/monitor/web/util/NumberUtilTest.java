package com.z.framework.monitor.web.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class NumberUtilTest {

    // 加法测试
    @Test
    void testAdd() {
        assertEquals(5.0, NumberUtil.add(2.0, 3.0));
        assertEquals(5.0, NumberUtil.add(2.0f, 3.0f));
        assertEquals(5.0, NumberUtil.add(2.0, 3.0f));
        assertEquals(5.0, NumberUtil.add(2.0f, 3.0));

        assertEquals(BigDecimal.valueOf(5.0), NumberUtil.add(BigDecimal.valueOf(2.0), BigDecimal.valueOf(3.0)));
        assertEquals(BigDecimal.valueOf(6), NumberUtil.add(1, 2, 3));
    }

    // 减法测试
    @Test
    void testSub() {
        assertEquals(1.0, NumberUtil.sub(3.0, 2.0));
        assertEquals(1.0, NumberUtil.sub(3.0f, 2.0f));
        assertEquals(1.0, NumberUtil.sub(3.0, 2.0f));
        assertEquals(1.0, NumberUtil.sub(3.0f, 2.0));

        assertEquals(BigDecimal.valueOf(1.0), NumberUtil.sub(BigDecimal.valueOf(3.0), BigDecimal.valueOf(2.0)));
        assertEquals(BigDecimal.valueOf(-4), NumberUtil.sub(1, 2, 3));
    }

    // 乘法测试
    @Test
    void testMul() {
        assertEquals(6.00, NumberUtil.mul(2.0, 3.0));
        assertEquals(6.00, NumberUtil.mul(2.0f, 3.0f));
        assertEquals(6.00, NumberUtil.mul(2.0, 3.0f));
        assertEquals(6.00, NumberUtil.mul(2.0f, 3.0));

        assertEquals(new BigDecimal("6.00"), NumberUtil.mul(BigDecimal.valueOf(2.0), BigDecimal.valueOf(3.0)));
        assertEquals(BigDecimal.valueOf(24), NumberUtil.mul(2, 3, 4));
    }

    // 除法测试
    @Test
    void testDiv() {
        assertEquals(2.0, NumberUtil.div(6.0, 3.0));
        assertEquals(2.0, NumberUtil.div(6.0f, 3.0f));
        assertEquals(2.0, NumberUtil.div(6.0, 3.0f));
        assertEquals(2.0, NumberUtil.div(6.0f, 3.0));

        assertEquals(BigDecimal.valueOf(2.0), NumberUtil.div(BigDecimal.valueOf(6.0), BigDecimal.valueOf(3.0), 1));
    }

    // 四舍五入测试
    @Test
    void testRound() {
        assertEquals(BigDecimal.valueOf(2.35), NumberUtil.round(2.345, 2));
        assertEquals(BigDecimal.valueOf(2), NumberUtil.round(2.345, 0));
        assertEquals("2.35", NumberUtil.roundStr(2.345, 2));
    }

    // 数字判断测试
    @ParameterizedTest
    @ValueSource(strings = {"123", "12.34", "-56", "0", "1.23E4"})
    void testIsNumberValid(String input) {
        assertTrue(NumberUtil.isNumber(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "", "12.34.56", "12a34"})
    void testIsNumberInvalid(String input) {
        assertFalse(NumberUtil.isNumber(input));
    }

    // 整数判断测试
    @ParameterizedTest
    @ValueSource(strings = {"123", "-456", "0"})
    void testIsIntegerValid(String input) {
        assertTrue(NumberUtil.isInteger(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12.34", "abc", ""})
    void testIsIntegerInvalid(String input) {
        assertFalse(NumberUtil.isInteger(input));
    }

    // 长整数判断测试
    @ParameterizedTest
    @ValueSource(strings = {"123", "-456", "0", "1234567890123"})
    void testIsLongValid(String input) {
        assertTrue(NumberUtil.isLong(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12.34", "abc", ""})
    void testIsLongInvalid(String input) {
        assertFalse(NumberUtil.isLong(input));
    }

    // 质数判断测试
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 19})
    void testIsPrimesTrue(int input) {
        assertTrue(NumberUtil.isPrimes(input));
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 6, 8, 9, 10, 12, 14})
    void testIsPrimesFalse(int input) {
        assertFalse(NumberUtil.isPrimes(input));
    }

    // 比较方法测试
    @Test
    void testCompare() {
        assertEquals(0, NumberUtil.compare(5, 5));
        assertTrue(NumberUtil.compare(5, 3) > 0);
        assertTrue(NumberUtil.compare(3, 5) < 0);
    }

    // 大小比较测试
    @Test
    void testBigDecimalComparison() {
        BigDecimal a = BigDecimal.valueOf(5);
        BigDecimal b = BigDecimal.valueOf(3);
        BigDecimal c = BigDecimal.valueOf(5);

        assertTrue(NumberUtil.isGreater(a, b));
        assertFalse(NumberUtil.isGreater(b, a));

        assertTrue(NumberUtil.isGreaterOrEqual(a, b));
        assertTrue(NumberUtil.isGreaterOrEqual(a, c));

        assertFalse(NumberUtil.isLess(a, b));
        assertTrue(NumberUtil.isLess(b, a));
        assertTrue(NumberUtil.isLessOrEqual(b, a));
        assertTrue(NumberUtil.isLessOrEqual(c, a));
    }

    // 相等性测试
    @Test
    void testEquals() {
        assertTrue(NumberUtil.equals(5.0, 5.0));
        assertTrue(NumberUtil.equals(3.0f, 3.0f));

        BigDecimal a = BigDecimal.valueOf(5);
        BigDecimal b = BigDecimal.valueOf(5);
        assertTrue(NumberUtil.equals(a, b));
    }

    // 转换测试
    @Test
    void testToBigDecimal() {
        Number num = null;
        assertEquals(BigDecimal.ZERO, NumberUtil.toBigDecimal(num));
        assertEquals(BigDecimal.valueOf(5), NumberUtil.toBigDecimal(5));
        assertEquals(BigDecimal.valueOf(5.5), NumberUtil.toBigDecimal(5.5));
        assertEquals(BigDecimal.valueOf(5), NumberUtil.toBigDecimal("5"));
    }

    // 范围测试
    @Test
    void testRange() {
        assertArrayEquals(new int[]{0, 1, 2, 3}, NumberUtil.range(3));
        assertArrayEquals(new int[]{1, 2, 3, 4}, NumberUtil.range(1, 4));
        assertArrayEquals(new int[]{1, 3}, NumberUtil.range(1, 4, 2));
    }

    // 最大公约数和最小公倍数测试
    @ParameterizedTest
    @CsvSource({
            "12, 8, 4",
            "15, 10, 5",
            "7, 5, 1"
    })
    void testDivisor(int m, int n, int expected) {
        assertEquals(expected, NumberUtil.divisor(m, n));
    }

    @ParameterizedTest
    @CsvSource({
            "12, 8, 24",
            "15, 10, 30",
            "7, 5, 35"
    })
    void testMultiple(int m, int n, int expected) {
        assertEquals(expected, NumberUtil.multiple(m, n));
    }

    // 幂运算测试
    @Test
    void testPow() {
        assertEquals(BigDecimal.valueOf(8), NumberUtil.pow(2, 3));
        assertEquals(BigDecimal.valueOf(1), NumberUtil.pow(5, 0));
    }

    // 二进制转换测试
    @Test
    void testBinaryConversion() {
        assertEquals("101", NumberUtil.getBinaryStr(5));
        assertEquals(5, NumberUtil.binaryToInt("101"));
    }

    // 特殊值处理测试
    @Test
    void testSpecialValues() {
        assertEquals(BigDecimal.ZERO, NumberUtil.null2Zero(null));
        assertEquals(BigDecimal.valueOf(5), NumberUtil.null2Zero(BigDecimal.valueOf(5)));

        assertEquals(1, NumberUtil.zero2One(0));
        assertEquals(5, NumberUtil.zero2One(5));
    }
}
