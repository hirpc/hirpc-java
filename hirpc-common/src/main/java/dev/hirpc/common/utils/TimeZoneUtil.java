package dev.hirpc.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeZoneUtil {

    private static Date date;
    private static SimpleDateFormat originalSdf;
    private static SimpleDateFormat destinationSdf;

    private TimeZoneUtil() {}

    static {
        originalSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        destinationSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    //入参：当前时区 当前时间 目标时区  返回：目标时区的时间
    public static String getSwitchedTime(String originalTimeZone, String originalTimeStr, String destinationTimeZone) throws ParseException {
        originalSdf.setTimeZone(java.util.TimeZone.getTimeZone(originalTimeZone));
        destinationSdf.setTimeZone(java.util.TimeZone.getTimeZone(destinationTimeZone));
        date = originalSdf.parse(originalTimeStr);
        return destinationSdf.format(date);
    }

    //入参： 当前时区的时间  目标时区  返回 目标时区的时间
    public static String getSwitchedTime(String originalTimeStr, String destinationTimeZone) throws ParseException {
        String originalTimeZone = getSystemTimeZone();
        originalSdf.setTimeZone(java.util.TimeZone.getTimeZone(originalTimeZone));
        destinationSdf.setTimeZone(java.util.TimeZone.getTimeZone(destinationTimeZone));
        date = originalSdf.parse(originalTimeStr);
        return destinationSdf.format(date);
    }

    //自动获取当前时区时间
    public static String getSystemTime() {
        String originalTimeZone = getSystemTimeZone();
        originalSdf.setTimeZone(java.util.TimeZone.getTimeZone(originalTimeZone));
        Date day = new Date();
        String originalTimeStr = originalSdf.format(day);
        return originalTimeStr;
    }

    //自动获取当前时区
    public static String getSystemTimeZone() {
//        Calendar cal = Calendar.getInstance();
//        int offset = cal.get(Calendar.ZONE_OFFSET);
//        cal.add(Calendar.MILLISECOND, -offset);
//        Long timeStampUTC = cal.getTimeInMillis();
//        Long timeStamp = System.currentTimeMillis();
//        Long timeZone = (timeStamp - timeStampUTC) / (1000 * 3600);
//        String originalTimeZone = "GMT" + String.valueOf(timeZone);
        return TimeZone.getDefault().getID();
    }

}
