package com.anop.controller;

import com.anop.pojo.Group;
import com.anop.resource.AutoTodoResource;
import com.anop.resource.GroupUnreadNotificationCountResource;
import com.anop.resource.PageParmResource;
import com.anop.service.GroupService;
import com.anop.service.GroupUserService;
import com.anop.service.NotificationService;
import com.anop.util.JsonResult;
import com.anop.util.Message;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订阅者通知群组控制器
 *
 * @author Xue_Feng
 */
@Api(value = "订阅者通知群组", tags = {"订阅者通知群组"})
@RestController
@RequestMapping("/sub/groups")
public class SubscriberGroupController {
    @Autowired
    GroupService groupService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    GroupUserService groupUserService;

    @ApiOperation(value = "获取用户订阅的通知群组列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功", response = PageInfo.class),
        @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class),
    })
    @GetMapping()
    public Object getGroups(@Valid PageParmResource page) {
        return JsonResult.ok(groupService.listUserSubscribeGroupInfo(page));
    }

    @ApiOperation(value = "获取用户订阅的通知群组未读通知数")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "通知群组群号", required = true, dataType = "int"),
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功", response = GroupUnreadNotificationCountResource.class),
        @ApiResponse(code = 404, message = "通知群组不存在", response = Message.class),
        @ApiResponse(code = 403, message = "用户没有权限", response = Message.class),
    })
    @GetMapping("/{id}/notifications/unread_count")
    public Object getGroupUnreadNotificationCount(@PathVariable("id") int groupId) {
        Group group = groupService.getGroup(groupId);
        if (group == null) {
            return JsonResult.notFound("通知群组不存在", null);
        }
        GroupUnreadNotificationCountResource resource = notificationService.countGroupUnreadNotification(groupId);
        if (resource == null) {
            return JsonResult.forbidden("通知群组的普通成员才可以群组的未读通知数", null);
        }
        return JsonResult.ok(resource);
    }

    @ApiOperation(value = "取消订阅的通知群组")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "通知群组群号", required = true, dataType = "int"),
    })
    @ApiResponses({
        @ApiResponse(code = 204, message = "操作成功"),
        @ApiResponse(code = 404, message = "通知群组不存在", response = Message.class),
        @ApiResponse(code = 403, message = "用户没有权限", response = Message.class),
    })
    @DeleteMapping("/{id}")
    public Object quitGroup(@PathVariable("id") int groupId) {
        Group group = groupService.getGroup(groupId);
        if (group == null) {
            return JsonResult.notFound("通知群组不存在", null);
        }
        int result = groupService.quitGroup(group);
        if (result == -1) {
            return JsonResult.forbidden("通知群组的成员才可以取消订阅", null);
        }
        return JsonResult.noContent().build();
    }

    @ApiOperation("获取指定通知群组自动通知转待办选项")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "通知群组群号", required = true, dataType = "int"),
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class)
    })
    @GetMapping("/{id}/autoTodo")
    public Object getGroupUserAutoTodoOption(@PathVariable("id") int groupId) {
        AutoTodoResource resource = groupUserService.getAutoTodoOption(groupId);
        if (resource == null) {
            return JsonResult.forbidden("只有该通知群组成员才可以获取自动通知转待办选项信息", null);
        }
        return JsonResult.ok(resource);
    }

    @ApiOperation("更新指定通知群组自动通知转待办选项")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "通知群组群号", required = true, dataType = "int"),
    })
    @ApiResponses({
        @ApiResponse(code = 204, message = "更新成功"),
        @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class)
    })
    @PatchMapping("/{id}/autoTodo")
    public Object updateGroupUserAutoTodoOption(
        @RequestBody @Valid AutoTodoResource resource,
        @PathVariable("id") int groupId) {
        int result = groupUserService.updateAutoTodoOption(groupId, resource);
        if (result == -1) {
            return JsonResult.forbidden("只有该通知群组成员才可以设置自动通知转待办选项", null);
        }
        return JsonResult.noContent().build();
    }
}
