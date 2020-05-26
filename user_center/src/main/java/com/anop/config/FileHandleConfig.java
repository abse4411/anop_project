package com.anop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileHandleConfig implements WebMvcConfigurer {

    private static final String RESOURCE_UPLOAD_PATH = "/src/main/resources/static/";
    private static final String SERVICE_ADDRESS = "localhost:8082/";
    public static final String AVATAR_GET_PATH = SERVICE_ADDRESS + "avatarimg/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String absolutePath = System.getProperty("user.dir") + "/user_center" + RESOURCE_UPLOAD_PATH;
        registry.addResourceHandler("/**").addResourceLocations("File:" + absolutePath);
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
    }

    public static String getImageUploadPath() {
        return System.getProperty("user.dir") + "/user_center" + RESOURCE_UPLOAD_PATH + "/avatarimg/";
    }

}
