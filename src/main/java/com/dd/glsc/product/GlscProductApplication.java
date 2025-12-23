package com.dd.glsc.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.dd.glsc.product.dao")
@SpringBootApplication
public class GlscProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlscProductApplication.class, args);
    }

}
