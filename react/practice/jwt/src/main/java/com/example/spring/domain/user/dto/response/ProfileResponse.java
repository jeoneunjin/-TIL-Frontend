package com.example.spring.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {
	private String email;
	private String name;
	private String role;
}
