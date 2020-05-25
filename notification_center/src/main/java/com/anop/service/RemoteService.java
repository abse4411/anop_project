package com.anop.service;

import com.anop.component.FeignAuthRequestInterceptor;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@FeignClient(value = "AUTH-SERVICE", configuration = FeignAuthRequestInterceptor.class)
public interface RemoteService {
    @GetMapping("/hello3")
    Object hello3(@RequestParam(name = "name") String name);

    @PostMapping("/hello4")
    Object hello4(@RequestBody() @Valid @NotNull String name);
}
