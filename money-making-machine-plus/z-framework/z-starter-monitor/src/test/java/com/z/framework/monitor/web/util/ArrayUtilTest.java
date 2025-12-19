package com.z.framework.monitor.web.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilTest {

    @Test
    public void testIsNotEmpty() {
        assertTrue(ArrayUtil.isNotEmpty(new Object[]{"a", "b"}));
        assertFalse(ArrayUtil.isNotEmpty(new Object[]{}));
        assertFalse(ArrayUtil.isNotEmpty(null));
    }

    @Test
    public void testIsEmpty() {
        // 测试空数组
        assertTrue(ArrayUtil.isEmpty(new Object[]{}));

        // 测试null数组
        assertTrue(ArrayUtil.isEmpty(null));

        // 测试非空数组
        assertFalse(ArrayUtil.isEmpty(new Object[]{"a", "b"}));
    }

}
