package dev.hirpc.common.plugin;

import java.util.Map;

/**
 * @author  JT
 * @date  2019/11/14
 * {@code title} 插件配置加载接口
 */
public interface ConfigLoad<T> {

    /**
     * 数据源配置加载
     *
     * Key： 数据源名称
     * Value： 数据源
     * @return 加载结果返回
     */
    Map<String, T> load();

}
