package com.ssafy.exam.listener;


import com.ssafy.exam.model.dao.FileMobileDao;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/*
 * MyContextListener 리스너에서는 서버가 시작될 때 DAO를 로딩하고, 종료될 때 저장하도록 구현한다.
 * */
@WebListener
public class MyContextListener implements ServletContextListener {
	
	private FileMobileDao dao = FileMobileDao.getInstance();
	
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("contextInitialized() called");
        //load 호출
        dao.load();
        
        //데이터가 없으면 초기화!! 
        if(dao.selectAll(null).isEmpty()) {
        	dao.reset(null);
        	dao.mobileReset(null);
        	dao.save();
        }
    }


    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("contextDestroyed() called");
        //save 호출
        dao.save();
    }

}




