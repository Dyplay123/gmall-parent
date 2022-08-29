package com.atguigu.gmall.item.controller;


import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkuIdBitMapController {


    @GetMapping("/sync/skuid/bitmap")
    public Result syncBitMap(){

        return Result.ok();
    }
}
