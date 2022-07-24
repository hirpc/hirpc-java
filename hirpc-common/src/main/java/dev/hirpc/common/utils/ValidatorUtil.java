package dev.hirpc.common.utils;

import cn.hutool.core.util.StrUtil;
import dev.hirpc.common.exceptions.ExceptionCode;
import dev.hirpc.common.exceptions.validate.ValidateException;

/**
 * @author JT
 * @date 2022/7/16
 * @desc
 */
public class ValidatorUtil {

    private ValidatorUtil() {}

    public static boolean validor(Object obj, String errorMessage) {
        if (StrUtil.isBlankIfStr(obj)) {
            throw new ValidateException(ExceptionCode.ERROR, errorMessage);
        }
        return true;
    }
}
