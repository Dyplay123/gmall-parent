package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
public class SkuManageController {


    @Autowired
    SkuInfoService skuInfoService;





   //获取sku分页列表
    @GetMapping("/list/{page}/{limit}")
    public Result skuInfoPage(@PathVariable("page") Long page,
                              @PathVariable("limit") Long limit) {

        Page<SkuInfo> page1 = new Page<>(page, limit);
        return Result.ok(skuInfoService.page(page1));
    }

    //上架
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        skuInfoService.onSale(skuId);

        return Result.ok();


    }

    //下架
    @GetMapping("/cancelSale/{skuId}")

    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuInfoService.cancelSale(skuId);

        return Result.ok();


    }



    @PostMapping("/saveSkuInfo")
    public Result saveSku(@RequestBody SkuInfo info){

        //sku的大保存
        skuInfoService.saveSkuInfo(info);

        return Result.ok();
    }
}
