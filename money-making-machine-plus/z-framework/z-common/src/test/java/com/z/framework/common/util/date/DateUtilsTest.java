
package com.z.framework.common.util.date;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilsTest {

    @Test
    public void testOfLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        Date date = DateUtils.of(now);
        assertNotNull(date);
    }

    @Test
    public void testOfDate() {
        Date now = new Date();
        LocalDateTime ldt = DateUtils.of(now);
        assertNotNull(ldt);
    }

    @Test
    public void testAddTime() {
        Date date = DateUtils.addTime(Duration.ofHours(1));
        assertTrue(date.after(new Date()));
    }

    @Test
    public void testIsExpired() {
        Date past = new Date(System.currentTimeMillis() - 1000);
        assertTrue(DateUtils.isExpired(past));

        Date future = new Date(System.currentTimeMillis() + 1000);
        assertFalse(DateUtils.isExpired(future));
    }

    @Test
    public void testIsExpiredLocalDateTime() {
        LocalDateTime past = LocalDateTime.now().minusHours(1);
        assertTrue(DateUtils.isExpired(past));

        LocalDateTime future = LocalDateTime.now().plusHours(1);
        assertFalse(DateUtils.isExpired(future));
    }

    @Test
    public void testDiff() {
        Date d1 = new Date();
        Date d2 = new Date(d1.getTime() + 1000);
        assertEquals(1000, DateUtils.diff(d2, d1));
    }

    @Test
    public void testBuildTime() {
        Date date = DateUtils.buildTime(2023, 1, 1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(2023, cal.get(Calendar.YEAR));
        assertEquals(0, cal.get(Calendar.MONTH)); // Month is 0-based
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testMax() {
        Date d1 = new Date();
        Date d2 = new Date(d1.getTime() + 1000);
        assertEquals(d2, DateUtils.max(d1, d2));
    }

    @Test
    public void testIsToday() {
        assertTrue(DateUtils.isToday(LocalDateTime.now()));
        assertFalse(DateUtils.isToday(LocalDateTime.now().minusDays(1)));
    }
}
