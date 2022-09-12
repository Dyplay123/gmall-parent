package com.atguigu.gmall.cart;


import com.atguigu.gmall.common.config.annotation.EnableAutoExceptionHandler;
import com.atguigu.gmall.common.config.annotation.EnableAutoFeignInterceptor;
import com.atguigu.gmall.common.config.annotation.EnableThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
@EnableThreadPool
@EnableAutoFeignInterceptor
@EnableAutoExceptionHandler
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@SpringCloudApplication
public class CartMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartMainApplication.class,args);
    }
}
