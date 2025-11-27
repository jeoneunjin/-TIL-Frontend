package com.example.spring.common.documentation;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.spring.common.annotation.swagger.ApiRequest;
import com.example.spring.common.exception.ErrorCode;
import com.example.spring.domain.auth.dto.request.LoginRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

public interface AuthDocumentation {

	@Tag(name="인증", description="인증 API")
	@Operation(summary = "로그인", description ="사용자의 이메일과 비밀번호를 이용하여 JWT를 발급받습니다.")
	@ApiRequest(
		content = @Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE,
			schema = @Schema(implementation = LoginRequest.class),
			examples = {
				@ExampleObject(
						name="사용자로그인",
						summary="사용자 로그인 예시",
						value="{\"email\":\"ssafy@ssafy.com\",\"password\":\"ssafy\"}"
				)
			}
		)
	)
	@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200", 
				description = "로그인 성공",
				headers = {
						@Header(
							name="Authrization",
							description="Bearer 토큰",
							schema=@Schema(type="string"),
							required = true,
							example = "Bearer Valid-Access-Token"
						),
						@Header(
								name="Set-Cookie",
								description="Http Only Refresh Token",
								schema=@Schema(type="string"),
								required = true,
								example = "refresh_token=Valid-Refresh-Token; Path/;"
						),
		}),
		@ApiResponse(
				responseCode = "401",
				description="로그인 실패",
				content=@Content(
						mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema=@Schema(type="string"),
						examples = {
								@ExampleObject(
										name="인증 실패",
										summary="이메일 또는 비밀번호 오류",
										value=ErrorCode.UNAUTHORIZED_EXAMPLE
								)						
						}
				)
		)
		
	})
	ResponseEntity<?> login(LoginRequest loginRequest);
	
}
