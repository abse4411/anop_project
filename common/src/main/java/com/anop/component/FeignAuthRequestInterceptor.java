package com.anop.component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 微服务验证信息转发调用拦截器
 *
 * @author Xue_Feng
 */
public class FeignAuthRequestInterceptor implements RequestInterceptor {
    private static final String XSRF_TOKEN_HEADER_NAME = "X-XSRF-TOKEN";
    private static final String COOKIE_HEADER_NAME = "Cookie";
    private final Logger logger = LoggerFactory.getLogger(FeignAuthRequestInterceptor.class);

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Cookie[] cks = request.getCookies();
        if (cks.length > 0) {
            List<String> cookies = new ArrayList<>(cks.length);
            for (int i = 0; i < cks.length; i++) {
                cookies.add(cks[i].getName() + "=" + cks[i].getValue());
                logger.info("cookie found:{}", cks[i].getName());
                if (!cks[i].getName().equals(XSRF_TOKEN_HEADER_NAME)) {
                    requestTemplate.header(XSRF_TOKEN_HEADER_NAME, cks[i].getValue());
                }
            }
            requestTemplate.header(COOKIE_HEADER_NAME, cookies);
        }
    }
}
