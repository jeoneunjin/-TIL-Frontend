package com.example.spring.common.exception;

import java.net.http.HttpRequest;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-001", "인증에 실패했습니다."), 
	DUPLICATED_EMAIL(HttpStatus.CONFLICT, "USER-001", "중복된 이메일입니다."), 
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-001", "잘못된 요청입니다.");
	
	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
	
	public static final String UNAUTHORIZED_EXAMPLE = "{\"code\":\"AUTH-001\",\"message\":\"인증실패.\"}";
	
}
