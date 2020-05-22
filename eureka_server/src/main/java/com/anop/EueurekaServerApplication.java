package com.anop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EueurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EueurekaServerApplication.class, args);
    }

}
