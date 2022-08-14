package dev.hirpc.common.cache.operate;

/**
 * @author JT
 * @date 2022/8/14
 * @desc
 */
public interface CacheOperate {

    void initCache();

    void set(String key, Object value);

    Object get(String key);
}
