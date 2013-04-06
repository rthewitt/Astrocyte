package com.myelin.client.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jgit.util.StringUtils;

public class SessionUtil {
	
	public static boolean ensureSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		
		if(session.getAttribute("userId") == null || 
				StringUtils.isEmptyOrNull(session.getAttribute("userId").toString()) ||
				session.getAttribute("courseName") == null ||
				StringUtils.isEmptyOrNull(session.getAttribute("courseName").toString()) ) {
			
			// This allows a test box to exists wherein I can still log in to mock users.
			if(StringUtils.isEmptyOrNull(GlialProps.STUDENT_ID) 
					|| StringUtils.isEmptyOrNull(GlialProps.COURSE_UUID)) {
				showLoginForm("Please Login:", response.getWriter());
				return false;
			} else {
				session.setAttribute("userId", GlialProps.STUDENT_ID);
				session.setAttribute("courseName", GlialProps.COURSE_UUID);
			}
		}
		
		return true;
	}
	
	public static void showLoginForm(String message, PrintWriter wrt) {
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