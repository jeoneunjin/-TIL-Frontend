package com.example.spring.domain.user.service;

import com.example.spring.domain.user.dto.request.SignUpRequest;
import com.example.spring.domain.user.dto.response.ProfileResponse;

public interface UserService {

	void signUp(SignUpRequest signUpRequest);

	ProfileResponse getProfile(String authorizationHeader);

}
