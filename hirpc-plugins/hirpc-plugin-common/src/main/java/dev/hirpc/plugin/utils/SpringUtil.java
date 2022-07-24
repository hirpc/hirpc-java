package dev.hirpc.plugin.utils;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author JT
 * @date 2022/7/10
 * @desc
 */
public class SpringUtil {

    private SpringUtil(){}
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * 获取ApplicationContext
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取Bean
     * @param name bean名称
     * @return
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 获取Bean
     * @param clazz 类别
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 获取Bean
     * @param name bean名称
     * @param clazz bean class
     * @return
     * @param <T>
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 注册Bean实例
     * @param beanName
     * @param singletonObject
     * @return
     */
    public static Object registerBean(String beanName, Object singletonObject) {

        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

        //获取BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();

        //动态注册bean.
        defaultListableBeanFactory.registerSingleton(beanName,singletonObject);

        //获取动态注册的bean.
        return configurableApplicationContext.getBean(beanName);
    }
}
