package com.ssafy.test.model.service;

import java.sql.SQLException;
import java.util.List;

import com.ssafy.test.model.dto.Content;
import com.ssafy.test.model.dto.Genre;

public interface ContentService {

	List<Content> selectAll() throws SQLException;
	Content selectByCode(String code) throws SQLException;
	int insert(Content content) throws SQLException;
	int update(Content content) throws SQLException;
	int delete(String code) throws SQLException;
	List<Genre> selectAllGenres() throws SQLException;
}
