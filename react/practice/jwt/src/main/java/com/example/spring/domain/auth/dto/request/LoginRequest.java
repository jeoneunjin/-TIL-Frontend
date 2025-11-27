package com.example.spring.domain.auth.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequest {
	private String email;
	private String password;
}
