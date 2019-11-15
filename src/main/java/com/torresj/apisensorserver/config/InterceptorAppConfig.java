package com.torresj.apisensorserver.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class InterceptorAppConfig implements WebMvcConfigurer {

    private LogHandlerInterceptor logHandlerInterceptor;

    public InterceptorAppConfig(
            LogHandlerInterceptor logHandlerInterceptor) {
        this.logHandlerInterceptor = logHandlerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logHandlerInterceptor);
    }

}