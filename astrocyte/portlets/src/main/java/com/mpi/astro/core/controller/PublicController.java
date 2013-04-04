package com.mpi.astro.core.controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.service.edu.EduService;

@Controller
//@RequestMapping("/delegate/bridge")
public class PublicController {
	
	@Autowired
	EduService eduService;
	
	@RequestMapping(method=RequestMethod.GET, value="/delegate/bridge/hello")
	public String welcome() {
		return "Service working correctly";
	}
	
	/*
	// Testing communication between client / server.
	// will use ajax from client, unless client is merged into server.  Maybe both cases.
	@RequestMapping(value="hello", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public String respondToClient(HttpServletRequest request, HttpServletResponse response) {
		String mediaJSON = "{}";
		String sid = request.getParameter("student");
		String course = request.getParameter("course");
		if( !(sid == null || course == null || StringUtils.isEmpty(sid) || StringUtils.isEmpty(course)) ) {
			Student student = eduService.getStudentEagerBySID(sid);
			StringBuilder sb = new StringBuilder();
			sb.append("{")
			.append("media: {")
			.append("\"name: "+student.getFirstName()+"\"")
			.append("}")
			.append("}");
		}
		
		return mediaJSON;
	} */
	
}