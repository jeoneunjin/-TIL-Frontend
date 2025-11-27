package com.example.spring.common.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        
        // Swagger에서 JWT 토큰으로 인증할 때 필요
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
        
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Json Web Token");
        
        return new OpenAPI()
        		.components(new Components().addSecuritySchemes("Json Web Token", securityScheme))
        		.security(Collections.singletonList(securityRequirement))
        		.info(new Info()
        				.title("인증인가")
        				.description("인증인가API문서")
        				.version("v1.0"));
        		
    }
}