package com.anop.controller;

import com.anop.pojo.Todo;
import com.anop.pojo.security.User;
import com.anop.resource.*;
import com.anop.service.TodoService;
import com.anop.util.JsonResult;
import com.anop.util.Message;
import com.anop.util.SecurityUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * @author chcic
 */
@Api(tags = "待办事项")
@RestController
@RequestMapping("/todos")
public class TodoController {

    @Resource(name = "todoServiceImpl")
    TodoService todoService;

    @ApiOperation(value = "添加待办事项", notes = "添加待办事项")
    @ApiResponses({
            @ApiResponse(code = 201, message = "成功创建", response = Todo.class),
            @ApiResponse(code = 422, message = "参数未通过验证", response = Message.class)
    })
    @PostMapping()
    public Object addTodo(
            @RequestBody @Valid TodoAddResource resource) throws URISyntaxException {
        Todo todo = todoService.addTodo(resource);
        return JsonResult.created(new URI("http://localhost:8080/v1/todos/" + todo.getId())).body(todo);
    }

    @ApiOperation(value = "获取待办事项列表", notes = "获取指定类型的待办事项列表")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "flag", value = "0:所有 1:重要 2:收藏", required = false, dataType = "int")
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取", response = PageInfo.class),
            @ApiResponse(code = 400, message = "flag参数验证错误"),
            @ApiResponse(code = 422, message = "分页参数验证错误", response = Message.class)
    })
    @GetMapping()
    public Object getTodoList(@Valid TodoFlagResource flagResource, @Valid PageParmResource page) {
        return JsonResult.ok(todoService.listUserTodo(page, flagResource));
    }

    @ApiOperation(value = "更新待办事项的信息", notes = "更新待办事项的信息（不包括完成状态）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "待办事项id", required = true, dataType = "int"),
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "更新成功"),
            @ApiResponse(code = 404, message = "未找到指定id的待办事项", response = Message.class),
            @ApiResponse(code = 403, message = "没有此待办事项的访问权限", response = Message.class)
    })
    @PutMapping("/{id}")
    public Object updateTodo(
            @RequestBody @Valid TodoUpdateResource resource,
            @PathVariable int id) {
        Todo todo = todoService.getTodo(id);

        if (todo == null) {
            return JsonResult.notFound("todoItem was not found", null);
        }
        int result = todoService.updateTodo(todo, resource);
        if (result == -1) {
            return JsonResult.forbidden("you have no permission to modify this todoitem", null);
        }
        return JsonResult.noContent().build();
    }

    @ApiOperation(value = "切换待办事项状态", notes = "切换待办事项状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "待办事项id", required = true, dataType = "int"),
            @ApiImplicitParam(name = "flag", value = "0:完成 1:重要 2:收藏", dataType = "int"),
    })
    @ApiResponses({
            @ApiResponse(code = 204, message = "切换状态成功"),
            @ApiResponse(code = 403, message = "没有此待办事项的访问权限", response = Message.class),
            @ApiResponse(code = 404, message = "未找到指定id的待办事项", response = Message.class)
    })
    @PutMapping("/check/{id}")
    public Object checkTodo(@PathVariable int id, @Valid TodoFlagResource resource) {
        Todo todo = todoService.getTodo(id);
        if (todo == null) {
            return JsonResult.notFound("todoItem was not found", null);
        }
        int result = todoService.checkTodo(todo, resource);
        if (result == -1) {
            return JsonResult.forbidden("you have no permission to check this todoitem", null);
        }
        return JsonResult.noContent().build();
    }

    @ApiOperation(value = "删除指定id的待办事项", notes = "删除指定id的待办事项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "待办事项id", required = true, dataType = "int"),
    })
    @ApiResponses({
            @ApiResponse(code = 204, message = "删除成功"),
            @ApiResponse(code = 403, message = "没有此待办事项的访问权限", response = Message.class),
            @ApiResponse(code = 404, message = "未找到指定id的待办事项", response = Message.class)
    })
    @DeleteMapping("/{id}")
    public Object deleteTodo(@PathVariable int id) {
        Todo todo = todoService.getTodo(id);
        if (todo == null) {
            return JsonResult.notFound("todoItem was not found", null);
        }
        int result = todoService.deleteTodo(todo);
        if (result == -1) {
            return JsonResult.forbidden("you have no permission to delete this todoitem", null);
        }
        return JsonResult.noContent().build();
    }

    @ApiOperation(value = "获取指定id的待办事项信息", notes = "获取指定id的待办事项信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "待办事项id", required = true, dataType = "int"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功", response = Todo.class),
            @ApiResponse(code = 403, message = "没有此待办事项的访问权限", response = Message.class),
            @ApiResponse(code = 404, message = "未找到指定id的待办事项", response = Message.class)
    })
    @GetMapping("/{id}")
    public Object getTodo(@PathVariable int id) {
        Todo todo = todoService.getTodo(id);
        if (todo == null) {
            return JsonResult.notFound("todoItem was not found", null);
        }
        if (!todo.getUserId().equals(SecurityUtils.getLoginUser(User.class).getId())) {
            return JsonResult.forbidden("you have no permission to get this todoitem", null);
        }
        return JsonResult.ok(todo);
    }

    @ApiOperation(value = "获取历史待办事项列表", notes = "获取历史待办事项列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取", response = PageInfo.class),
            @ApiResponse(code = 422, message = "分页参数验证错误", response = Message.class)
    })
    @GetMapping("/histories")
    public Object getHistoryTodoList(@Valid PageParmResource page) {
        return JsonResult.ok(todoService.listHistoryTodo(page));
    }

    @ApiOperation(value = "批量添加待办事项", notes = "获取历史待办事项列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "成功添加", response = PageInfo.class),
        @ApiResponse(code = 422, message = "分页参数验证错误", response = Message.class)
    })
    @PostMapping("/batch")
    public Object addTodosBatch(@RequestBody @Valid TodoBatchAddResource resource) {
        return JsonResult.ok(todoService.addTodos(resource));
    }

    @ApiOperation(value = "搜索满足条件的待办事项", notes = "获取历史待办事项列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取", response = PageInfo.class),
            @ApiResponse(code = 422, message = "分页参数验证错误", response = Message.class)
    })
    @PostMapping("/search")
    public Object searchTodos(@RequestParam String title, @Valid PageParmResource page) {
        return JsonResult.ok(todoService.searchTodosLike(title, page));
    }
}
