package main.java.com.eunjin.controller;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ControllerHelper {
    //action 파라미터를 추출하여 반환
    default String getActionParameter(HttpServletRequest request, HttpServletResponse response){
        //get Parameter 사용해서 추출
        String action = request.getParameter("action");
        if(action == null || action.isBlank()){
            action = "index";
        }
        System.out.println("action : " + action);
        return action;
    } 

    //redirect
    public default void redirect(HttpServletRequest request, HttpServletResponse response, String path){
        response.sendRedirect(requset.getContextPath() + path);
    }

    //forward
    public default void forward(HttpServletReqeust request, HttpServletResponse response, Stiring path){
        RequestDispatcher disp = request.getReqestDispatcher(path);
        disp.forward(request, response);
    }

    //cookie 관리
    public default void setupCookie(String name, String value, int maxAge, String path,
        HttpServletResponse response){
            Cookie cookie = new Cookie(name, value);
            cookie.setMaxAge(maxAge);
            if(path != null) {
                cookie.setPath(path);
            }
            reponse.addCookie(cookie);
    }

    public default void toJSON(Object target, HttpServletResponse response) throws ServletException, IOException{
      String json = new ObjectMapper().writeValueAsString(target);
      response.setContentType("application.json;charset=UTF-8");
      response.getWriter().write(json);  
    } 
}

