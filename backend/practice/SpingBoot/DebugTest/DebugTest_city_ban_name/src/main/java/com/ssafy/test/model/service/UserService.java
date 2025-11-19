package com.ssafy.test.model.service;

import java.sql.SQLException;

import com.ssafy.test.model.dto.User;

public interface UserService {

	User login(User user) throws SQLException;
	int register(User user) throws SQLException;
	User detail(String id) throws SQLException;
	int update(User user) throws SQLException;
	
}
