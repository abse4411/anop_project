package com.anop.service;

import com.anop.component.FeignAuthRequestInterceptor;
import com.anop.resource.TodoAddResource;
import com.anop.resource.TodoBatchAddResource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 备忘录微服务调用类
 *
 * @author Xue_Feng
 */
@FeignClient(value = "TODO-SERVICE", configuration = FeignAuthRequestInterceptor.class)
public interface TodoRemoteService {
    /**
     * 将指定通知转化为代办事项
     *
     * @param resource 待办事项添加参数
     * @return 处理结果
     */
    @PostMapping("/todos")
    String addTodo(@RequestBody @Valid TodoAddResource resource);

    /**
     * 批量添加待办事项
     *
     * @param resource 批量添加待办事项参数
     * @return 处理结果
     */
    @PostMapping("/todos/batch")
    String addTodosBatch(@RequestBody @Valid TodoBatchAddResource resource);
}
