package com.example.spring.common.interceptor;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.spring.common.annotation.RequireRole;
import com.example.spring.common.annotation.role.UserRole;
import com.example.spring.common.exception.CustomException;
import com.example.spring.common.exception.ErrorCode;
import com.example.spring.common.util.jwt.JwtTokenProvider;

import io.jsonwebtoken.lang.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleInterceptor implements HandlerInterceptor{
	
	private final JwtTokenProvider jwtTokenProvider;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(!(handler instanceof HandlerMethod)) {
			return true;
		}
		
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		
		RequireRole requireRole = getRequireRoleAnnotation(handlerMethod);
		
		//메서드나 클래스에 @RequireRole이 안붙어있으면 권한 검증 대상이 아님
		if(requireRole == null) {
			return true;
		}
		
		// 권한 검증 대상인 경우
		// JWT에 Role 정보를 넣어뒀으니까, 여기서 Role을 추출하고, 권한 검증.
		
		try {
			String userRole = extractUserRole(request);
			
			if(hasRequirePermission(userRole, requireRole)) {
				return true;
			}
			
			response .setStatus(HttpServletResponse.SC_FORBIDDEN);
			return false;
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean hasRequirePermission(String userRole, RequireRole requireRole) {
		List<UserRole> requireRoles = Arrays.asList(requireRole.value());
		
		UserRole userRoleEnum = UserRole.valueOf(userRole);
		
		return checkPermission(userRoleEnum, requireRoles);
	}

	private boolean checkPermission(UserRole userRoleEnum, List<UserRole> requireRoles) {
		for(UserRole requireRole : requireRoles) {
			if(requireRole.hasPermission(requireRole)) {
				return true;
			}
		}
		
		return false;
	}

	// 메서드나 클래스에 @RequireRole이 붙어있는지를 확인하는 메서드
	private RequireRole getRequireRoleAnnotation(HandlerMethod handlerMethod) {
		// 메서드 레벨에서 확인
		RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
		
		// 클래스 레벨에서 학인
		if(requireRole == null) {
			requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
		}
		
		return requireRole;
	}
	
	private String extractUserRole(HttpServletRequest httpServletRequest) {
		String authorizationHeader = httpServletRequest.getHeader("Authorization");
		
		if(authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}
		
		if(!authorizationHeader.startsWith("Bearer")) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
		
		return jwtTokenProvider.extractRole(authorizationHeader, true);
		
		// Role이 null인지 같은 것도 확인은 해야함
	}
	
}
