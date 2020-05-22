package com.anop;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class NotificationCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationCenterApplication.class, args);
    }

    @PostMapping("/hello1")
    public String hello(){
        return "Hello world!-NotificationCenterApplication";
    }

    @GetMapping("/user")
    public Principal home(Principal principal) {
        return principal;
    }
}

