package dev.hirpc.common.utils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author JT
 * @date 2022/8/21
 * @desc
 */
public class TimeZoneUtil {

    private TimeZoneUtil() {}

    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";


    /**
     * 获取系统时区
     * @return 系统时间ID字符串
     */
    public static String getSystemTimeZone() {
        return ZoneId.systemDefault().getId();
    }

    /**
     * 获取系统当前时区
     * @return 例如  +08:00
     */
    public static String getSystemTimeZoneOffset() {
        return OffsetDateTime.now().getOffset().getId();
    }

    /**
     * 获取当前时区系统时间
     * @return 未格式化原格式时间
     */
    public static ZonedDateTime getSystemTime() {
        return ZonedDateTime.now(ZoneId.systemDefault());
    }

    /**
     * 获取当前系统时间系统时间
     * @return 标准格式化后系统时间
     */
    public static String getSystemTimeOfStandardFormat() {
        return getSystemTimeOfFormat(STANDARD_FORMAT);
    }

    /**
     * 获取当前时区系统时间，
     * @param pattern 指定返回格式
     * @return 指定的返回格式时间
     */
    public static String getSystemTimeOfFormat(String pattern) {
        return getSystemTime().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取某个时区的时间
     * @param zoneStr 时区ID 例如：Asia/Shanghai
     * @return 标准时间格式 字符串
     */
    public static String getStandardFormatTimeOfZone(String zoneStr) {
        return getFormatTimeOfZone(zoneStr, STANDARD_FORMAT);
    }

    /**
     * 获取指定时区指定格式的当前时间
     * @param zoneStr 指定时区ID 例如：Asia/Shanghai
     * @param pattern 指定时间返回格式
     * @return 指定返回格式指定时区的时间
     */
    public static String getFormatTimeOfZone(String zoneStr, String pattern) {
        return getTimeOfZone(zoneStr).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取某个时区的时间
     * @param zoneStr 时区ID 例如：Asia/Shanghai
     * @return 指定时区时间
     */
    public static ZonedDateTime getTimeOfZone(String zoneStr) {
        return ZonedDateTime.now(ZoneId.of(zoneStr));
    }

    /**
     * 标准的字符串时间转换为LocalDateTime
     * @param datetime 标准格式字符串时间
     * @return LocalDateTime类型
     */
    public static ZonedDateTime parseStandardLocalDateTime(String datetime) {
        return parseLocalDateTime(datetime, STANDARD_FORMAT);
    }

    /**
     * 字符串时间转换为LocalDateTime
     * @param datetime 字符串时间
     * @param pattern 字符串使用的模式
     * @return LocalDateTime类型时间
     */
    public static ZonedDateTime parseLocalDateTime(String datetime, String pattern) {
        return ZonedDateTime.parse(datetime, DateTimeFormatter.ofPattern(pattern));
    }


}
