package dev.hirpc.common.domain;

import org.slf4j.helpers.MessageFormatter;

import java.util.Objects;

/**
 * author: JT
 * date: 2020/6/7
 * title:
 */
public class WebResponseBuilder {

    private static final Integer SUCCESS = 200;

    private static final Integer FAILED = 500;

    public static WebResponse ok() {
        return ok(SUCCESS);
    }

    public static WebResponse ok(Integer code) {
        return ok(code, null);
    }

    public static WebResponse ok(String messageFormat, Object... args) {
        return ok(SUCCESS, messageFormat, args);
    }

    public static WebResponse ok(Integer code, String messageFormat, Object... args) {
        String msg = null;
        if (!Objects.isNull(messageFormat)) {
            msg = MessageFormatter.arrayFormat(messageFormat, args).getMessage();
        }
        return new WebResponse().setCode(code).setMsg(msg);
    }


    public static WebResponse fail() {
        return ok(FAILED);
    }

    public static WebResponse fail(Integer code) {
        return ok(code, null);
    }

    public static WebResponse fail(String messageFormat, Object... args) {
        return ok(FAILED, messageFormat, args);
    }

    public static WebResponse fail(Integer code, String messageFormat, Object... args) {
        String msg = null;
        if (!Objects.isNull(messageFormat)) {
            msg = MessageFormatter.arrayFormat(messageFormat, args).getMessage();
        }
        return new WebResponse().setCode(code).setMsg(msg);
    }


}
