package dev.hirpc.common.exceptions;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author JT
 * @date 2019/11/14
 * {@code Title}: 基础异常
 */
@Slf4j
public class BasicException extends RuntimeException{

    private Integer code;
    private String messageFormat;
    private String message;
    private Object[] args;

    public BasicException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BasicException(Integer code, String messageFormat, Object... args) {
        this.code = code;
        this.messageFormat = messageFormat;
        this.args = args;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        if (!Objects.isNull(this.messageFormat)) {
            this.message = StrUtil.format(messageFormat, args);
        }
        return this.message;
    }

}
