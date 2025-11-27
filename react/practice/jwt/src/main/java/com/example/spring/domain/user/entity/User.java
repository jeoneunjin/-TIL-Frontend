package com.example.spring.domain.user.entity;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class User {
	private String uuid;
	private String email;
	private String password;
	private String name;
	private Role role;

}
