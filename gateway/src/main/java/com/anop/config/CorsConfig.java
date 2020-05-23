package com.anop.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

//@Configuration
//public class CorsConfig {
//    @Bean
//    public CorsResponseHeaderFilter corsResponseHeaderFilter() {
//        return new CorsResponseHeaderFilter();
//    }
//
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.addAllowedHeader("*");
//        corsConfiguration.addAllowedMethod("*");
//        corsConfiguration.addAllowedOrigin("*");
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.setMaxAge(36000L);
//
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        return new CorsWebFilter(source);
//    }
//}
//class CorsResponseHeaderFilter implements GlobalFilter, Ordered {
//    @Override
//    public int getOrder() {
//        // 指定此过滤器位于NettyWriteResponseFilter之后
//        // 即待处理完响应体后接着处理响应头
//        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
//    }
//
//    @Override
//    @SuppressWarnings("serial")
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        return chain.filter(exchange).then(Mono.defer(() -> {
//            exchange.getResponse().getHeaders().entrySet().stream()
//                .filter(kv -> (kv.getValue() != null && kv.getValue().size() > 1))
//                .filter(kv -> (kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
//                    || kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)))
//                .forEach(kv ->
//                {
//                    kv.setValue(new ArrayList<String>() {{
//                        add(kv.getValue().get(0));
//                    }});
//                });
//
//            return chain.filter(exchange);
//        }));
//    }
//}