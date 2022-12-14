package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/inner/rpc/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    @GetMapping("/addToCart")
    public Result<Object> addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num);

    @GetMapping("/deleteChecked")
    Result deleteChecked();
}
