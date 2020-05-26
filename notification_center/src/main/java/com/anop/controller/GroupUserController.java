package com.anop.controller;

import com.github.pagehelper.PageInfo;
import com.anop.pojo.GroupUser;
import com.anop.resource.GroupUserResource;
import com.anop.resource.GroupUserUpdateResource;
import com.anop.resource.PageParmResource;
import com.anop.service.GroupUserService;
import com.anop.util.BindingResultUtils;
import com.anop.util.JsonResult;
import com.anop.util.Message;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 通知群组成员控制器
 *
 * @author Xue_Feng
 */
@Api(value = "通知群组成员", tags = {"通知群组成员"})
@RestController
@RequestMapping("/pub/groups/{gid}/users")
public class GroupUserController {
    @Autowired
    GroupUserService groupUserService;

    @ApiOperation("获取指定通知群组的成员信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "gid", value = "通知群组群号", required = true, dataType = "int"),
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功", response = GroupUserResource.class),
        @ApiResponse(code = 403, message = "用户没有权限", response = Message.class),
        @ApiResponse(code = 404, message = "成员不存在", response = Message.class)
    })
    @GetMapping("/{uid}")
    public Object getGroupUser(@PathVariable("gid") int groupId, @PathVariable("uid") int userId) {
        GroupUser groupUser = groupUserService.getGroupUser(userId, groupId);
        if (groupUser == null) {
            return JsonResult.notFound("groupUser was not found", null);
        }
        GroupUserResource userResource = groupUserService.getGroupUserInfo(userId, groupId);
        if (userResource == null) {
            return JsonResult.forbidden(null, null);
        }
        return JsonResult.ok(userResource);
    }

    @ApiOperation("获取指定通知群组的成员列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "gid", value = "通知群组群号", required = true, dataType = "int"),
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功", response = PageInfo.class),
        @ApiResponse(code = 403, message = "用户没有权限", response = Message.class),
        @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class)
    })
    @GetMapping()
    public Object getGroupUsers(
        @Valid PageParmResource page,
        @PathVariable("gid") int groupId) {
        PageInfo<List<GroupUserResource>> listPageInfo = groupUserService.listGroupUser(page, groupId);
        if (listPageInfo == null) {
            return JsonResult.forbidden(null, null);
        }
        return JsonResult.ok(listPageInfo);
    }

    @ApiOperation("更新指定通知群组成员权限")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "gid", value = "通知群组群号", required = true, dataType = "int"),
        @ApiImplicitParam(name = "uid", value = "成员id", required = true, dataType = "int")
    })
    @ApiResponses({
        @ApiResponse(code = 204, message = "更新成功"),
        @ApiResponse(code = 403, message = "用户没有权限", response = Message.class),
        @ApiResponse(code = 404, message = "成员不存在", response = Message.class),
        @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class)
    })
    @PatchMapping("/{uid}")
    public Object updateGroupUserRole(
        @RequestBody @Valid GroupUserUpdateResource resource,
        @PathVariable("gid") int groupId,
        @PathVariable("uid") int userId) {
        GroupUser groupUser = groupUserService.getGroupUser(userId, groupId);
        if (groupUser == null) {
            return JsonResult.notFound("groupUser was not found", null);
        }
        int result = groupUserService.updateGroupUserRole(groupUser, resource);
        if (result == -1) {
            return JsonResult.forbidden(null, null);
        }
        return JsonResult.noContent().build();
    }

    @ApiOperation("删除指定通知群组成员")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "gid", value = "通知群组群号", required = true, dataType = "int"),
        @ApiImplicitParam(name = "uid", value = "成员id", required = true, dataType = "int")
    })
    @ApiResponses({
        @ApiResponse(code = 204, message = "更新成功"),
        @ApiResponse(code = 403, message = "用户没有权限", response = Message.class),
        @ApiResponse(code = 404, message = "成员不存在", response = Message.class),
    })
    @DeleteMapping("/{uid}")
    public Object deleteGroupUser(@PathVariable("gid") int groupId, @PathVariable("uid") int userId) {
        GroupUser groupUser = groupUserService.getGroupUser(userId, groupId);
        if (groupUser == null) {
            return JsonResult.notFound("groupUser was not found", null);
        }
        int result = groupUserService.deleteGroupUser(groupUser);
        if (result == -1) {
            return JsonResult.forbidden(null, null);
        }
        return JsonResult.noContent().build();
    }
}
