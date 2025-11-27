package com.example.spring.common.util.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix="jwt")
public class JwtProperties {
    private String privateKeyPath;
    private String publicKeyPath;
    private Long accessTokenExpiry;
    private Long refreshTokenExpiry;
    private String issuer;
}