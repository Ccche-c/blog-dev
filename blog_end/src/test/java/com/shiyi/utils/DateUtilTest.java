package com.shiyi.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateUtil 单元测试
 *
 * @author shiyi
 * @date 2024/01/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("日期工具类测试")
class DateUtilTest {

    // ==================== getNowTime 方法测试 ====================

    @Test
    @DisplayName("测试获取当前时间字符串")
    void testGetNowTime() {
        String result = DateUtil.getNowTime();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"), "格式应为 yyyy-MM-dd HH:mm:ss");
    }

    // ==================== getNowDate 方法测试 ====================

    @Test
    @DisplayName("测试获取当前Date对象")
    void testGetNowDate() {
        Date result = DateUtil.getNowDate();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.getTime() <= System.currentTimeMillis(), "时间不应超过当前时间");
    }

    // ==================== str2Date 方法测试 ====================

    @Test
    @DisplayName("测试字符串转Date-成功")
    void testStr2Date_Success() {
        String dateString = "Mon Jan 01 2024 12:00:00 GMT+08:00";
        Date result = DateUtil.str2Date(dateString);
        assertNotNull(result, "返回结果不应为null");
    }

    @Test
    @DisplayName("测试字符串转Date-包含中国标准时间")
    void testStr2Date_WithSplitString() {
        String dateString = "Mon Jan 01 2024 12:00:00 GMT+08:00(中国标准时间)";
        Date result = DateUtil.str2Date(dateString);
        assertNotNull(result, "返回结果不应为null");
    }

    @Test
    @DisplayName("测试字符串转Date-异常情况")
    void testStr2Date_Exception() {
        String invalidDateString = "invalid date string";
        assertThrows(RuntimeException.class, () -> DateUtil.str2Date(invalidDateString), "应抛出RuntimeException");
    }

    // ==================== getToDayStartTime 方法测试 ====================

    @Test
    @DisplayName("测试获取今天开始时间")
    void testGetToDayStartTime() {
        String result = DateUtil.getToDayStartTime();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 00:00:00"), "应以 00:00:00 结尾");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} 00:00:00"), "格式应为 yyyy-MM-dd 00:00:00");
    }

    // ==================== getToDayEndTime 方法测试 ====================

    @Test
    @DisplayName("测试获取今天结束时间")
    void testGetToDayEndTime() {
        String result = DateUtil.getToDayEndTime();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 23:59:59"), "应以 23:59:59 结尾");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} 23:59:59"), "格式应为 yyyy-MM-dd 23:59:59");
    }

    // ==================== getYestodayStartTime 方法测试 ====================

    @Test
    @DisplayName("测试获取昨天开始时间")
    void testGetYestodayStartTime() {
        String result = DateUtil.getYestodayStartTime();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 00:00:00"), "应以 00:00:00 结尾");
    }

    // ==================== getYestodayEndTime 方法测试 ====================

    @Test
    @DisplayName("测试获取昨天结束时间")
    void testGetYestodayEndTime() {
        String result = DateUtil.getYestodayEndTime();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 23:59:59"), "应以 23:59:59 结尾");
    }

    // ==================== getOneDayStartTime 方法测试 ====================

    @Test
    @DisplayName("测试获取某天开始时间-String参数")
    void testGetOneDayStartTime_String() {
        // 注意：getOneDayStartTime(String) 使用 new Date(String)，
        // Date(String) 构造函数期望的格式是类似 "Mon Jan 01 00:00:00 GMT 2024" 的格式
        // 这里使用一个有效的日期字符串格式
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date currentDate = new Date();
        String oneDay = sdf.format(currentDate);
        String result = DateUtil.getOneDayStartTime(oneDay);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 00:00:00"), "应以 00:00:00 结尾");
    }

    @Test
    @DisplayName("测试获取某天开始时间-Date参数")
    void testGetOneDayStartTime_Date() {
        Date oneDay = new Date();
        String result = DateUtil.getOneDayStartTime(oneDay);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 00:00:00"), "应以 00:00:00 结尾");
    }

    // ==================== getOneDayEndTime 方法测试 ====================

    @Test
    @DisplayName("测试获取某天结束时间-String参数")
    void testGetOneDayEndTime_String() {
        // 注意：getOneDayEndTime(String) 使用 new Date(String)，
        // Date(String) 构造函数期望的格式是类似 "Mon Jan 01 00:00:00 GMT 2024" 的格式
        // 这里使用一个有效的日期字符串格式
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date currentDate = new Date();
        String oneDay = sdf.format(currentDate);
        String result = DateUtil.getOneDayEndTime(oneDay);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 00:00:00"), "应返回日期格式");
    }

    @Test
    @DisplayName("测试获取某天结束时间-Date参数")
    void testGetOneDayEndTime_Date() {
        Date oneDay = new Date();
        String result = DateUtil.getOneDayEndTime(oneDay);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 00:00:00"), "应返回日期格式");
    }

    // ==================== getWeekStartTime 方法测试 ====================

    @Test
    @DisplayName("测试获取本周开始时间")
    void testGetWeekStartTime() {
        Date result = DateUtil.getWeekStartTime();
        assertNotNull(result, "返回结果不应为null");
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK), "应为周一");
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY), "小时应为0");
        assertEquals(0, cal.get(Calendar.MINUTE), "分钟应为0");
        assertEquals(0, cal.get(Calendar.SECOND), "秒应为0");
    }

    // ==================== strToDateTime 方法测试 ====================

    @Test
    @DisplayName("测试字符串转Date-成功")
    void testStrToDateTime_Success() {
        String dateTime = "2024-01-01 12:00:00";
        Date result = DateUtil.strToDateTime(dateTime);
        assertNotNull(result, "返回结果不应为null");
    }

    @Test
    @DisplayName("测试字符串转Date-无效格式")
    void testStrToDateTime_InvalidFormat() {
        String invalidDateTime = "invalid date";
        Date result = DateUtil.strToDateTime(invalidDateTime);
        assertNull(result, "无效格式应返回null");
    }

    // ==================== dateToStamp 方法测试 ====================

    @Test
    @DisplayName("测试Date转时间戳-成功")
    void testDateToStamp_Success() throws ParseException {
        String dateString = "2024-01-01 12:00:00";
        Long result = DateUtil.dateToStamp(dateString);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result > 0, "时间戳应大于0");
    }

    @Test
    @DisplayName("测试Date转时间戳-无效格式")
    void testDateToStamp_InvalidFormat() {
        String invalidDate = "invalid date";
        assertThrows(ParseException.class, () -> DateUtil.dateToStamp(invalidDate), "应抛出ParseException");
    }

    // ==================== dateTimeToStr 方法测试 ====================

    @Test
    @DisplayName("测试Date转String")
    void testDateTimeToStr() {
        Date date = new Date();
        String result = DateUtil.dateTimeToStr(date);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"), "格式应为 yyyy-MM-dd HH:mm:ss");
    }

    // ==================== getWeekStartTimeStr 方法测试 ====================

    @Test
    @DisplayName("测试获取本周开始时间字符串")
    void testGetWeekStartTimeStr() {
        String result = DateUtil.getWeekStartTimeStr();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 00:00:00"), "应以 00:00:00 结尾");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} 00:00:00"), "格式应为 yyyy-MM-dd 00:00:00");
    }

    // ==================== getWeekEndTime 方法测试 ====================

    @Test
    @DisplayName("测试获取本周结束时间")
    void testGetWeekEndTime() {
        Date result = DateUtil.getWeekEndTime();
        assertNotNull(result, "返回结果不应为null");
        Date weekStart = DateUtil.getWeekStartTime();
        assertTrue(result.after(weekStart), "结束时间应在开始时间之后");
    }

    // ==================== getWeekEndTimeStr 方法测试 ====================

    @Test
    @DisplayName("测试获取本周结束时间字符串")
    void testGetWeekEndTimeStr() {
        String result = DateUtil.getWeekEndTimeStr();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 23:59:59"), "应以 23:59:59 结尾");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} 23:59:59"), "格式应为 yyyy-MM-dd 23:59:59");
    }

    // ==================== getLastWeekStartTimeStr 方法测试 ====================

    @Test
    @DisplayName("测试获取上周开始时间字符串")
    void testGetLastWeekStartTimeStr() {
        String result = DateUtil.getLastWeekStartTimeStr();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 00:00:00"), "应以 00:00:00 结尾");
    }

    // ==================== getMonthStartTime 方法测试 ====================

    @Test
    @DisplayName("测试获取本月开始时间")
    void testGetMonthStartTime() {
        Date result = DateUtil.getMonthStartTime();
        assertNotNull(result, "返回结果不应为null");
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH), "应为当月第一天");
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY), "小时应为0");
    }

    // ==================== getMonthStartTimeStr 方法测试 ====================

    @Test
    @DisplayName("测试获取本月开始时间字符串")
    void testGetMonthStartTimeStr() {
        String result = DateUtil.getMonthStartTimeStr();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 23:59:59"), "应以 23:59:59 结尾");
    }

    // ==================== getMonthEndTime 方法测试 ====================

    @Test
    @DisplayName("测试获取本月结束时间")
    void testGetMonthEndTime() {
        Date result = DateUtil.getMonthEndTime();
        assertNotNull(result, "返回结果不应为null");
        Date monthStart = DateUtil.getMonthStartTime();
        assertTrue(result.after(monthStart) || result.equals(monthStart), "结束时间应在开始时间之后或相等");
    }

    // ==================== getMonthEndTimeStr 方法测试 ====================

    @Test
    @DisplayName("测试获取本月结束时间字符串")
    void testGetMonthEndTimeStr() {
        String result = DateUtil.getMonthEndTimeStr();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.endsWith(" 23:59:59"), "应以 23:59:59 结尾");
    }

    // ==================== getCurrentMonthDay 方法测试 ====================

    @Test
    @DisplayName("测试获取当月天数")
    void testGetCurrentMonthDay() {
        int result = DateUtil.getCurrentMonthDay();
        assertTrue(result >= 28 && result <= 31, "天数应在28-31之间");
    }

    // ==================== getDayByTwoDay 方法测试 ====================

    @Test
    @DisplayName("测试获取两个日期之间的天数-成功")
    void testGetDayByTwoDay_Success() {
        String date1 = "2024-01-10";
        String date2 = "2024-01-01";
        int result = DateUtil.getDayByTwoDay(date1, date2);
        assertEquals(9, result, "日期差应为9天");
    }

    @Test
    @DisplayName("测试获取两个日期之间的天数-无效格式")
    void testGetDayByTwoDay_InvalidFormat() {
        String date1 = "invalid";
        String date2 = "2024-01-01";
        int result = DateUtil.getDayByTwoDay(date1, date2);
        assertEquals(0, result, "无效格式应返回0");
    }

    // ==================== getSecondByTwoDay 方法测试 ====================

    @Test
    @DisplayName("测试获取两个日期之间的秒数")
    void testGetSecondByTwoDay() {
        Date date1 = new Date(System.currentTimeMillis() + 10000); // 10秒后
        Date date2 = new Date();
        int result = DateUtil.getSecondByTwoDay(date1, date2);
        assertTrue(result >= 9 && result <= 11, "秒数差应在9-11秒之间（考虑执行时间）");
    }

    // ==================== getDaysByWeek 方法测试 ====================

    @Test
    @DisplayName("测试判断某个日期属于本周的第几天")
    void testGetDaysByWeek() throws ParseException {
        // 获取当前日期
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = sdf.format(cal.getTime());
        
        int result = DateUtil.getDaysByWeek(dateTime);
        assertTrue(result >= 1 && result <= 7, "结果应在1-7之间");
    }

    @Test
    @DisplayName("测试判断某个日期属于本周的第几天-无效格式")
    void testGetDaysByWeek_InvalidFormat() {
        String invalidDate = "invalid date";
        assertThrows(ParseException.class, () -> DateUtil.getDaysByWeek(invalidDate), "应抛出ParseException");
    }

    // ==================== getDaysByMonth 方法测试 ====================

    @Test
    @DisplayName("测试判断某个日期属于本月的第几天")
    void testGetDaysByMonth() throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = sdf.format(cal.getTime());
        
        int result = DateUtil.getDaysByMonth(dateTime);
        assertTrue(result >= 1 && result <= 31, "结果应在1-31之间");
    }

    @Test
    @DisplayName("测试判断某个日期属于本月的第几天-无效格式")
    void testGetDaysByMonth_InvalidFormat() {
        String invalidDate = "invalid date";
        assertThrows(ParseException.class, () -> DateUtil.getDaysByMonth(invalidDate), "应抛出ParseException");
    }

    // ==================== getDaysByYearMonth 方法测试 ====================

    @Test
    @DisplayName("测试根据年月获取对应的月份天数")
    void testGetDaysByYearMonth() {
        int result = DateUtil.getDaysByYearMonth(2024, 1);
        assertEquals(31, result, "2024年1月应有31天");
        
        result = DateUtil.getDaysByYearMonth(2024, 2);
        assertEquals(29, result, "2024年2月应有29天（闰年）");
        
        result = DateUtil.getDaysByYearMonth(2023, 2);
        assertEquals(28, result, "2023年2月应有28天（非闰年）");
    }

    // ==================== getYears 方法测试 ====================

    @Test
    @DisplayName("测试获取当前年份")
    void testGetYears() {
        Integer result = DateUtil.getYears();
        assertNotNull(result, "返回结果不应为null");
        Calendar cal = Calendar.getInstance();
        assertEquals(cal.get(Calendar.YEAR), result, "年份应匹配当前年份");
    }

    // ==================== getMonth 方法测试 ====================

    @Test
    @DisplayName("测试获取当前月份")
    void testGetMonth() {
        Integer result = DateUtil.getMonth();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result >= 1 && result <= 12, "月份应在1-12之间");
    }

    // ==================== getDay 方法测试 ====================

    @Test
    @DisplayName("测试获取当前天数")
    void testGetDay() {
        Integer result = DateUtil.getDay();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result >= 1 && result <= 31, "天数应在1-31之间");
    }

    // ==================== getTime 方法测试 ====================

    @Test
    @DisplayName("测试获取过期时间")
    void testGetTime() {
        double hour = 1.0;
        String result = DateUtil.getTime(hour);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.matches("\\d{14}"), "格式应为14位数字 yyyyMMddHHmmss");
    }

    // ==================== getDate 方法测试 ====================

    @Test
    @DisplayName("测试获取几天之后的日期-成功")
    void testGetDate_Success() {
        String date = "2024-01-01 12:00:00";
        int day = 5;
        Date result = DateUtil.getDate(date, day);
        assertNotNull(result, "返回结果不应为null");
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(6, cal.get(Calendar.DAY_OF_MONTH), "应为1月6日");
    }

    @Test
    @DisplayName("测试获取几天之后的日期-无效格式")
    void testGetDate_InvalidFormat() {
        String invalidDate = "invalid date";
        int day = 5;
        Date result = DateUtil.getDate(invalidDate, day);
        assertNull(result, "无效格式应返回null");
    }

    // ==================== getDateStr 方法测试 ====================

    @Test
    @DisplayName("测试获取某个日期加上秒数的时间")
    void testGetDateStr() {
        Date beforeDate = new Date();
        Long timeSecond = 3600L; // 1小时
        String result = DateUtil.getDateStr(beforeDate, timeSecond);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"), "格式应为 yyyy-MM-dd HH:mm:ss");
    }

    // ==================== formateDate 方法测试 ====================

    @Test
    @DisplayName("测试格式化日期")
    void testFormateDate() {
        Date date = new Date();
        String code = "yyyy-MM-dd";
        String result = DateUtil.formateDate(date, code);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"), "格式应为 yyyy-MM-dd");
    }

    // ==================== getDaysByN 方法测试 ====================

    @Test
    @DisplayName("测试获取过去N天内的日期数组")
    void testGetDaysByN() {
        int intervals = 7;
        String formatStr = "yyyy-MM-dd";
        ArrayList<String> result = DateUtil.getDaysByN(intervals, formatStr);
        assertNotNull(result, "返回结果不应为null");
        assertEquals(intervals, result.size(), "数组大小应为intervals");
        for (String day : result) {
            assertTrue(day.matches("\\d{4}-\\d{2}-\\d{2}"), "格式应为 yyyy-MM-dd");
        }
    }

    // ==================== getPastDate 方法测试 ====================

    @Test
    @DisplayName("测试获取过去第几天的日期")
    void testGetPastDate() {
        int past = 0;
        String formatStr = "yyyy-MM-dd";
        String result = DateUtil.getPastDate(past, formatStr);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"), "格式应为 yyyy-MM-dd");
    }

    // ==================== getDayBetweenDates 方法测试 ====================

    @Test
    @DisplayName("测试获取某个时间段内所有日期")
    void testGetDayBetweenDates() {
        String begin = "2024-01-01 00:00:00";
        String end = "2024-01-05 00:00:00";
        List<String> result = DateUtil.getDayBetweenDates(begin, end);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.size() >= 5, "应包含至少5个日期");
    }

    // ==================== getServerStartDate 方法测试 ====================

    @Test
    @DisplayName("测试获取服务器启动时间")
    void testGetServerStartDate() {
        Date result = DateUtil.getServerStartDate();
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.getTime() <= System.currentTimeMillis(), "启动时间不应超过当前时间");
    }

    // ==================== getDatePoor 方法测试 ====================

    @Test
    @DisplayName("测试计算两个时间差")
    void testGetDatePoor() {
        Date endDate = new Date(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000L + 3 * 60 * 60 * 1000L + 30 * 60 * 1000L); // 2天3小时30分钟后
        Date nowDate = new Date();
        String result = DateUtil.getDatePoor(endDate, nowDate);
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.contains("天"), "应包含'天'");
        assertTrue(result.contains("小时"), "应包含'小时'");
        assertTrue(result.contains("分钟"), "应包含'分钟'");
    }
}

