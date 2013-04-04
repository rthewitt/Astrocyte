package com.mpi.astro.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

//@Controller
//@RequestMapping("/delegate/bridge")
public class PublicController extends AbstractController {
	
//	@Autowired
//	EduService eduService;
	/*
	@RequestMapping(method=RequestMethod.GET, value="hello")
	public String welcome() {
		return "Service working correctly";
	} */

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("public/hello");
	}
	
//	@RequestMapping(method=RequestMethod.GET, value="/bridge/fallback")
//	public String fallback() {
//		return "Fallback working correctly";
//	}
//	
//	@RequestMapping(method=RequestMethod.GET, value="/delegate/bridge/resort")
//	public String lastResort() {
//		return "lastResort working correctly";
//	}
	
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