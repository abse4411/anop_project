package com.anop;

import com.anop.config.BeanConfig;
import com.anop.mapper.CustomUserMapper;
import com.anop.pojo.security.User;
import com.anop.resource.PasswordUpdateResource;
import com.anop.service.UserService;
import com.anop.util.SecurityUtils;
import com.anop.util.test.MockUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService service;
    @Autowired
    CustomUserMapper userMapper;

    @BeforeEach
    public void mockLoginUser() {
        MockUtils.mockLoginUser("zwn");
    }

    @Test
    @Transactional
    @Rollback
    public void resetPasswordTest() {
        PasswordEncoder passwordEncoder = new BeanConfig().passwordEncoder();
        PasswordUpdateResource resource = new PasswordUpdateResource();
        resource.setOldPassword("123456");
        resource.setNewPassword("654321");

        User user = SecurityUtils.getLoginUser(User.class);
        String oldPassword = user.getPassword();
        Assertions.assertEquals(passwordEncoder.encode(resource.getOldPassword()),
                                oldPassword, "旧密码匹配失败");

        service.resetPassword(resource.getNewPassword());
        user = userMapper.selectByUsername("zwn");
        Assertions.assertEquals(passwordEncoder.encode(resource.getNewPassword()),
                                user.getPassword(), "新密码匹配失败");
    }

}
