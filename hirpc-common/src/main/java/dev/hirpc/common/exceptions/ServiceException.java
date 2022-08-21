package dev.hirpc.common.exceptions;

import cn.hutool.core.util.StrUtil;

import java.util.Objects;

/**
 * @author JT
 * @date 2022/8/6
 * @desc
 */
public class ServiceException extends RuntimeException{

    private Integer code;
    private String messageFormat;
    private String message;
    private Object[] args;

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(Integer code, String messageFormat, Object... args) {
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
