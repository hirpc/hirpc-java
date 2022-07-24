package dev.hirpc.kafka.utils;

import dev.hirpc.common.exceptions.ExceptionCode;
import dev.hirpc.common.exceptions.env.ProfilesActiveException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;
import java.util.Objects;

/**
 * @author: JT
 * @date: 2019/11/16
 * @Title:
 *
 * Bean实体注册工具类，使用注册中心注册
 */
public class BeanRegistryUtil {

    public static String readEnvFromBeanRegistry(BeanDefinitionRegistry registry) {
        StandardEnvironment envObj = (StandardEnvironment)((DefaultListableBeanFactory) registry).getSingleton("environment");
        String[] activeProfiles = envObj.getActiveProfiles();
        if (activeProfiles.length == 0) {
            throw new ProfilesActiveException(ExceptionCode.ERROR, "未找到配置[spring.profiles.active]的值!");
        }
        return activeProfiles[0];
    }

    /**
     * 若Bean不存在 注册Bean
     * @param registry
     * @param beanClass
     * @return
     *      false: 注册中心中存在Bean
     *      true: 注册中心不存在Bean, 并将Bean在注册中心注册
     */
    public static boolean registerBeanDefinitionIfNotExist(BeanDefinitionRegistry registry, Class<?> beanClass) {
        return registerBeanDefinitionIfNotExist(registry, beanClass, null);
    }

    public static boolean registerBeanDefinitionIfNotExist(BeanDefinitionRegistry registry, Class<?> beanClass, Map<String, Object> mv) {
        return registerBeanDefinitionIfNotExist(registry, beanClass.getName(), beanClass, null, mv);
    }

    public static boolean registerBeanDefinitionIfNotExist(BeanDefinitionRegistry registry, Class<?> beanClass, Object constructorArg, Map<String, Object> mv) {
        return registerBeanDefinitionIfNotExist(registry, beanClass.getName(), beanClass, constructorArg, mv);
    }

    public static boolean registerBeanDefinitionIfNotExist(
            BeanDefinitionRegistry registry, String beanName, Class<?> beanClass, Object constructorArg, Map<String, Object> mv
    ) {
        /**
         * 检测Bean是否存在
         */
        if (registry.containsBeanDefinition(beanName)) {
            return false;
        }
        String[] exitsBeanDefinitionNames = registry.getBeanDefinitionNames();
        for (String exitsBeanDefinitionName : exitsBeanDefinitionNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(exitsBeanDefinitionName);
            if (Objects.equals(beanDefinition.getBeanClassName(), beanClass.getName())) {
                return false;
            }
        }
        return registerBeanDefinition(registry, beanName, beanClass, constructorArg, mv);
    }

    /**
     * 注册Bean
     * @param registry
     * @param beanName
     * @param beanClass
     * @param constructorArg
     * @param mv
     * @return
     */
    public static boolean registerBeanDefinition(
            BeanDefinitionRegistry registry, String beanName, Class<?> beanClass, Object constructorArg, Map<String, Object> mv
    ) {
        /**
         * 注册Bean
         */
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        if (!Objects.isNull(constructorArg)) {
            builder.addConstructorArgValue(constructorArg);
        }
        if (!Objects.isNull(mv)) {
            for (Map.Entry<String, Object> entry : mv.entrySet()) {
                builder.addPropertyValue(entry.getKey(), entry.getValue());
            }
        }
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);
        return true;
    }

}
