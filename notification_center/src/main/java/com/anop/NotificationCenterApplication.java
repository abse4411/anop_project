package com.anop;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@SpringBootApplication
@RestController
public class NotificationCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationCenterApplication.class, args);
    }

    @GetMapping("/hello1")
    public String hello(){
        return "Hello world!-NotificationCenterApplication";
    }

    @GetMapping("/user")
    public Principal home(Principal principal) {
        return principal;
    }
}

