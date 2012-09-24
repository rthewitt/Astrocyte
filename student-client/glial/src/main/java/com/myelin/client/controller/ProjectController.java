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

public class ProjectController extends HttpServlet {

	private static final long serialVersionUID = -170992583878685792L;
	
	static String PROJECT_BASE=null;
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		PROJECT_BASE = getServletContext().getRealPath("/");
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String name = request.getParameter("project");
		// Was having problems with cache before.  
		// Provides a simple check, also cleans maven project
		boolean delete = request.getParameter("delete") != null;
		String action = request.getParameter("action");
		
		if(StringUtils.isEmptyOrNull(name) || StringUtils.isEmptyOrNull(action)) {
			response.setStatus(400); // bad request
			return;
		}
		
		String courseName = request.getSession().getAttribute("courseName").toString();
		
		if(delete) {
			File assetDir = new File(PROJECT_BASE+"/"+name);
			FileUtils.deleteDirectory(assetDir);
		}
		
		File projectRoot = new File(PROJECT_BASE+"/"+courseName); // should already exist
		
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
			return; // flush is handled by parent I believe.
		}
	}

}
