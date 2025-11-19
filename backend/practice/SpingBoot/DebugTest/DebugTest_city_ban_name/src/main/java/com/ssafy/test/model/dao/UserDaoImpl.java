package com.ssafy.test.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.ssafy.test.model.dto.User;
import com.ssafy.util.DBUtil;

@Repository
public class UserDaoImpl implements UserDao {

	private DBUtil dbUtil;

	private UserDaoImpl(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	@Override
	public User login(Connection conn, User user) throws SQLException {
		String sql = "select id, name from users where id=? and pw=?";
		User userInfo = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.getId());
			pstmt.setString(2, user.getPw());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				userInfo = new User(id, name);

			}

		} finally {
			dbUtil.close(rs, pstmt);
		}

		return userInfo;
	}

	@Override
	public int register(Connection conn, User user) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into users(id, pw, name, birthdate, phone, email) \n");
		sql.append("values(?,?,?,?,?,?)\n");
		int cnt = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, user.getId());
			pstmt.setString(2, user.getPw());
			pstmt.setString(3, user.getName());
			pstmt.setString(4, user.getBirthdate());
			pstmt.setString(5, user.getPhone());
			pstmt.setString(6, user.getEmail());

			cnt = pstmt.executeUpdate();

		} finally {
			dbUtil.close(pstmt);
		}
		return cnt;
	}

	@Override
	public User detail(Connection conn, String id) throws SQLException {
		String sql = "select * from users where id = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		User user = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				String pw = rs.getString("pw");
				String name = rs.getString("name");
				String birthdate = rs.getString("birthdate");
				String phone = rs.getString("phone");
				String email = rs.getString("email");
				String regDate = rs.getString("reg_date");
				user = new User(id, pw, name, birthdate, phone, email, regDate);
			}
		} finally {
			dbUtil.close(rs, pstmt);
		}

		return user;
	}

	@Override
	public int update(Connection conn, User user) throws SQLException {
		int cnt = 0;
		int idx = 1;
		StringBuilder sql = new StringBuilder();
		sql.append("update users set ");
		if (user.getPw() != null && user.getPw().length() != 0) {
			sql.append("pw=?, ");
		}
		sql.append("name=?, birthdate=?, phone=?, email=? where id=?");
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql.toString());
			if (user.getPw() != null && user.getPw().length() != 0) {
				pstmt.setString(idx++, user.getPw());
			}

			pstmt.setString(idx++, user.getName());
			pstmt.setString(idx++, user.getBirthdate());
			pstmt.setString(idx++, user.getPhone());
			pstmt.setString(idx++, user.getEmail());
			pstmt.setString(idx++, user.getId());

			cnt = pstmt.executeUpdate();
		} finally {
			dbUtil.close(pstmt);
		}
		return cnt;

	}

}
