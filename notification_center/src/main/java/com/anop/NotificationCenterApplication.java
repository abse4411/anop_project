package com.anop;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

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
    public Principal user(Principal principal) {
        return principal;
    }

    @GetMapping("/")
    public Message home() {
        return new Message("Hello world");
    }

    class Message {
        private String id = UUID.randomUUID().toString();
        private String content;

        public Message(String content) {
            this.content = content;
        }

        public Message() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

