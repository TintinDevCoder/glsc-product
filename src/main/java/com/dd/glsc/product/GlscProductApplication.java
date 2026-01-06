package com.dd.glsc.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@MapperScan("com.dd.glsc.product.dao")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.dd.glsc.product.feign")
public class GlscProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlscProductApplication.class, args);
    }

}
