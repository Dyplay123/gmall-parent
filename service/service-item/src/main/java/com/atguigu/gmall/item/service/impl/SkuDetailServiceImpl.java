package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;
@Autowired
    ThreadPoolExecutor executor;

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
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
