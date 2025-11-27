package com.example.spring.domain.auth.service;

import com.example.spring.domain.auth.dto.request.LoginRequest;
import com.example.spring.domain.auth.dto.response.TokenResponse;

public interface AuthService {

	TokenResponse login(LoginRequest loginRequest);

	void logout(String authorizationHeader, String refreshToken);

	TokenResponse createNewAccessToken(String refreshToken);

}
