package com.example.spring.domain.auth.service;

public interface JwtService {
	boolean isBlackListed(String token);
}
