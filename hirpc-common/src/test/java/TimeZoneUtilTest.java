import dev.hirpc.common.utils.TimeZoneUtil;
import org.junit.Test;

/**
 * @author JT
 * @date 2022/8/21
 * @desc
 */
public class TimeZoneUtilTest {

    @Test
    public void getSystemTimeZoneTest() {
        System.out.printf("获取当前系统时区位置: %s \n", TimeZoneUtil.getSystemTimeZone());
    }

    @Test
    public void getSystemTimeZoneNumberTest() {
        System.out.printf("获取当前系统时区: %s \n", TimeZoneUtil.getSystemTimeZoneOffset());
    }

    @Test
    public void getSystemTimeTest() {
        System.out.printf("获取当前时区时间： %s \n", TimeZoneUtil.getSystemTime());
    }

    @Test
    public void getSystemTimeOfStandardTest() {
        System.out.printf("获取当前时区标准格式时间： %s \n", TimeZoneUtil.getSystemTimeOfStandardFormat());
    }

    @Test
    public void getSystemTimeOfFormatTest() {
        System.out.printf("获取指定返回格式的当前时区当前时间: %s \n", TimeZoneUtil.getSystemTimeOfFormat("yyyy-MM-dd"));
    }

    @Test
    public void getStandardFormatTimeOfZoneTest() {
        System.out.printf("使用标准时间格式输出指定时区的当前时间： %s \n", TimeZoneUtil.getStandardFormatTimeOfZone("America/Puerto_Rico"));
    }

    @Test
    public void getFormatTimeOfZoneTest() {
        System.out.printf("使用指定时间格式输出指定时区的当前时间： %s \n", TimeZoneUtil.getFormatTimeOfZone("America/Puerto_Rico", "yyyy-MM-dd HH:mm:ss.SSS"));
    }

    @Test
    public void parseStandardLocalDateTimeTest() {
        System.out.printf("标准格式化时间转换为LocalDateTime: %s \n", TimeZoneUtil.parseStandardLocalDateTime("2022-08-21 21:06:04"));
    }

}
