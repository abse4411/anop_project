package com.anop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    //security的鉴权排除的url列表
    private static final String[] excludedAuthPages = {
        "/login",
        "/logout",
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
            .formLogin().authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/user"))
            .and()
            .logout();
        CookieServerCsrfTokenRepository tokenRepository = new CookieServerCsrfTokenRepository();
        tokenRepository.setCookieHttpOnly(false);
        http.csrf().csrfTokenRepository(tokenRepository);
        //http.csrf().disable();
        return http.build();
    }
}

//@Configuration
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .httpBasic()
//            .and()
//            .authorizeRequests()
//            .anyRequest().authenticated()
//            .and()
//            .cors()
//            .and()
//            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//            .and()
//            .formLogin()
//            .successForwardUrl("/user");
//    }
//}