package com.atguigu.gmall.item;


import com.atguigu.gmall.common.config.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.threadpool.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;





@EnableThreadPool
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.product",
        "com.atguigu.gmall.feign.search"
})
@SpringCloudApplication
public class ItemMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
