package com.atguigu.gmall.item;


import com.atguigu.gmall.common.config.annotation.EnableThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * 1、公共的配置搬家放到 service-util
 * 2、当前项目依赖了 service-util
 *
 * 当前应用启动只会扫描 ItemMainApplication 所在包的所有组件
 * - com.atguigu.gmall.item.*****
 * - com.atguigu.gmall.common.**
 */


@EnableThreadPool
@EnableFeignClients
@SpringCloudApplication
public class ItemMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
