package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    //http://cart.gmall.com/addCart.html?skuId=51&skuNum=1&sourceType=query


    @GetMapping("/addCart.html")
    public String addCartHtml(@RequestParam("skuId") Long skuId,
                              @RequestParam("skuNum") Integer skuNum,

                              Model model){


        //1、把指定商品添加到购物车
        System.out.println("web-all 获取到的用户id：");
        Result<Object> result = cartFeignClient.addToCart(skuId, skuNum);
        if (result.isOk()) {
            model.addAttribute("skuInfo",result.getData());
            model.addAttribute("skuNum",skuNum);
            return "cart/addCart";
        }else {
            String message = result.getMessage();
            model.addAttribute("msg",result.getData());
            return "cart/error";
        }

    }



    @GetMapping("/cart.html")
    public String cartHtml(){

        return "cart/index";
    }


    @GetMapping("/cart/deleteChecked")
    public String deleteChecked(){


        cartFeignClient.deleteChecked();
        return "redirect:http://cart.gmall.com/cart.html";
    }


}
