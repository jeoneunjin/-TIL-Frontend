package com.example.spring.domain.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.spring.common.exception.CustomException;
import com.example.spring.common.exception.ErrorCode;
import com.example.spring.common.util.jwt.JwtTokenProvider;
import com.example.spring.domain.user.dto.request.SignUpRequest;
import com.example.spring.domain.user.dto.response.ProfileResponse;
import com.example.spring.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final JwtTokenProvider jwtTokenProvider;
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	private final UserRepository userRepository;

	@Override
	public void signUp(SignUpRequest signUpRequest) {
		
		// 해당 이메일을 가진  유저가 있는지 확인
		userRepository.findByEmail(signUpRequest.getEmail())
			.ifPresent(existingUser -> {
				throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
			});
		
		
		// 해당 이메일을 가진 유저가 없다면, 비밀번호를 해시화
		signUpRequest.setPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()));
		
		
		// 그 유저를 저장.
		userRepository.save(signUpRequest);
		
	}

	@Override
	public ProfileResponse getProfile(String authorizationHeader) {
		
		if(authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}
		return userRepository.findByEmail(jwtTokenProvider.extractEmail(authorizationHeader, true))
				.map(user -> ProfileResponse.builder()
						.email(user.getEmail())
						.name(user.getName())
						.role(user.getRole().getName())
						.build()
						)
				.orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
	}

}
