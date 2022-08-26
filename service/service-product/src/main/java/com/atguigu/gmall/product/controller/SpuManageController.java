package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
public class SpuManageController {

@Autowired
    SpuInfoService spuInfoService;

    @Autowired
    SpuImageService spuImageService;

@Autowired
    BaseTrademarkService baseTrademarkService;

        @Autowired
    SpuSaleAttrService spuSaleAttrService;
//获取spu分页列表,
    @GetMapping("/{page}/{limit}")
    public Result getSpuInfoPage(@PathVariable("page") Long page,
                                 @PathVariable("limit") Long limit,
                                 @RequestParam("category3Id") Long category3Id) {


        Page<SpuInfo> page1 = new Page<>(page,limit);
       Page<SpuInfo> spuInfoPageList=spuInfoService.getSpuInfoPage(page1,category3Id);


      return Result.ok(spuInfoPageList);

    }



    //添加spu
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
      spuInfoService.saveSpuInfo(spuInfo);

      return Result.ok();
    }



        //根据spuId获取图片列表
    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId) {
        List<SpuImage> skuImageList = spuImageService.getspuImageList(spuId);

        return Result.ok(skuImageList);

    }



    //根据spuId获取销售属性
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }





}
