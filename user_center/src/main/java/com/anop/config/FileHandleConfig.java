package com.anop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileHandleConfig implements WebMvcConfigurer {

    private static final String AVATAR_UPLOAD_PATH = "/src/main/resources/static/avatarimg/";
    private static final String SERVICE_ADDRESS = "localhost:8082/";
    public static final String AVATAR_SAVE_PATH = SERVICE_ADDRESS + "avatarimg/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
    }

    public static String getUploadPath() {
        return System.getProperty("user.dir") + AVATAR_UPLOAD_PATH;
    }
}
