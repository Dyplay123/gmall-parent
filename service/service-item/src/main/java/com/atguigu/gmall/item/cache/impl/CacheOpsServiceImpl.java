package com.atguigu.gmall.item.cache.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@Service
public class CacheOpsServiceImpl implements CacheOpsService {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Override
    public <T> T getCacheData(String cacheKey, Class<T> clz) {
        String jsonStr = redisTemplate.opsForValue().get(cacheKey);
        if (SysRedisConst.NULL_VAL.equals(jsonStr)){
            return  null;
        }
        T t = Jsons.toObj(jsonStr, clz);
        return t;
    }

    @Override
    public boolean blomContains(Long skuId) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        return bloomFilter.contains(skuId);
    }

    @Override
    public boolean tryLock(Long skuId) {
        //准备锁用的唯一key
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL + skuId;
        //拿到锁
        RLock lock = redissonClient.getLock(lockKey);
       //尝试加锁
        boolean b = lock.tryLock();
        return b;
    }

    @Override
    public void saveData(String cacheKey, SkuDetailTo fromRpc) {
         if (fromRpc == null){
             redisTemplate.opsForValue().set(cacheKey,
                     SysRedisConst.NULL_VAL,
                     SysRedisConst.NULL_VAL_TTL,
                     TimeUnit.SECONDS);
         }else {
             String str = Jsons.toStr(fromRpc);
             redisTemplate.opsForValue().set(cacheKey,
                     str,
                     SysRedisConst.SKUDETAIL_TTL,
                     TimeUnit.SECONDS);

         }
    }

    @Override
    public void unlock(Long skuId) {
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL+skuId;
        RLock lock = redissonClient.getLock(lockKey);

        //解掉这把锁
        lock.unlock();
    }
}
