package com.example.spring.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.domain.user.dto.request.SignUpRequest;
import com.example.spring.domain.user.dto.response.ProfileResponse;
import com.example.spring.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(SignUpRequest signUpRequest){
		
		userService.signUp(signUpRequest);
		
		return ResponseEntity.ok().build();
	}
	
	
	@GetMapping("/profile")
	public ResponseEntity<?> getProfile(
			@RequestHeader("Authorization") String authorizationHeader
	){
		ProfileResponse profileResponse = userService.getProfile(authorizationHeader);
		return ResponseEntity.ok(profileResponse);
		
	}
	

}
