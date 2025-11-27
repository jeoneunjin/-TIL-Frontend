package com.example.spring.domain.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.common.annotation.RequireRole;
import com.example.spring.common.annotation.role.UserRole;

@RestController
@RequestMapping("/admin")
@RequireRole({UserRole.ADMIN})	// 이 role이 아니면 접근권한 없음 -> 커스텀 어노테이션
public class AdminController {
	
	@GetMapping("/test")
	public ResponseEntity<?> test(){
		return ResponseEntity.ok("관리자만 보세요.");
	}

}
