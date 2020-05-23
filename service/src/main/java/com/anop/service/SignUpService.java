package com.anop.service;

import com.anop.pojo.ValidEmail;
import com.anop.resource.UserSignUpResource;
import com.anop.pojo.security.User;

import javax.mail.MessagingException;

/**
 * @author SilverBay
 */
public interface SignUpService {

    /**
     * 从ValidEmail表中根据邮箱获取validEmail对象
     *
     * @param email 邮箱字符串
     * @return 若找到，则返回数据库表响应列对应的对象；否则返回null
     */
    ValidEmail getValidEmail(String email);

    /**
     * 判断邮箱是否已被注册
     *
     * @param email 邮箱字符串
     * @return 该邮箱是否已注册布尔值
     */
    boolean isSignedUpEmail(String email);

    /**
     * 判断用户名是否已被使用
     *
     * @param userName 用户名字符串
     * @return 该用户名是否已被使用布尔值
     */
    boolean isSignedUpUsername(String userName);

    /**
     * 保存验证邮件
     *
     * @param email 验证邮箱
     * @param code  验证码
     */
    void saveValidEmail(String email, String code);

    /**
     * 发送验证邮件
     *
     * @param email 接收者邮箱
     * @return 验证码
     * @throws MessagingException 发送失败抛出该异常
     */
    String sendValidEmail(String email) throws MessagingException;

    /**
     * 用户注册方法
     * 进行新用户的插入、新用户信息的插入、验证邮箱状态改为已验证
     *
     * @param resource 用户注册请求体对应的资源类
     * @return 新用户对象
     */
    User signUp(UserSignUpResource resource);
}
