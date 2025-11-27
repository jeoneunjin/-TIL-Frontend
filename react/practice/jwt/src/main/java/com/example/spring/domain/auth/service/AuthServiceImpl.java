package com.example.spring.domain.auth.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.spring.common.exception.CustomException;
import com.example.spring.common.exception.ErrorCode;
import com.example.spring.common.util.jwt.JwtProperties;
import com.example.spring.common.util.jwt.JwtTokenProvider;
import com.example.spring.domain.auth.dto.request.LoginRequest;
import com.example.spring.domain.auth.dto.response.TokenResponse;
import com.example.spring.domain.user.entity.User;
import com.example.spring.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
	
	private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public TokenResponse login(LoginRequest loginRequest) {
        // 패스워드 해시 추가
		// 이메일 기반으로 USer를 조회
		// 그 User가 가지고 있는 비밀번호와 로그인 요청의 비밀번호를 bCryptPasswordEncoder.matches로 비교
        
        return userRepository.findByEmailAndPassword(loginRequest)
                .map(user -> TokenResponse.builder()
                        .accessToken(jwtTokenProvider.createAccessToken(user))
                        .refreshToken(jwtTokenProvider.createRefreshToken(user))
                        .accessTokenExpiresIn(jwtProperties.getAccessTokenExpiry())
                        .refreshTokenExpiresIn(jwtProperties.getRefreshTokenExpiry())
                        .build())
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
	}

	@Override
	public void logout(String authorizationHeader, String refreshToken) {
		
		// authorizationHeader, refreshToken이 빈 값으로 오는 경우도 존재
		// 예외 처리는 필수
		// 나중에 jwtService로 옮길꺼
		String accessToken = authorizationHeader.substring(7);
		
		redisTemplate.opsForValue().set(accessToken, "BlackList");
		redisTemplate.opsForValue().set(refreshToken, "BlackList");
		
		// 정확하게 하려면, 토큰에서 만료시간을 추출하고, 현재 시간과 만료 시간의 차이를 구해서, 그 값을 redis만료 시간으로 사용
		// -> JWT와 Redis에 등록된 만료시간이 맞춰짐.
		redisTemplate.expire(accessToken, jwtProperties.getAccessTokenExpiry(), TimeUnit.MILLISECONDS);
		redisTemplate.expire(refreshToken, jwtProperties.getRefreshTokenExpiry(), TimeUnit.MILLISECONDS);
		
		
	}

	@Override
	public TokenResponse createNewAccessToken(String refreshToken) {
		System.out.println(refreshToken);
		// TODO Auto-generated method stub
		return Optional.ofNullable(refreshToken)
				.filter(token -> !token.trim().isEmpty())
				.filter(token -> !jwtService.isBlackListed(token))// jwtService -> 블랙리스트에 등록된 토큰인지 확인
				.map(token -> jwtTokenProvider.extractEmail(refreshToken, false))
				.flatMap(userRepository::findByEmail)
				 .map(user -> TokenResponse.builder()
	                        .accessToken(jwtTokenProvider.createAccessToken(user))
	                        .accessTokenExpiresIn(jwtProperties.getAccessTokenExpiry())
	                        .build())
				 .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
				 
				
	}

}
