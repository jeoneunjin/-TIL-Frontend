package com.ssafy.test.model.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.test.model.dao.ContentDao;
import com.ssafy.test.model.dao.ContentDaoImpl;
import com.ssafy.test.model.dto.Content;
import com.ssafy.test.model.dto.Genre;
import com.ssafy.util.DBUtil;

@Service
public class ContentServiceImpl implements ContentService {
	private DBUtil dbUtil;
	private ContentDao contentDao;

	private ContentServiceImpl(DBUtil dbUtil, ContentDao contentDao) {
		this.dbUtil = dbUtil;
		this.contentDao = contentDao;
	}


	@Override
	public List<Content> selectAll() throws SQLException {
		try (Connection conn = dbUtil.getConnection()) {
			return contentDao.selectAll(conn);
		}
	}

	@Override
	public Content selectByCode(String code) throws SQLException {
		try (Connection conn = dbUtil.getConnection()) {
			return contentDao.selectByCode(conn, code);
		}
	}

	@Override
	public int insert(Content content) throws SQLException {
		try (Connection conn = dbUtil.getConnection()) {
			return contentDao.insert(conn, content);
		}
	}

	@Override
	public int update(Content content) throws SQLException {
		try (Connection conn = dbUtil.getConnection()) {
			return contentDao.update(conn, content);
		}
	}

	@Override
	public int delete(String code) throws SQLException {
		try (Connection conn = dbUtil.getConnection()) {
			return contentDao.delete(conn, code);
		}
	}

	@Override
	public List<Genre> selectAllGenres() throws SQLException {
		try (Connection conn = dbUtil.getConnection()) {
			return contentDao.selectAllGenres(conn);
		}
	}

}
