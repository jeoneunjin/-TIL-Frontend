package com.example.spring.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignUpRequest {
	
	private String email;
	private String name;
	private String password;
	

}
