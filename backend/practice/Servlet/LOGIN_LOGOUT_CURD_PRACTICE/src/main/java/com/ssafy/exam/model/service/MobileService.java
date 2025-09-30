package com.ssafy.exam.model.service;

import java.util.List;

import com.ssafy.exam.model.dto.Member;
import com.ssafy.exam.model.dto.Mobile;

public interface MobileService {

    Member login(String email, String password);
    
	List<Mobile> selectAll();

	Mobile selectByCode(String code);

	int insert(Mobile mobile);

	int deleteByCode(String code);

	int update(Mobile mobile);

    
}
