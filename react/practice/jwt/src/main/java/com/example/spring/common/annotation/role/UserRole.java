package com.example.spring.common.annotation.role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
	ADMIN("ADMIN", "관리자", 1),
	NORMAL("NORMAL", "일반", 0);
	
	private final String code;
	private final String name;
	private final Integer level;
	
	// 계층적 권한체크
	public boolean hasPermission(UserRole requRole) {
		return this.level >= requRole.level;
	}

}
