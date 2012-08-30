package com.mpi.astro.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.Tutorial;
import com.mpi.astro.service.edu.EduService;

// TODO important - this is really the courseName, change logic, add convenience method to service

@Controller
@RequestMapping("/course/{courseId}/")
public class CourseController {
	
	// TODO verify using service that course has been "deployed", not just initialized, otherwise redirect 
	
	@Autowired
	private EduService eduService;
	
	// testing default landing page
	@RequestMapping(method=RequestMethod.GET)
	public void coursePage(@PathVariable String courseId, HttpServletResponse response) 
	throws IOException {
		// TODO use simple template
		String welcome = String.format("Welcome to <span style=\"text-decoration:bold;\">%s</span>, where everything's made up" +
				"<br />and the points don't matter!", courseId);
		
		String temp = String.format("<html><head><title>%s</title></head><body><h2>%s</h2></body></html>",
				courseId, welcome);
		response.getWriter().print(temp);
	}
	
	@RequestMapping(value="control", method=RequestMethod.GET)
	@ResponseBody
	public String controlPanel(@PathVariable String courseId) {
		return String.format("<a href=\"/astrocyte/course/%s/update?ref=XXX\">Will update here</a>", courseId);
	}
	
	@RequestMapping(value="update", method=RequestMethod.GET)
	@ResponseBody
	public String update(@PathVariable String courseId, HttpServletRequest request) {
		
//		long id = Long.parseLong(courseId); // name, not id
//		Course course = eduService.getCourse(id);
		String ref = request.getParameter("ref");
		
		
		// hardcoding for now, move all of this into service layer
		// more importantly, make tutorial a property, along with state
		
		eduService.deployLesson(1L, 1L, ref);
		
		return "Commit ref: " + ref;
	}
	
	
}