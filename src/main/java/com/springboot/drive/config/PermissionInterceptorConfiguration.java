package com.springboot.drive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Bean
    PermissionInterceptor getPermissionInterceptor(){
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whitelist = {
                "/api/v1/auth/**",
                "/",
                "/storage/**"
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(
                        whitelist
                );
    }
}
