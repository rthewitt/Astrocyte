package com.myelin.client.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.util.StringUtils;

import com.myelin.client.util.GlialProps;
import com.myelin.client.util.SessionUtil;

public class ProjectController extends HttpServlet {
	
	private static final long serialVersionUID = 3642951840339972596L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		SessionUtil.ensureSession(request, response);
		
		// Was having problems with cache before.  
		// Provides a simple check, also cleans maven project
		boolean delete = request.getParameter("delete") != null;
		String action = request.getParameter("action");
		
		if(StringUtils.isEmptyOrNull(action)) {
			response.setStatus(400); // bad request
			return;
		}
		
		String courseName = request.getSession().getAttribute("courseName").toString();
		
		if(delete)
			FileUtils.deleteDirectory(new File(GlialProps.WEB_PROJECT));
		
		File projectRoot = new File(GlialProps.STUDENT_PROJECT+"/"+courseName); // should already exist
		
		if("compile-swap".equals(action)) {
			Process proc = Runtime.getRuntime().exec((delete ? new String[] {"mvn", "-Phtml", "clean", "integration-test"} :
				new String[]{"mvn", "-Phtml", "integration-test"}), null, projectRoot);
			
			// TODO consider getting maven err stream if problem, passing back.
			
			int status;
			try {
				status = proc.waitFor();
			} catch (InterruptedException e) {
				status = -1;
			}
			
			boolean success = status == 0;
			StringBuilder sb = new StringBuilder();
			sb.append("{")
			.append("'status':")
			.append(( success ? "'true'" : "'false'"));
			if(!success) {
				sb.append(",")
				.append("'message':")
				.append((status > 0 ? "'Project compilation failed.'" :"'Compilation unexpectedly interrupted'"));
			}
			sb.append("}");
			
			response.setStatus((success ? 200 : 500));
			response.setContentType("application/json");
			response.getWriter().println(sb.toString());
			return;
		}
	}

}
