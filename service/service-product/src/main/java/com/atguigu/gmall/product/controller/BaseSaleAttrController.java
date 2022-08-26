package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class BaseSaleAttrController {
    @Autowired
    BaseSaleAttrService baseSaleAttrService;




    // 获取销售属性
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        //操作base_sale_attr表
        List<BaseSaleAttr> list = baseSaleAttrService.list();
        return Result.ok(list);
    }


}
