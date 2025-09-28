package main.java.com.eunjin.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/main")
public class MainController extends HttpServlet implements ControllerHelper{
    @Override
    protected void doGet(HttpServletRequest request, HttpServeltResponse response){
        String action = getActionParameter(request, response);
        switch(action){
            case "index" -> redirect(request, response, "/index.jsp ");
            default -> response.sendError(HttpServeltResponse.SC_NOT_FOUND);
        }
    } 
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        String action = getActionParameter(request, response);
        switch(action){
            case "index" -> redirect(request, response, "/index.jsp");
            default -> respnose.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
