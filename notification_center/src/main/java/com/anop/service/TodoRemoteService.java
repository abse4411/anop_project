package com.anop.service;

import com.anop.component.FeignAuthRequestInterceptor;
import com.anop.resource.TodoAddResource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "TODO-SERVICE", configuration = FeignAuthRequestInterceptor.class)
public interface TodoRemoteService {
    @PostMapping("/todos")
    String addTodo(@RequestBody @Valid TodoAddResource resource);
}
