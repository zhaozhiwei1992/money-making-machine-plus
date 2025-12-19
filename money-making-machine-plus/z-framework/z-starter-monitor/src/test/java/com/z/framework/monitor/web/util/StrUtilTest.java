package com.z.framework.monitor.web.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StrUtil 工具类的单元测试
 */
class StrUtilTest {

    @Test
    void testIsBlank() {
        // 测试null值
        assertTrue(StrUtil.isBlank(null));

        // 测试空字符串
        assertTrue(StrUtil.isBlank(""));

        // 测试只有空格的字符串
        assertTrue(StrUtil.isBlank(" "));
        assertTrue(StrUtil.isBlank("   "));

        // 测试包含空白字符的字符串
        assertTrue(StrUtil.isBlank("\t\n\r"));

        // 测试包含非空白字符的字符串
        assertFalse(StrUtil.isBlank("a"));
        assertFalse(StrUtil.isBlank(" a "));
        assertFalse(StrUtil.isBlank("abc"));
    }

    @Test
    void testIsBlankChar() {
        // 测试常见的空白字符
        assertTrue(StrUtil.isBlankChar(' '));  // 普通空格
        assertTrue(StrUtil.isBlankChar('\t')); // 制表符
        assertTrue(StrUtil.isBlankChar('\n')); // 换行符
        assertTrue(StrUtil.isBlankChar('\r')); // 回车符
        assertTrue(StrUtil.isBlankChar(65279)); // ZERO WIDTH NO-BREAK SPACE
        assertTrue(StrUtil.isBlankChar(8234));  // LEFT-TO-RIGHT OVERRIDE

        // 测试非空白字符
        assertFalse(StrUtil.isBlankChar('a'));
        assertFalse(StrUtil.isBlankChar('1'));
        assertFalse(StrUtil.isBlankChar('@'));
    }

    @Test
    void testTrimToNull() {
        // 测试null值
        assertNull(StrUtil.trimToNull(null));

        // 测试空字符串和只包含空白字符的字符串
        assertNull(StrUtil.trimToNull(""));
        assertNull(StrUtil.trimToNull(" "));
        assertNull(StrUtil.trimToNull("   "));
        assertNull(StrUtil.trimToNull("\t\n\r"));

        // 测试包含非空白字符的字符串
        assertEquals("a", StrUtil.trimToNull("a"));
        assertEquals("a", StrUtil.trimToNull(" a "));
        assertEquals("abc", StrUtil.trimToNull(" abc "));
        assertEquals("abc def", StrUtil.trimToNull(" abc def "));
    }

    @Test
    void testTrim() {
        // 测试null值
        assertNull(StrUtil.trim(null));

        // 测试空字符串
        assertEquals("", StrUtil.trim(""));

        // 测试只包含空白字符的字符串
        assertEquals("", StrUtil.trim(" "));
        assertEquals("", StrUtil.trim("   "));
        assertEquals("", StrUtil.trim("\t\n\r"));

        // 测试包含非空白字符的字符串
        assertEquals("a", StrUtil.trim("a"));
        assertEquals("a", StrUtil.trim(" a "));
        assertEquals("abc", StrUtil.trim(" abc "));
        assertEquals("abc def", StrUtil.trim(" abc def "));
        assertEquals("abc", StrUtil.trim("\t\n abc \r"));
    }

    @Test
    void testTrimWithMode() {
        String testStr = " abc ";

        // 测试mode <= 0的情况 (去除开头空白)
        assertEquals("abc ", StrUtil.trim(testStr, -1));

        // 测试mode = 0的情况 (去除两端空白)
        assertEquals("abc", StrUtil.trim(testStr, 0));

        // 测试mode >= 0的情况 (去除结尾空白)
        assertEquals(" abc", StrUtil.trim(testStr, 1));

        // 测试null值
        assertNull(StrUtil.trim(null, 0));

        // 测试空字符串
        assertEquals("", StrUtil.trim("", 0));
    }
}