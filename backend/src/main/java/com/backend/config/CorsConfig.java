package com.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Allows all endpoints
                .allowedOrigins("http://localhost:5173")  // Allow your frontend origin
                .allowedMethods("GET")  // Only allow GET requests
                .allowedHeaders("*");  // Allow all headers
    }
}