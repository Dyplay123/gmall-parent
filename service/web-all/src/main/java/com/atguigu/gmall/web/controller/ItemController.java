package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.web.feign.SkuDetailFeifnClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

@Autowired
    SkuDetailFeifnClient skuDetailFeifnClient;

//    @GetMapping("/{skuId}.html")
    @GetMapping("/{skuId}.html")
        public String item(@PathVariable("skuId") Long skuId, Model model){
        Result<SkuDetailTo> skuDetail = skuDetailFeifnClient.getSkuDetail(skuId);
        if (skuDetail.isOk()){
            SkuDetailTo skuDetailTo = skuDetail.getData();
            if (skuDetailTo == null || skuDetailTo.getSkuInfo() == null){
                return "item/404";
            }

            model.addAttribute("categoryView",skuDetailTo.getCategoryView());
            model.addAttribute("skuInfo",skuDetailTo.getSkuInfo());
            model.addAttribute("price",skuDetailTo.getPrice());
            model.addAttribute("spuSaleAttrList",skuDetailTo.getSpuSaleAttrList());//spu的销售属性列表
            model.addAttribute("valuesSkuJson",skuDetailTo.getValuesSkuJson());//json

        }
        return "item/index";
        }

}
