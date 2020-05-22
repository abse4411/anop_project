package com.anop;

import com.anop.security.pojo.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.anop.security.mapper")
@RestController
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @RequestMapping(path = "/user", method = {RequestMethod.GET, RequestMethod.POST})
    public Mono<User> user() {
        Mono<User> user = ReactiveSecurityContextHolder.getContext()
            .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getPrincipal)
            .cast(User.class);
        return user;
    }
}

