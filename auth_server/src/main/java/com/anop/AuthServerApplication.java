package com.anop;

import com.anop.util.JsonResult;
import com.anop.util.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@EnableEurekaClient
@RestController
@MapperScan("com.anop.mapper.security")
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

    @GetMapping("/hello3")
    public String hello(@RequestParam(name = "name") String name) {
        return "Hello" + name + "world!-AuthApplication";
    }

    @RequestMapping(path = "/user", method = {RequestMethod.GET, RequestMethod.POST})
    public Object user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal();
    }

    @PostMapping(path = "/failed")
    public Object failed() {
        return JsonResult.unauthorized("用户名或者密码错误", null);
    }

    @PostMapping(path = "/test")
    public Object test() {
        return JsonResult.noContent().build();
    }
}

