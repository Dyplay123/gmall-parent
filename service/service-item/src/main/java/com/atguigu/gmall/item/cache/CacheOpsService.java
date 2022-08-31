package com.atguigu.gmall.item.cache;

import com.atguigu.gmall.model.to.SkuDetailTo;

public interface CacheOpsService {
    <T>T getCacheData(String cacheKey, Class<T> clz);

    boolean blomContains(Long skuId);

    boolean tryLock(Long skuId);

    void saveData(String cacheKey, SkuDetailTo fromRpc);

    void unlock(Long skuId);
}
