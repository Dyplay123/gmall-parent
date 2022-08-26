package com.atguigu.gmall.product.config.minio;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "app.minio")
@Component
@Data
public class MinioProperties {

    String endpoint;
    String ak;
    String sk;
    String bucketName;

}
