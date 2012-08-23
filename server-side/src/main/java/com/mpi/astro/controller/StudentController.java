package com.mpi.astro.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mpi.astro.dao.StudentDao;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.service.edu.StudentService;

@Controller
@RequestMapping("/student/")
public class StudentController {
	
	// TODO Use studentService as an intermediary to dao
	
	private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

	// TODO Use the service only from within the controller.  May / will the way I'm retrieving information
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private StudentService studentService;
	
	@RequestMapping(method=RequestMethod.GET,value="edit")
	public ModelAndView editStudent(@RequestParam(value="id",required=false) Long id) {		
		logger.debug("Received request to edit student id : "+id);				
		ModelAndView mav = new ModelAndView();		
 		mav.setViewName("edu/edit-student");
 		Student student = null;
 		if (id == null) {
 			student = new Student();
 		} else {
 			student = studentDao.find(id);
 		}
 		
 		mav.addObject("student", student);
		return mav;
		
	}
	
	@RequestMapping(value = "generate-course", method=RequestMethod.POST)
	public String testValues(@RequestParam("course-name") String courseName, HttpServletRequest request, HttpServletResponse response) throws IOException{
		
//		String courseName = request.getParameter("courseName"); // changed to camal, didn't work
		
		// TODO rely on exceptions, perhaps, instead of nested boolean returns? #future
		if(courseName != null) {
			
			Boolean b = studentService.writeStudentsToFile(courseName);
			try {
				
				if(!b) response.getWriter().print("Was unable to persist to file");
//				else if(!studentService.initializeCourse(courseName, response))
//						response.getWriter().print("Failed to initialize course " + courseName);
				else return "redirect:http://" + request.getServerName() + "/cgi-bin/test.py";
				
			} catch(IOException ioe) {
				logger.error("There's no hope at this point...", ioe);
			}	
		} else response.getWriter().print("Course name not found...");
		
		return "redirect:http://www.google.com"; // for fun

//		ModelAndView mav = new ModelAndView("edu/failure");
//		
//		return mav.addObject("courseName", (courseName!=null ? courseName : "NOT-PROVIDED"));	
	}
	
	// Below, the redirect:view convention may help me to avoid
	// the double catch of jsp views in L-Dopa
	@RequestMapping(method=RequestMethod.POST,value="edit") 
	public String saveStudent(@ModelAttribute Student student) {
		logger.debug("Received postback on student "+student);		
		studentDao.save(student);
		return "redirect:list";
		
	}
	
	@RequestMapping(method=RequestMethod.GET,value="list")
	public ModelAndView listStudents() {
		logger.debug("Received request to list students");
		ModelAndView mav = new ModelAndView();
		List<Student> students = studentDao.getStudents();
		logger.debug("Student Listing count = "+students.size());
		mav.addObject("students",students);
		mav.setViewName("edu/list-students");
		return mav;
		
	}

}
