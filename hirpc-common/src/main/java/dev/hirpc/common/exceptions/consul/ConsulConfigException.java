package dev.hirpc.common.exceptions.consul;

import dev.hirpc.common.exceptions.BasicException;

/**
 * @author JT
 * @date 2022/7/16
 * @desc
 */
public class ConsulConfigException extends BasicException  {
    public ConsulConfigException(Integer code, String message) {
        super(code, message);
    }

    public ConsulConfigException(Integer code, String messageFormat, Object... args) {
        super(code, messageFormat, args);
    }
}
