package com.ssafy.exam.model.service;

import java.sql.Connection;
import java.util.List;

import com.ssafy.exam.model.dao.FileMobileDao;
import com.ssafy.exam.model.dao.MobileDao;
import com.ssafy.exam.model.dto.Member;
import com.ssafy.exam.model.dto.Mobile;

public class BasicMobileService implements MobileService{
	private MobileDao dao = FileMobileDao.getInstance();

	private static BasicMobileService service = new BasicMobileService();
	
	private BasicMobileService() {}
	
	public static BasicMobileService getService() {
		return service;
	}
		
	@Override
	public Member login(String email, String password) {
		return dao.login(getConnection(), email, password);
	}

	@Override
	public List<Mobile> selectAll() {
		return dao.selectAll(getConnection());
	}

	@Override
	public Mobile selectByCode(String code) {
		return dao.selectByCode(getConnection(), code);
	}

	@Override
	public int insert(Mobile mobile) {
		return dao.insert(getConnection(), mobile);
	}

	@Override
	public int deleteByCode(String code) {
		return dao.deleteByCode(getConnection(), code);
	}

	@Override
	public int update(Mobile mobile) {
		return dao.update(getConnection(), mobile);
	}

	private Connection getConnection() {
		return null;
	}
	

}
