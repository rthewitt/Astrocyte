package com.myelin.client.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jgit.util.StringUtils;

import com.myelin.client.util.SessionUtil;

public class DispatchServlet extends HttpServlet {
	
	private static final long serialVersionUID = 7568007455366384415L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		if(!SessionUtil.ensureSession(request, response)) return;
		
		String need = request.getParameter("return");
		String student = "";
		String course = "";
		if( need != null) {
			if("student-info".equals(need)) {
				student = request.getSession().getAttribute("userId").toString();
				course = request.getSession().getAttribute("courseName").toString();
			}
			response.setStatus(HttpServletResponse.SC_OK);
			response.setHeader("Content-Type", "application/json");
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.getOutputStream().write(
					String.format("{\"student\":\"%s\",\"course\":\"%s\"}", student, course).getBytes()
					);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		System.out.println("POST as: " + request.getMethod() + " " + request.getRequestURI());
		// currently not checking passwords, etc
		if(!StringUtils.isEmptyOrNull(request.getParameter("userId")) &&
				!StringUtils.isEmptyOrNull(request.getParameter("courseName")) ) {
			request.getSession().setAttribute("userId", request.getParameter("userId"));
			request.getSession().setAttribute("courseName", request.getParameter("courseName"));
			doGet(request, response);
		} else
			SessionUtil.showLoginForm("Error!", response.getWriter());
	}
	
	
	
}