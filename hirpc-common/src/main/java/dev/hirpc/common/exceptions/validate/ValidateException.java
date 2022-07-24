package dev.hirpc.common.exceptions.validate;

import dev.hirpc.common.exceptions.BasicException;

/**
 * @author JT
 * @date 2022/7/16
 * @desc
 */
public class ValidateException extends BasicException {
    public ValidateException(Integer code, String message) {
        super(code, message);
    }

    public ValidateException(Integer code, String messageFormat, Object... args) {
        super(code, messageFormat, args);
    }
}
