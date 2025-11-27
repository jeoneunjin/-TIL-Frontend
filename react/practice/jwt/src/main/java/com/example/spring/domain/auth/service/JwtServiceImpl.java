package com.example.spring.domain.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService{
	
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public boolean isBlackListed(String token) {
		// TODO Auto-generated method stub
		return redisTemplate.hasKey(token);
	}

}
