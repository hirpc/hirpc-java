package dev.hirpc.common.utils;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;

/**
 * @author JT
 * @date 2022/6/16
 * {@code description}
 */
public class CommonIdUtil {

    private CommonIdUtil() {}

    private static final Integer WORK_ID;

    static {
        WORK_ID = Math.floorMod(NetUtil.ipv4ToLong(NetUtil.getLocalhostStr()), 30);
    }

    public static String getId(final int centerId) {
        return IdUtil.getSnowflake(WORK_ID, centerId).nextIdStr();
    }

    public static String getId() {
        return IdUtil.getSnowflake(WORK_ID).nextIdStr();
    }

}
