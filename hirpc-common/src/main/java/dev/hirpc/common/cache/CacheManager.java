package dev.hirpc.common.cache;

import dev.hirpc.common.cache.operate.CacheOperate;

/**
 * @author JT
 * @date 2022/8/14
 * @desc
 */
public class CacheManager {

    private CacheManager() {}

    public static CacheOperate getGrpcHeaderCache() {
        return CacheEnum.GRPC_HEADER.getCacheOperate();
    }
}
