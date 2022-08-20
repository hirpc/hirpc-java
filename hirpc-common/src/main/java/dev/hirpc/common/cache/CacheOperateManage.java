package dev.hirpc.common.cache;

import dev.hirpc.common.cache.operate.CacheOperate;

/**
 * @author JT
 * @date 2022/8/14
 * @desc
 */
public interface CacheOperateManage {

    /**
     * 获取Cache操作
     * @return CacheOperate 接口
     */
    CacheOperate getCacheOperate();
}
