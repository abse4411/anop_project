package com.anop.controller;

import com.anop.pojo.UserRequest;
import com.anop.resource.GroupUnreadNotificationCountResource;
import com.anop.resource.PageParmResource;
import com.anop.resource.UserRequestAddResource;
import com.anop.resource.UserRequestUpdateResource;
import com.anop.service.UserRequestService;
import com.anop.util.BindingResultUtils;
import com.anop.util.JsonResult;
import com.anop.util.Message;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 通知群加入申请控制器
 *
 * @author Xue_Feng
 */
@Api(value = "通知群组加入申请", tags = {"通知群组加入申请"})
@RestController
@RequestMapping("/pub/requests")
public class UserRequestController {
    @Autowired
    UserRequestService userRequestService;

    @ApiOperation(value = "申请加入指定通知群组")
    @ApiResponses({
        @ApiResponse(code = 204, message = "申请成功"),
        @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class),
        @ApiResponse(code = 403, message = "用户没有权限", response = Message.class),
    })
    @PostMapping()
    public Object addUserRequest(
        @RequestBody @Valid UserRequestAddResource resource) {
        int result = userRequestService.addUserRequest(resource);
        if (result == -1) {
            return JsonResult.forbidden(null, null);
        }
        return JsonResult.noContent().build();
    }

    @ApiOperation(value = "获取用户创建的通知群组加入申请列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class),
    })
    @GetMapping()
    public Object getUserRequests(@Valid PageParmResource page) {
        return JsonResult.ok(userRequestService.listUserRequest(page));
    }

    @ApiOperation(value = "获取用户管理的通知群组加入申请列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class),
    })
    @GetMapping("/manage")
    public Object getManageUserRequests(@Valid PageParmResource page) {
        return JsonResult.ok(userRequestService.listManageUserRequest(page));
    }

    @ApiOperation(value = "同意或者拒绝加入申请")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "申请id", required = true, dataType = "int"),
    })
    @ApiResponses({
        @ApiResponse(code = 204, message = "操作成功", response = GroupUnreadNotificationCountResource.class),
        @ApiResponse(code = 404, message = "加入申请不存在", response = Message.class),
        @ApiResponse(code = 403, message = "用户没有权限", response = Message.class),
        @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class),
    })
    @PostMapping("/{id}")
    public Object acceptOrDenyUserRequest(
        @RequestBody @Valid UserRequestUpdateResource resource,
        @PathVariable("id") int id) {
        UserRequest request = userRequestService.getUserRequest(id);
        if (request == null) {
            return JsonResult.notFound("user request was not found", null);
        }
        int result = userRequestService.acceptOrDenyUserRequest(request, resource.getIsAccepted());
        if (result == -1) {
            return JsonResult.forbidden(null, null);
        }
        return JsonResult.noContent().build();
    }
}
