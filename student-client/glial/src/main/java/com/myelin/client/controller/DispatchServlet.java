package com.myelin.client.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jgit.util.StringUtils;

public class DispatchServlet extends HttpServlet {
	
	private static final long serialVersionUID = 7568007455366384415L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		if(session.getAttribute("userId") == null || 
				StringUtils.isEmptyOrNull(session.getAttribute("userId").toString()) ||
				session.getAttribute("courseName") == null ||
				StringUtils.isEmptyOrNull(session.getAttribute("courseName").toString()) ) {
			showLoginForm("Please Login:", response.getWriter());
			return;
		} else
			System.out.println("Attempting forward to main.jsp");
			request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
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
			showLoginForm("Error!", response.getWriter());
	}
	
	private void showLoginForm(String message, PrintWriter wrt) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>\n")
		.append("<head><title>Login</title><head>\n")
		.append("<body>\n")
		.append("<span class='message'>" + message + "</span><br />\n")
		.append("<form method='POST' action='login'>\n")
		.append("<label for='userId'>User Id: </label>")
		.append("<input id='user-id' name='userId' /><br />\n")
		.append("<label for='courseName'>Course: </label>")
		.append("<input id='course-name' name='courseName' /><br />\n")
		.append("<input type='submit' value='Login' />\n")
		.append("</form>\n")
		.append("</html>\n");
		
		wrt.println(sb.toString());
	}
	
}