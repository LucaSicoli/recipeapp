package com.example.recipeapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cualquier GET a /images/** servirá archivos desde uploads/recipes
        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/recipes/");
    }
}
