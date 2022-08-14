package dev.hirpc.common.cache.operate;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * @author JT
 * @date 2022/8/14
 * @desc
 */
public class GrpcHeaderCacheOperate extends AbstractCacheOperate {

    public GrpcHeaderCacheOperate() {
        super();
    }
    @Override
    public void initCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterAccess(2, TimeUnit.MINUTES)
                .maximumSize(100000)
                .build();
    }

}
