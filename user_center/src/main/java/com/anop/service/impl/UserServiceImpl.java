package com.anop.service.impl;

import com.anop.config.BeanConfig;
import com.anop.mapper.CustomUserMapper;
import com.anop.pojo.security.User;
import com.anop.service.UserService;
import com.anop.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author SilverBay
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    CustomUserMapper customUserMapper;

    @Override
    public boolean isRightOldPassword(String oldPassword) {
        PasswordEncoder passwordEncoder = new BeanConfig().passwordEncoder();
        String userPassword = SecurityUtils.getLoginUser(User.class).getPassword();
        return passwordEncoder.matches(oldPassword, userPassword);
    }

    @Override
    public int resetPassword(String newPassword) {
        User user = new User();
        user.setId(SecurityUtils.getLoginUser(User.class).getId());
        PasswordEncoder passwordEncoder = new BeanConfig().passwordEncoder();
        user.setPassword(passwordEncoder.encode(newPassword));
        return customUserMapper.updateByPrimaryKeySelective(user);
    }
}
