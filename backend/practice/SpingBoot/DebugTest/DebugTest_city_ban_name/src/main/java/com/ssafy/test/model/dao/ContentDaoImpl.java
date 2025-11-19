package com.ssafy.test.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ssafy.test.model.dto.Content;
import com.ssafy.test.model.dto.Genre;
import com.ssafy.util.DBUtil;

@Repository
public class ContentDaoImpl implements ContentDao {

	private DBUtil dbUtil;

	private ContentDaoImpl(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	@Override
	public List<Content> selectAll(Connection conn) throws SQLException {
		List<Content> contentList = new ArrayList<>();
		String sql = "SELECT code, title, artist FROM contents";
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery();) {

			while (rs.next()) {
				String code = rs.getString("code");
				String title = rs.getString("title");
				String artist = rs.getString("artist");

				Content content = new Content();
				content.setCode(code);
				content.setTitle(title);
				content.setArtist(artist);

				contentList.add(content);

			}
		}

		return contentList;
	}

	@Override
	public Content selectByCode(Connection conn, String code) throws SQLException {
		String sql = "SELECT c.code, c.title, g.name, c.album, c.artist, c.user_id FROM contents c JOIN genres g ON c.genre = g.genre_id WHERE c.code = ?";
		Content content = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();

			if (rs!=null) {
				content = new Content();
				content.setCode(rs.getString("code"));
				content.setTitle(rs.getString("title"));
				content.setGenreName(rs.getString("name"));
				content.setAlbum(rs.getString("album"));
				content.setArtist(rs.getString("artist"));
				content.setUserId(rs.getString("user_id"));
			}
		} finally {
			dbUtil.close(rs, pstmt);
		}
		return content;
	}

	@Override
	public int insert(Connection conn, Content content) throws SQLException {
		String sql = "insert into contents (code, title, genre, album, artist, user_id) values (?, ?, ?, ?, ?, ?)";
		int cnt = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, content.getCode());
			pstmt.setString(2, content.getTitle());
			pstmt.setInt(3, content.getGenre());
			pstmt.setString(4, content.getAlbum());
			pstmt.setString(5, content.getArtist());
			pstmt.setString(6, content.getUserId());
			cnt = pstmt.executeUpdate();
		} finally {
			dbUtil.close(pstmt);
		}
		return cnt;
	}

	@Override
	public int update(Connection conn, Content content) throws SQLException {
		String sql = "update contents title=?, genre=?,album=?, artist=? where code=?";
		int cnt = 0;
		try (PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, content.getTitle());
			pstmt.setInt(2, content.getGenre());
			pstmt.setString(3, content.getAlbum());
			pstmt.setString(4, content.getArtist());
			pstmt.setString(5, content.getCode());

			cnt = pstmt.executeUpdate();
		}
		return cnt;
	}

	@Override
	public int delete(Connection conn, String code) throws SQLException {
		String sql = "delete from contents where code=?";
		int cnt = 0;
		try (PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, code);

			pstmt.executeUpdate();
		}

		return cnt;

	}

	public List<Genre> selectAllGenres(Connection conn) throws SQLException {
		List<Genre> genreList = new ArrayList<>();
		String sql = "SELECT genre_id, name FROM genres";

		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				Genre genre = new Genre();
				genre.setGenreId(rs.getInt("genre_id"));
				genre.setName(rs.getString("name"));
				genreList.add(genre);
			}
		}

		return genreList;
	}

}
