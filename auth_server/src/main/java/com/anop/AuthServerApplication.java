package com.anop;

import com.anop.pojo.security.User;
import com.anop.util.JsonResult;
import com.anop.util.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@SpringBootApplication
@EnableEurekaClient
@RestController
@MapperScan("com.anop.mapper.security")
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

    @GetMapping("/hello3")
    public JsonResult hello(@RequestParam(name = "name") String name) {
        return JsonResult.notFound("Hello" + name + "world!-AuthApplication", null);

    }

    @PostMapping("/hello4")
    public Object hello2(@RequestBody() @Valid @NotNull String name, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || bindingResult.hasFieldErrors()) {
            return "Error";
        }
        return JsonResult.ok(new User());
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

