package com.ssafy.exam.model.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ssafy.exam.model.dto.Member;
import com.ssafy.exam.model.dto.Mobile;


public interface MobileDao {

    /*사용자 로그인*/
    Member login(Connection con, String email, String password);
    /*등록 된 모든 휴대폰 정보 조회*/
	List<Mobile> selectAll(Connection conn);
	/*휴대폰 정보 상세 조회*/
	Mobile selectByCode(Connection conn, String code);
	/*휴대폰 정보 등록*/
	int insert(Connection conn, Mobile mobile);
	/*휴대폰 정보 삭제*/
	int deleteByCode(Connection conn, String code);
	/*휴대폰 정보 수정*/
	int update(Connection conn, Mobile mobile);


}
