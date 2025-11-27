package com.example.spring.domain.user.entity;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role {
	private String uuid;
	private String name;
	private Integer level;
	private String description;
}
