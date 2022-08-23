package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class CategoryController {
    @Autowired
    BaseCategory1Service baseCategory1Service;
    @Autowired
    BaseCategory2Service baseCategory2Service;
    @Autowired
    BaseCategory3Service baseCategory3Service;

    //查询一级分类
    @GetMapping("/getCategory1")
    public Result getCategory1 (){
     List<BaseCategory1> category1List =baseCategory1Service.list();
     return Result.ok(category1List);

    }

    //查询二级分类
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") Long category1Id){
     List<BaseCategory2> category2List =  baseCategory2Service.getCategory1Child(category1Id);
     return Result.ok(category2List);
    }


    //查询三级分类
    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3 (@PathVariable("category2Id") Long category2Id){

        List<BaseCategory3> category3List =  baseCategory3Service.getCategory1Child(category2Id);
        return Result.ok(category3List);
    }









}
