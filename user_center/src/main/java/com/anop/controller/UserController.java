package com.anop.controller;

import com.anop.pojo.security.User;
import com.anop.resource.PasswordUpdateResource;
import com.anop.resource.UserResource;
import com.anop.service.UserService;
import com.anop.util.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "用户中心", tags = "用户中心")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(path = "/user", method = {RequestMethod.GET, RequestMethod.POST})
    public Object user() {
        User loginUser = SecurityUtils.getLoginUser(User.class);
        UserResource resource = PropertyMapperUtils.map(loginUser, UserResource.class);
        return JsonResult.ok(resource);
    }

    @PostMapping(path = "/failed")
    public Object failed() {
        return JsonResult.unauthorized("用户名或者密码错误", null);
    }

    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="resource", value = "修改密码参数", required = true, dataType = "PasswordUpdateResource")
    })
    @ApiResponses({
            @ApiResponse(code = 204, message = "修改密码成功", response = Message.class),
            @ApiResponse(code = 400, message = "旧密码错误", response = Message.class),
            @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class),
            @ApiResponse(code = 500, message = "服务器内部错误，修改密码失败", response = Message.class)
    })
    @PostMapping("account/password")
    public Object resetPassword(@RequestBody @Valid PasswordUpdateResource resource) {

        User user = SecurityUtils.getLoginUser(User.class);
        if( !userService.isRightOldPassword(resource.getOldPassword()) ) {
            return JsonResult.badRequest("The old password is incorrect", null);
        }

        int result = userService.resetPassword(resource.getNewPassword());
        if(result == -1) {
            return JsonResult.internalServerError("Failed to update password", null);
        }
        return JsonResult.noContent().build();
    }

}
