package com.anop.controller;

import com.anop.pojo.UserInfo;
import com.anop.pojo.security.User;
import com.anop.resource.UserInfoResource;
import com.anop.resource.UserInfoUpdateResource;
import com.anop.service.UserInfoService;
import com.anop.util.JsonResult;
import com.anop.util.Message;
import com.anop.util.SecurityUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Api(value = "用户信息", tags = "用户信息")
@RestController
public class UserInfoController {

    @Autowired
    UserInfoService userInfoService;

    @ApiOperation(value = "获取用户信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功", response = Message.class),
            @ApiResponse(code = 404, message = "未找到当前用户信息", response = Message.class)
    })
    @GetMapping("profile")
    public Object getUserInfo() {
        User loginUser = SecurityUtils.getLoginUser(User.class);
        UserInfoResource resource = userInfoService.getUserInfoResource(loginUser.getId());
        if(resource == null) {
            return JsonResult.notFound("userInfo was not found", null);
        }
        return JsonResult.ok(resource);
    }

    @ApiOperation(value = "更新用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="resource", value = "更新用户信息参数", required = true, dataType = "UserInfoUpdateResource")
    })
    @ApiResponses({
            @ApiResponse(code = 204, message = "更新用户信息成功", response = Message.class),
            @ApiResponse(code = 403, message = "没有权限更改用户信息", response = Message.class),
            @ApiResponse(code = 404, message = "未找到当前用户信息", response = Message.class),
            @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class),
    })
    @PutMapping("profile")
    public Object updateUserInfo(@RequestBody @Valid UserInfoUpdateResource resource) {

        User loginUser = SecurityUtils.getLoginUser(User.class);
        UserInfo userInfo = userInfoService.getUserInfoByUserId(loginUser.getId());

        if (userInfo == null) {
            return JsonResult.notFound("userInfo was not found", null);
        }
        int result = userInfoService.updateUserInfo(userInfo, resource);
        if (result == -1) {
            return JsonResult.forbidden("you have no permission to modify userInfo", null);
        }
        return JsonResult.noContent().build();
    }

    @ApiOperation(value = "上传并修改用户头像")
    @ApiImplicitParams({
            @ApiImplicitParam(name="avatarimg", value = "图片文件", required = true, dataType = "MultipartFile")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "修改用户头像成功", response = Message.class),
            @ApiResponse(code = 403, message = "没有权限更改用户信息", response = Message.class),
            @ApiResponse(code = 404, message = "未找到当前用户信息", response = Message.class),
            @ApiResponse(code = 500, message = "服务器内部错误，上传文件失败", response = Message.class),
    })
    @PostMapping("avatar")
    public Object uploadAvatar(@RequestParam("avatarimg") MultipartFile file) {

        String url = "";
        try {
            url = userInfoService.saveAvatarFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return JsonResult.internalServerError("Failed to upload file", null);
        }

        User loginUser = SecurityUtils.getLoginUser(User.class);
        UserInfo userInfo = userInfoService.getUserInfoByUserId(loginUser.getId());
        if(userInfo == null) {
            return JsonResult.notFound("userInfo was not found", null);
        }
        int result = userInfoService.updateAvatarUrl(userInfo, url);
        if(result == -1) {
            return JsonResult.forbidden("you have no permission to modify avatar", null);
        }
        return JsonResult.ok(url);

    }

}
