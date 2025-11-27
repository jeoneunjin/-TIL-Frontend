package com.example.spring.common.exception;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<?> handleCustomException(CustomException e){
		ErrorResponse errorResponse = ErrorResponse.builder()
				.code(e.getErrorCode().getCode())
				.message(e.getErrorCode().getMessage())
				.build();
		
		return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
				
	}
}
