package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;

import com.atguigu.gmall.feign.product.SkuProdectFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.starter.cache.annotation.GmallCache;
import com.atguigu.starter.cache.service.CacheOpsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
@Slf4j
@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    SkuProdectFeignClient skuDetailFeignClient;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    StringRedisTemplate redisTemplate;
    //每个skuId，关联自己的一把锁
    Map<Long, ReentrantLock> lockPool = new ConcurrentHashMap<>();
    @Autowired
    CacheOpsService cacheOpsService;

    @GmallCache(
            cacheKey =SysRedisConst.SKU_INFO_PREFIX+"#{#params[0]}",
            bloomName = SysRedisConst.BLOOM_SKUID,
            bloomValue = "#{#params[0]}",
            lockName = SysRedisConst.LOCK_SKU_DETAIL+"#{#params[0]}",
            ttl = 60*60*24*7L
    )
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {

        SkuDetailTo fromRpc = this.getSkuDetailFromRpc(skuId);
        return fromRpc;
    }

    public SkuDetailTo getSkuDetailWithCache(Long skuId) {
        String cacheKey = SysRedisConst.SKU_INFO_PREFIX + skuId;
        SkuDetailTo cacheData = cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
        //1 先看缓存中有没有
        if (cacheData == null) {
            boolean contain = cacheOpsService.bloomContains(skuId);
            if (!contain){
                //2 布隆过滤器中说没有，那就一定没有
                log.info("布隆过滤器中说没有,存在攻击风险");
                return  null;
            }
            //布隆说有那就可能有，所以就要数据回源
          boolean lock=  cacheOpsService.tryLock(skuId);
            if (lock){
                log.info("缓存中没查到，布隆过滤器中说有，准备回源查询数据库");
                SkuDetailTo fromRpc = this.getSkuDetailFromRpc(skuId);
                //将查询到的数据放到缓存中
                cacheOpsService.saveData(cacheKey,fromRpc);
                //解锁
                cacheOpsService.unlock(skuId);
                return fromRpc;
            }
            try {Thread.sleep(1000);
                return cacheOpsService.getCacheData(cacheKey,SkuDetailTo.class);
            } catch (InterruptedException e) {

            }
        }
        //4、缓存中有
        return cacheData;


    }

    public SkuDetailTo getSkuDetailXXX(Long skuId) {
        //查看缓存中有没有
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        //如果与x相等，说明以前查过,为了防止资源浪费回源，缓存了一个占位符
        if ("x".equals(jsonStr)) {
            return null;
        }
        //如果是空的，说明缓存中没有这个数据
        if (StringUtils.isEmpty(jsonStr)) {
            //从数据库中查询出数据
            SkuDetailTo fromRpc = this.getSkuDetail(skuId);
            //设定占位符x
            String cacheJson = "x";
            if (fromRpc != null) {
                //将查询到的数据转成Json格式，并且设置给cacheJson
                cacheJson = Jsons.toStr(fromRpc);
                //将数据库查询到的数据存储到redis中
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 7, TimeUnit.DAYS);
            } else {
                //将x占位符缓存到redis中
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 30, TimeUnit.MINUTES);
            }
            return fromRpc;


        }


        //3、缓存中有. 把json转成指定的对象
        SkuDetailTo skuDetailTo = Jsons.toObj(jsonStr, SkuDetailTo.class);
        return skuDetailTo;
    }

    public SkuDetailTo getSkuDetailFeature(Long skuId) {
        lockPool.put(skuId, new ReentrantLock());
        //查看缓存中有没有
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        //如果与x相等，说明以前查过,为了防止资源浪费回源，缓存了一个占位符
        if ("x".equals(jsonStr)) {
            return null;
        }
        //如果是空的，说明缓存中没有这个数据
        if (StringUtils.isEmpty(jsonStr)) {

            //加锁解决击穿
            SkuDetailTo fromRpc = null;

            ReentrantLock lock = lockPool.putIfAbsent(skuId, new ReentrantLock());
            //设定占位符x

            boolean b = lock.tryLock(); //立即尝试加锁，不用等，瞬发。等待逻辑在业务上 .抢一下，不成就不用再抢了
            if (b) {
                //抢到锁
                fromRpc = getSkuDetailFromRpc(skuId);
            } else {
                //没抢到
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
                //逆转为 SkuDetailTo
                //3、缓存中有. 把json转成指定的对象
                SkuDetailTo skuDetailTo = Jsons.toObj(jsonStr, SkuDetailTo.class);
                return skuDetailTo;
            }


            //2.2、放入缓存【查到的对象转为json字符串保存到redis】
            //设定占位符x
            String cacheJson = "x";
            if (fromRpc != null) {
                cacheJson = Jsons.toStr(fromRpc);
                //加入雪崩解决方案。固定业务时间+随机过期时间
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 7, TimeUnit.DAYS);
            } else {
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 30, TimeUnit.MINUTES);
            }

            return fromRpc;


        }


        //3、缓存中有. 把json转成指定的对象
        SkuDetailTo skuDetailTo = Jsons.toObj(jsonStr, SkuDetailTo.class);
        return skuDetailTo;
    }


    public SkuDetailTo getSkuDetailFromRpc(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();

        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> skuInfo = skuDetailFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfodata = skuInfo.getData();
            skuDetailTo.setSkuInfo(skuInfo.getData());
            return skuInfodata;
        }, executor);


        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync((skuInfodata) -> {
            if(skuInfodata != null){
                Result<List<SkuImage>> skuImages = skuDetailFeignClient.getSkuImages(skuId);
                skuInfodata.setSkuImageList(skuImages.getData());
            }


        }, executor);

        CompletableFuture<Void> priceFutrue = CompletableFuture.runAsync(() -> {

            Result<BigDecimal> sku1010Price = skuDetailFeignClient.getSku1010Price(skuId);
            skuDetailTo.setPrice(sku1010Price.getData());
        }, executor);


        CompletableFuture<Void> spuSalaAttrFutre = skuInfoFuture.thenAcceptAsync(skuInfodata -> {
            if(skuInfodata != null){
                Long spuId = skuInfodata.getSpuId();
                Result<List<SpuSaleAttr>> skuSaleattrvalues = skuDetailFeignClient.getSkuSaleattrvalues(skuId, spuId);
                skuDetailTo.setSpuSaleAttrList(skuSaleattrvalues.getData());
            }

        }, executor);


        CompletableFuture<Void> skuVlaueFuture = skuInfoFuture.thenAcceptAsync(skuInfodata -> {
            if(skuInfodata != null){
                Result<CategoryViewTo> categoryView = skuDetailFeignClient.getCategoryView(skuInfodata.getCategory3Id());
                skuDetailTo.setCategoryView(categoryView.getData());
            }

        }, executor);


        //6、查分类
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfodata -> {
            if(skuInfodata != null){
                Result<String> sKuValueJson = skuDetailFeignClient.getSKuValueJson(skuInfodata.getSpuId());
                skuDetailTo.setValuesSkuJson(sKuValueJson.getData());
            }

        }, executor);

        CompletableFuture
                .allOf(imageFuture, priceFutrue, spuSalaAttrFutre, skuVlaueFuture, categoryFuture)
                .join();

        return skuDetailTo;
    }
}
