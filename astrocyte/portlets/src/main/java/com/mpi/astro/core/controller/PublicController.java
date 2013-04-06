package com.mpi.astro.core.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.service.edu.EduService;
import com.mpi.astro.core.util.AstrocyteConstants.STUDENT_STATE;

@Controller
public class PublicController extends AbstractController {
	
	@Autowired
	EduService eduService;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// see if I can just place a filter somewhere for this, or a spring interceptor
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		
		
		String studentId = request.getParameter("student");
		
		if(studentId == null || StringUtils.isEmpty(studentId)) return new ModelAndView("edu/failure"); // TODO error
		
		Student student = eduService.getStudentEagerBySID(studentId);
		
		if(request.getParameter("notified") != null) {
			
			if(student.getState() == STUDENT_STATE.NOTIFY_STUDENT)
				student.setState(STUDENT_STATE.WORKING);
			
		} else {
			
			ObjectMapper mapper = new ObjectMapper();
			JSONObject obj = new JSONObject();
			obj.put("lessonAvailable", (Boolean)(student.getState() == STUDENT_STATE.NOTIFY_STUDENT));
			String jsonString = mapper.writeValueAsString(obj);
			
			MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
			MediaType jsonMimeType = MediaType.APPLICATION_JSON;

			if(jsonConverter.canWrite(String.class, jsonMimeType)) {
				// Should allow ajax, I may even be able to dynamically specify the host based on lookup
				
				try {
		            jsonConverter.write(obj, jsonMimeType, new ServletServerHttpResponse(response));
		        } catch (HttpMessageNotWritableException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			} else return new ModelAndView("edu/failure"); // TODO error
		}
		response.setStatus(HttpServletResponse.SC_OK);
        return null;
	}
	
}