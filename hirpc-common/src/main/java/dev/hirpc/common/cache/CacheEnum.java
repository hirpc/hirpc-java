package dev.hirpc.common.cache;

import dev.hirpc.common.cache.operate.CacheOperate;
import dev.hirpc.common.cache.operate.CacheOperateManage;
import dev.hirpc.common.cache.operate.GrpcHeaderCacheOperate;

/**
 * @author JT
 * @date 2022/8/14
 * @desc
 */
public enum CacheEnum implements CacheOperateManage {

    /**
     * GRPC 头部信息缓存
     */
    GRPC_HEADER {
        @Override
        public CacheOperate getCacheOperate() {
            return new GrpcHeaderCacheOperate();
        }
    },

    ;


}
