package com.ssafy.test.model.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ssafy.test.model.dto.Content;
import com.ssafy.test.model.dto.Genre;

public interface ContentDao {

	
	List<Content> selectAll(Connection conn) throws SQLException;
	Content selectByCode(Connection conn, String code) throws SQLException;
	int insert(Connection conn, Content content) throws SQLException;
	int update(Connection conn, Content content) throws SQLException;
	int delete(Connection conn, String code) throws SQLException;
	List<Genre> selectAllGenres(Connection conn) throws SQLException;

}
