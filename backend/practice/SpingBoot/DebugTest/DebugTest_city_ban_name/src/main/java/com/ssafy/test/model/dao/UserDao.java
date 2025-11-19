package com.ssafy.test.model.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.ssafy.test.model.dto.User;

public interface UserDao {

	User login(Connection conn, User user) throws SQLException;
	int register(Connection conn, User user) throws SQLException;
	User detail(Connection conn, String id) throws SQLException;
	int update(Connection conn, User user) throws SQLException;
	
}
