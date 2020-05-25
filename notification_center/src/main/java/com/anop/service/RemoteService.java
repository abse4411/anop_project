package com.anop.service;

import com.anop.component.FeignAuthRequestInterceptor;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "AUTH-SERVICE", configuration = FeignAuthRequestInterceptor.class)
public interface RemoteService {
    @GetMapping("/hello3")
    String hello(@RequestParam(name = "name") String name) throws FeignException;
}
