package com.ssafy.test.model.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.ssafy.test.model.dao.UserDao;
import com.ssafy.test.model.dao.UserDaoImpl;
import com.ssafy.test.model.dto.User;
import com.ssafy.util.DBUtil;

@Service
public class UserServiceImpl implements UserService {
	private DBUtil dbUtil;
	private UserDao userDao;

	private UserServiceImpl() {

	}


	@Override
	public User login(User user) throws SQLException {
		try (Connection conn = dbUtil.getConnection()) {
			return userDao.login(conn, user);
		}
	}

	@Override
	public int register(User user) throws SQLException {
		try (Connection conn = dbUtil.getConnection()) {
			return userDao.register(conn, user);
		}
	}

	@Override
	public User detail(String id) throws SQLException {
		try (Connection conn = dbUtil.getConnection()) {
			return userDao.detail(conn, id);
		}
	}

	@Override
	public int update(User user) throws SQLException {
		try (Connection conn = dbUtil.getConnection()) {
			return userDao.update(conn, user);
		}
	}

}
