package com.example.courseworkbyzayats.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
@EnableWebMvc
public class ResourceConfigs implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String iconsPath = "file:///D:/JavaProjects/courseWorkByZayats/src/main/resources/static/images/icons/";
        String avatarsPath = "file:///D:/JavaProjects/courseWorkByZayats/src/main/resources/static/images/avatars/";
        String stylesPath = "file:///D:/JavaProjects/courseWorkByZayats/src/main/resources/static/styles/";
        String javaScriptPath = "file:///D:/JavaProjects/courseWorkByZayats/src/main/resources/static/js/";

        registry.addResourceHandler("/images/icons/**").addResourceLocations(iconsPath);
        registry.addResourceHandler("/images/avatars/**").addResourceLocations(avatarsPath);
        registry.addResourceHandler("/styles/**").addResourceLocations(stylesPath);
        registry.addResourceHandler("/scripts/**").addResourceLocations(javaScriptPath);
    }
}
