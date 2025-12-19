
package com.z.framework.common.util.collection;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionUtilsTest {

    @Test
    public void testContainsAny() {
        assertTrue(CollectionUtils.containsAny("a", "a", "b", "c"));
        assertFalse(CollectionUtils.containsAny("d", "a", "b", "c"));
    }

    @Test
    public void testIsAnyEmpty() {
        assertTrue(CollectionUtils.isAnyEmpty(new ArrayList<>(), Arrays.asList("a")));
        assertFalse(CollectionUtils.isAnyEmpty(Arrays.asList("a"), Arrays.asList("b")));
    }

    @Test
    public void testFilterList() {
        List<String> list = Arrays.asList("a", "b", "c");
        List<String> filtered = CollectionUtils.filterList(list, s -> s.equals("b"));
        assertEquals(1, filtered.size());
        assertEquals("b", filtered.get(0));
    }

    @Test
    public void testDistinct() {
        List<String> list = Arrays.asList("a", "b", "a");
        List<String> distinct = CollectionUtils.distinct(list, s -> s);
        assertEquals(2, distinct.size());
    }

    @Test
    public void testConvertList() {
        List<String> list = Arrays.asList("1", "2", "3");
        List<Integer> converted = CollectionUtils.convertList(list, Integer::parseInt);
        assertEquals(3, converted.size());
        assertEquals(1, converted.get(0).intValue());
    }

    @Test
    public void testConvertSet() {
        List<String> list = Arrays.asList("1", "2", "1");
        Set<Integer> converted = CollectionUtils.convertSet(list, Integer::parseInt);
        assertEquals(2, converted.size());
    }

    @Test
    public void testConvertMap() {
        List<String> list = Arrays.asList("a", "b");
        Map<String, String> map = CollectionUtils.convertMap(list, s -> s);
        assertEquals(2, map.size());
        assertEquals("a", map.get("a"));
    }

    @Test
    public void testConvertMultiMap() {
        List<String> list = Arrays.asList("a", "b", "a");
        Map<String, List<String>> map = CollectionUtils.convertMultiMap(list, s -> s);
        assertEquals(2, map.size());
        assertEquals(2, map.get("a").size());
    }
}
