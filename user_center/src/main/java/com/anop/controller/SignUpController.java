package com.anop.controller;

import com.anop.pojo.ValidEmail;
import com.anop.resource.UserSignUpResource;
import com.anop.resource.ValidEmailResource;
import com.anop.service.SignUpService;
import com.anop.util.BindingResultUtils;
import com.anop.util.JsonResult;
import com.anop.util.Message;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Date;

@Api(value = "用户注册", tags = "用户注册")
@RestController
public class SignUpController {

    @Autowired
    SignUpService signUpService;

    @ApiOperation(value = "验证邮箱并发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="resource", value = "验证邮箱参数", required = true, dataType = "ValidEmailResource")
    })
    @ApiResponses({
            @ApiResponse(code = 204, message = "发送验证码成功", response = Message.class),
            @ApiResponse(code = 400, message = "邮箱已被注册", response = Message.class),
            @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class),
            @ApiResponse(code = 500, message = "服务器内部错误，发送邮件失败", response = Message.class)
    })
    @PostMapping("valid_email")
    public Object validEmail(
            @RequestBody @Valid ValidEmailResource resource,
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return JsonResult.unprocessableEntity("error in validating", BindingResultUtils.getErrorList(bindingResult));
        }
        if(signUpService.isSignedUpEmail(resource.getEmail())) {
            return JsonResult.badRequest("Email has been signed up", null);
        }
        try {
            signUpService.sendValidEmail(resource.getEmail());
        } catch (MessagingException e) {
            return JsonResult.internalServerError("Failed to send email", null);
        }
        return JsonResult.noContent().build();
    }

    @ApiOperation(value = "用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name="resource", value = "用户注册参数", required = true, dataType = "UserSignUpResource")
    })
    @ApiResponses({
            @ApiResponse(code = 204, message = "注册成功", response = Message.class),
            @ApiResponse(code = 400, message = "邮箱未验证/邮箱已被注册/用户名已被使用/验证码过期/验证码错误", response = Message.class),
            @ApiResponse(code = 422, message = "请求体参数验证错误", response = Message.class)
    })
    @PostMapping("signup")
    public Object signUp(
            @RequestBody @Valid UserSignUpResource resource,
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return JsonResult.unprocessableEntity("error in validating", BindingResultUtils.getErrorList(bindingResult));
        }
        ValidEmail validEmail = signUpService.getValidEmail(resource.getEmail());
        if(validEmail == null) {
            return JsonResult.badRequest("Email is not verified", null);
        }
        if( signUpService.isSignedUpEmail(resource.getEmail()) ) {
            return JsonResult.badRequest("Email has been signed up", null);
        }
        if( signUpService.isSignedUpUsername(resource.getUsername()) ) {
            return JsonResult.badRequest("Username has been used", null);
        }
        if( validEmail.getExpire().before(new Date()) ) {
            return JsonResult.badRequest("Verification code is expired", null);
        }
        if( !resource.getCode().equals(validEmail.getCode().toUpperCase()) ) {
            return JsonResult.badRequest("Verification code mismatch", null);
        }
        signUpService.signUp(resource);
        return JsonResult.noContent().build();
    }

}
