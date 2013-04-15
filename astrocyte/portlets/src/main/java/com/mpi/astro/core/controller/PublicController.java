package com.mpi.astro.core.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.Lesson;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentStatus;
import com.mpi.astro.core.model.edu.Tutorial;
import com.mpi.astro.core.service.edu.EduService;
import com.mpi.astro.core.util.AstrocyteConstants.STUDENT_STATE;

@Controller
public class PublicController extends AbstractController {
	
	@Autowired
	EduService eduService;
	
	private static final Logger logger = LoggerFactory.getLogger(PublicController.class);

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*"); // Use this as a filter for internal VM driven requests
		
		String studentId = request.getParameter("student");
		String queryType = request.getParameter("return");
		if(StringUtils.isEmpty(studentId) || StringUtils.isEmpty(queryType))
			return badRequest(response);
		
		Student student = eduService.getStudentEagerBySID(studentId);
		
		Object obj = null;
		
		try{
			if("status".equals(queryType)) {
				obj =  handleStatusRequest(request, response, student);
			} else if("lessonMedia".equals(queryType)) {
				obj =  handleMediaRequest(request, response, student);
			} else return badRequest(response);
		} catch(IllegalArgumentException badParams) {
			return badRequest(response);
		}
		
		response.setStatus((obj != null && outputJSON(obj, response) ?
					HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
		return null;
	}
	
	private Object handleStatusRequest(HttpServletRequest request,
			HttpServletResponse response, Student student) throws JsonGenerationException, JsonMappingException, IOException {
		if(request.getParameter("notified") != null) {
			if(student.getState() == STUDENT_STATE.NOTIFY_STUDENT)
				student.setState(STUDENT_STATE.WORKING);
			return " "; // bad habit, may not work anyway
		} else {
			JSONObject obj = new JSONObject();
			obj.put("lessonAvailable", (Boolean)(student.getState() == STUDENT_STATE.NOTIFY_STUDENT));
			return obj;			
		}
	}
	
	
	private Object handleMediaRequest(HttpServletRequest request,
			HttpServletResponse response, Student student) {
		if(StringUtils.isEmpty(request.getParameter("courseId")))
			throw new IllegalArgumentException();
		CourseInstance course = eduService.getDeployedCourse(request.getParameter("courseId"));
		
		StudentStatus stat = eduService.getStudentStatus(student, course);
		Tutorial tut = eduService.getCurrentTutorialForStudent(student, course);
		Lesson lesson = tut.getLessons().get(stat.getLessonNum());
		return lesson.getClientJSON();
	}
	
	private boolean outputJSON(Object obj, HttpServletResponse response) 
			throws JsonGenerationException, JsonMappingException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String jsonString = (obj instanceof String) ? (String)obj : mapper.writeValueAsString(obj);

		MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
		MediaType jsonMimeType = MediaType.APPLICATION_JSON;
		
		if(jsonConverter.canWrite(String.class, jsonMimeType)) {
			try {
				// was obj, leaving jsonString untouched.  Two strategies mixed or problem with SimpleJSON?
	            jsonConverter.write(jsonString, jsonMimeType, new ServletServerHttpResponse(response));
	            return true;
	        } catch (HttpMessageNotWritableException e) {
	        	logger.error("Can't write message in PublicController", e);
	            return false;
	        } catch (IOException e) {
	        	logger.error("Can't write to response in PublicController", e);
	            return false;
	        }
		} else {
			logger.error("jsonConverter canot write JSON in PublicController");
			return false;
		}
	}
	
	private ModelAndView badRequest(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return null;
	}
	
}