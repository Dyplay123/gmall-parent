package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;
@Autowired
    ThreadPoolExecutor executor;
@Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        //查看缓存中有没有
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        //如果与x相等，说明以前查过,为了防止资源浪费回源，缓存了一个占位符
        if("x".equals(jsonStr)){
            return null;
        }
        //如果是空的，说明缓存中没有这个数据
        if(StringUtils.isEmpty(jsonStr)){
            //从数据库中查询出数据
            SkuDetailTo fromRpc = this.getSkuDetail(skuId);
            //设定占位符x
            String cacheJson = "x";
            if (fromRpc!=null){
                //将查询到的数据转成Json格式，并且设置给cacheJson
                cacheJson = Jsons.toStr(fromRpc);
                //将数据库查询到的数据存储到redis中
                redisTemplate.opsForValue().set("sku:info:" + skuId,cacheJson,7, TimeUnit.DAYS);
            }else {
                //将x占位符缓存到redis中
                redisTemplate.opsForValue().set("sku:info:" + skuId,cacheJson,30,TimeUnit.MINUTES);
            }
            return fromRpc;



        }


        //3、缓存中有. 把json转成指定的对象
        SkuDetailTo skuDetailTo = Jsons.toObj(jsonStr,SkuDetailTo.class);
        return skuDetailTo;
    }


    public SkuDetailTo getSkuDetailFromRpc(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();

        CompletableFuture <SkuInfo>  skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> skuInfo = skuDetailFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfodata = skuInfo.getData();
            skuDetailTo.setSkuInfo(skuInfo.getData());
            return skuInfodata;
        },executor);


        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync((skuInfodata) -> {
            Result<List<SkuImage>> skuImages = skuDetailFeignClient.getSkuImages(skuId);
            skuInfodata.setSkuImageList(skuImages.getData());

        }, executor);

        CompletableFuture<Void> priceFutrue = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> sku1010Price = skuDetailFeignClient.getSku1010Price(skuId);
            skuDetailTo.setPrice(sku1010Price.getData());
        }, executor);


        CompletableFuture<Void> spuSalaAttrFutre = skuInfoFuture.thenAcceptAsync(skuInfodata -> {
            Long spuId = skuInfodata.getSpuId();
            Result<List<SpuSaleAttr>> skuSaleattrvalues = skuDetailFeignClient.getSkuSaleattrvalues(skuId, spuId);
            skuDetailTo.setSpuSaleAttrList(skuSaleattrvalues.getData());
        }, executor);






        CompletableFuture<Void> skuVlaueFuture = skuInfoFuture.thenAcceptAsync(skuInfodata -> {
            Result<CategoryViewTo> categoryView = skuDetailFeignClient.getCategoryView(skuInfodata.getCategory3Id());
            skuDetailTo.setCategoryView(categoryView.getData());
        }, executor);



        //6、查分类
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfodata -> {

            Result<String> sKuValueJson = skuDetailFeignClient.getSKuValueJson(skuInfodata.getSpuId());
            skuDetailTo.setValuesSkuJson(sKuValueJson.getData());
        },executor);

        CompletableFuture
                .allOf(imageFuture,priceFutrue,spuSalaAttrFutre,skuVlaueFuture,categoryFuture)
                .join();

        return skuDetailTo;
    }
}
