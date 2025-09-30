package com.ssafy.exam.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.ssafy.exam.model.dto.Mobile;
import com.ssafy.exam.model.service.BasicMobileService;
import com.ssafy.exam.model.service.MobileService;

import java.util.*;

/**
 * Servlet implementation class MobileController
 */
@WebServlet("/mobile")
public class MobileController extends HttpServlet implements ControllerHelper {
	private static final long serialVersionUID = 1L;
	
	private final MobileService mService = BasicMobileService.getService();
      
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = getActionParameter(request, response);
		switch(action) {
		case "index" -> redirect(request, response, "/");
		case "mobile-regist-form" -> forward(request, response, "/mobile/regist.jsp");
		default -> response.sendError(HttpServletResponse.SC_NOT_FOUND); //404처리
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = getActionParameter(request, response);
		switch(action) {
		case "index" -> redirect(request, response, "/");
		default -> response.sendError(HttpServletResponse.SC_NOT_FOUND); //404처리
		}
	}
	

	
	private void template(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}
