package dev.hirpc.kafka.plugin;

import dev.hirpc.common.exceptions.ExceptionCode;
import dev.hirpc.common.plugin.ConfigLoad;
import dev.hirpc.kafka.annotation.EnableKafka;
import dev.hirpc.kafka.domain.KafkaSource;
import dev.hirpc.kafka.exception.KafkaConfigException;
import dev.hirpc.kafka.plugin.config.KafkaDefaultConfigLoad;
import dev.hirpc.kafka.utils.BeanRegistryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: Oliver
 * @date: 2022/6/14
 * @title:
 */
@Slf4j
public class KafkaLoadPlugin implements ImportBeanDefinitionRegistrar {

//    @Resource
//    KafkaProperties kafkaProperties;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        /**
         * 环境配置获取
         */
        String env = BeanRegistryUtil.readEnvFromBeanRegistry(beanDefinitionRegistry);
        ConfigLoad<KafkaSource> configLoad = new KafkaDefaultConfigLoad(env);
        Map<String, KafkaSource> kafkaSourceMap = configLoad.load();

        /**
         * 获取注解配置的值
         */
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableKafka.class.getName());
        String[] sourceNames = (String[]) attributes.get("source");
        String defaultSource = (String) attributes.get("defaultSource");

        for (String sourceName : sourceNames) {
            KafkaSource kafkaSource = kafkaSourceMap.get(sourceName);
            if (Objects.isNull(kafkaSource)) {
                continue;
            }

            /**
             * 配置producer
             */
//            Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
            Map<String, Object> producerProps = new HashMap<>();
            producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSource.getBootstrapServers().get(0));
            producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            ProducerFactory producerFactory = new DefaultKafkaProducerFactory<>(producerProps);

//            /**
//             * 配置consumer
//             */
//            Map<String, Object> consumerProps = new HashMap<>();
//            consumerProps = new HashMap<>();
//            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//            consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "myGroup");
//            ConsumerFactory consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
//
//            Map<String, Object> param = new HashMap<>();
//            param.put("consumerFactory", consumerFactory);

//            boolean producerIsRegister = BeanRegistryUtil.registerBeanDefinition(
//                    beanDefinitionRegistry, sourceName, KafkaTemplate.class, producerFactory, null
//            );
//            this.checkIsRegister(producerIsRegister, sourceName);
//
//            boolean consumerIsRegister = BeanRegistryUtil.registerBeanDefinition(
//                    beanDefinitionRegistry, sourceName, ConcurrentKafkaListenerContainerFactory.class, null, param
//            );
//            this.checkIsRegister(consumerIsRegister, sourceName);

            if (sourceName.equals(defaultSource)) {
                String producerBeanName = "kafkaTemplate";
                boolean producerIsRegister = BeanRegistryUtil.registerBeanDefinition(
                        beanDefinitionRegistry, producerBeanName, KafkaTemplate.class, producerFactory, null
                );
                this.checkIsRegister(producerIsRegister, producerBeanName);

//                String consumerBeanName = "concurrentKafkaListenerContainerFactory";
//                boolean consumerIsRegister = BeanRegistryUtil.registerBeanDefinition(
//                        beanDefinitionRegistry, consumerBeanName, ConcurrentKafkaListenerContainerFactory.class, null, param
//                );
//                this.checkIsRegister(consumerIsRegister, consumerBeanName);
            }

            log.info("[插件配置成功] - 成功插件:[Kafka]");

        }
    }


    /**
     * 未在注册中心注册成功异常
     * @param source
     */
    private void checkIsRegister(boolean isRegister, String source) {
        if (!isRegister) {
            throw new KafkaConfigException(ExceptionCode.ERROR, "Bean[{}]未在注册中心注册成功！", source);
        }
    }

}
