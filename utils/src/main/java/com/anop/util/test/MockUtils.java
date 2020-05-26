package com.anop.util.test;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * 模拟测试数据工具类
 *
 * @author Xue_Feng
 */
@Component
public class MockUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static void mockLoginUser(String username) {
        UserDetails user = MockUtils.applicationContext.getBean(UserDetailsService.class).loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.commaSeparatedStringToAuthorityList(null));
        SecurityContextHolder.getContext().setAuthentication(authRequest);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MockUtils.applicationContext = applicationContext;
    }
}
