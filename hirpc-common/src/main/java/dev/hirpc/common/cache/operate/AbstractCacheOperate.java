package dev.hirpc.common.cache.operate;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * @author JT
 * @date 2022/8/14
 * @desc
 */
public abstract class AbstractCacheOperate implements CacheOperate{

    AbstractCacheOperate() {
        initCache();
    }

    protected Cache<String, Object> cache;

    @Override
    public abstract void initCache();

    @Override
    public void set(String key, Object value) {
        this.cache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return this.cache.getIfPresent(key);
    }

}
