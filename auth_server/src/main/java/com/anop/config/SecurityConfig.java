package com.anop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;


/**
 * 安全配置类
 *
 * @author Xue_Feng
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String VALID_EMAIL_URL = "/v1/valid_email";
    private static final String SUCCESS_FORWARD_URL = "/user";
    private static final String FAILURE_FORWARD_URL = "/failed";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .httpBasic().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, VALID_EMAIL_URL).permitAll()
            .anyRequest().authenticated()
            .and()
            .cors()
            .and()
            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .formLogin()
            .successForwardUrl(SUCCESS_FORWARD_URL)
            .failureForwardUrl(FAILURE_FORWARD_URL);
    }
}
