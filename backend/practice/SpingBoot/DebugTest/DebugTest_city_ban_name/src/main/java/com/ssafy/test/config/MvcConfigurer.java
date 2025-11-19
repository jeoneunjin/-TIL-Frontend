package com.ssafy.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ssafy.test.interceptor.SessionInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MvcConfigurer implements WebMvcConfigurer {

	private final SessionInterceptor si;
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(si).addPathPatterns("/user/**").excludePathPatterns("/user/login");
        registry.addInterceptor(si).addPathPatterns("/content/**").excludePathPatterns("/content/list");
    }
}
