package com.anop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 通知微服务启动类
 *
 * @author Xue_Feng
 */
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.anop.mapper")
public class NotificationCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationCenterApplication.class, args);
    }
}

