package com.mpi.astro.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mpi.astro.model.edu.Student;
import com.mpi.astro.service.edu.EduService;

@Controller
@RequestMapping("/")
public class PublicController {
	
	@Autowired
	EduService eduService;
	
	@RequestMapping(method=RequestMethod.GET)
	public String welcome() {
		return "Welcome to Myelin Prime";
	}
	
	// Testing communication between client / server.
	// will use ajax from client, unless client is merged into server.  Maybe both cases.
	@RequestMapping(value="bridge", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public String respondToClient(HttpServletRequest request, HttpServletResponse response) {
		String mediaJSON = "{}";
		String id = request.getParameter("student");
		String course = request.getParameter("course");
		if( !(id == null || course == null || StringUtils.isEmpty(id) || StringUtils.isEmpty(course)) ) {
			Student student = eduService.getStudent(Long.parseLong(id));
			StringBuilder sb = new StringBuilder();
			sb.append("{")
			.append("media: {")
			.append("\"TODO: IMPLEMENT\"")
			.append("}")
			.append("}");
		}
		
		return mediaJSON;
	}
	
}