package com.anop.security.impl;

import com.anop.security.mapper.RoleMapper;
import com.anop.security.mapper.UserMapper;
import com.anop.security.pojo.Role;
import com.anop.security.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Mono<UserDetails> findByUsername(String userName) {
        //查数据库
        User user = userMapper.loadUserByUsername(userName);
        if (null != user) {
            List<Role> roles = roleMapper.getRolesByUserId(user.getId());
            user.setAuthorities(roles);
        } else {
            return  Mono.error(new UsernameNotFoundException(userName));
        }

        return Mono.just(user);
    }
}
