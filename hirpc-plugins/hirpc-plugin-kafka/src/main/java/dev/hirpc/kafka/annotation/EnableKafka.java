package dev.hirpc.kafka.annotation;

import dev.hirpc.kafka.plugin.KafkaLoadPlugin;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: Oliver
 * @date: 2022/6/14
 * @title:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(KafkaLoadPlugin.class)
public @interface EnableKafka {

    String[] source();

    String defaultSource();

}
