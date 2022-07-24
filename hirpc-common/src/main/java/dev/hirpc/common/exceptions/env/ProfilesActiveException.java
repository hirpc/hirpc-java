package dev.hirpc.common.exceptions.env;

import dev.hirpc.common.exceptions.BasicException;

/**
 * @author JT
 * @date 2019/11/25
 * {@code Title}: 配置文件激活异常
 */
public class ProfilesActiveException extends BasicException {

    public ProfilesActiveException(Integer code, String message) {
        super(code, message);
    }

    public ProfilesActiveException(Integer code, String messageFormat, String... args) {
        super(code, messageFormat, args);
    }

}
