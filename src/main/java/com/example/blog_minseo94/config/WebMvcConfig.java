package com.example.blog_minseo94.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/blog_img/**")
                .addResourceLocations("file:///D:/blog_minseo94/src/main/resources/static/blog_img/");

        registry.addResourceHandler("/blog_file/**")
                .addResourceLocations("file:///D:/blog_minseo94/src/main/resources/static/blog_file/");

    }
}
