package com.anop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;


@EnableWebFluxSecurity
public class SecurityConfig {
    //security的鉴权排除的url列表
    private static final String[] excludedAuthPages = {
        "v1/auth/login",
        "v1/auth/logout",
        "v1/auth/user",
    };
    @Bean
    SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        http
            .authorizeExchange()
            //无需进行权限过滤的请求路径
            .pathMatchers(excludedAuthPages).permitAll()
            //option 请求默认放行
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .anyExchange().authenticated()
            .and()
            .cors()
            .and()
            .httpBasic().disable()
            .formLogin()
            .and()
            .logout();
        CookieServerCsrfTokenRepository tokenRepository = new CookieServerCsrfTokenRepository();
        tokenRepository.setCookieHttpOnly(false);
        http.csrf().csrfTokenRepository(tokenRepository);
        return http.build();
    }
}
