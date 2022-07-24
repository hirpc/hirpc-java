package dev.hirpc.kafka.exception;

import dev.hirpc.common.exceptions.BasicException;

/**
 * @author: Oliver
 * @date: 2022/6/14
 * @title:
 */
public class KafkaConfigException extends BasicException {

    public KafkaConfigException(Integer code, String message) {
        super(code, message);
    }

    public KafkaConfigException(Integer code, String messageFormat, Object... args) {
        super(code, messageFormat, args);
    }

}
