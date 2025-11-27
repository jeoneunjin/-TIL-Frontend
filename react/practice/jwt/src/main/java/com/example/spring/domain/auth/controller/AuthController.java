package com.example.spring.domain.auth.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.common.documentation.AuthDocumentation;
import com.example.spring.common.util.jwt.JwtProperties;
import com.example.spring.domain.auth.dto.request.LoginRequest;
import com.example.spring.domain.auth.dto.response.TokenResponse;
import com.example.spring.domain.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthDocumentation{
	
	private final AuthService authService;
	private final JwtProperties jwtProperties;
	
	// 이거 스프링 프레임워크꺼
	@Value("${app.cookie.domain}")
	private String cookieDomain;
	
	@Value("${app.cookie.secure}")
	private Boolean cookieSecure;
	
	@Override
	@PostMapping("/sessions")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
		
		
		TokenResponse tokenResponse = authService.login(loginRequest);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken());
		
		String cookie = ResponseCookie.from("refresh_token", tokenResponse.getRefreshToken())
				.domain(cookieDomain) // 배포 위치의 IP
				.path("/")
				.maxAge(Duration.ofMillis(jwtProperties.getRefreshTokenExpiry()))
				.httpOnly(false)
				.secure(cookieSecure)
				.sameSite("Lax")
				.build()
				.toString();
		
		headers.add(HttpHeaders.SET_COOKIE, cookie);
		
		return ResponseEntity.ok()
				.headers(headers)
				.build(); 
	}
	
	@DeleteMapping("/sessions")
	public ResponseEntity<?> logout(
			@RequestHeader("Authorization") String authorizationHeader,
			@CookieValue("refresh_token") String refreshToken
	){
		
		authService.logout(authorizationHeader, refreshToken);
		
		//쿠키삭제
		HttpHeaders headers = new HttpHeaders();
		
		String cookie = ResponseCookie.from("refresh_token", "")
				.domain(cookieDomain) // 배포 위치의 IP
				.path("/")
				.maxAge(0)
				.httpOnly(false)
				.secure(cookieSecure)
				.sameSite("Lax")
				.build()
				.toString();
		
		headers.add(HttpHeaders.SET_COOKIE, cookie);
		
		return ResponseEntity.ok()
				.headers(headers)
				.build(); 
		
	}
	
	@GetMapping("/refresh-token")
	public ResponseEntity<?> createNewAccessToken(
			@CookieValue("refresh_token") String refreshToken
			){
		
		TokenResponse tokenResponse = authService.createNewAccessToken(refreshToken);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken());

//		
//		headers.add(HttpHeaders.SET_COOKIE, cookie);
		
		return ResponseEntity.ok()
				.headers(headers)
				.build(); 
		
	}
	
	
}
