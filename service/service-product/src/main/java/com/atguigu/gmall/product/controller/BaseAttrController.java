package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("")
public class BaseAttrController {
    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    //查询平台属性
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList (@PathVariable("category1Id") Long category1Id
            , @PathVariable("category2Id") Long category2Id
            , @PathVariable("category3Id") Long category3Id){
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoService.getAttrInfoList(category1Id,category2Id,category3Id);

        return Result.ok();
    }
}
