package com.mpi.astro.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import com.mpi.astro.dao.CourseDao;
import com.mpi.astro.dao.StudentDao;
import com.mpi.astro.dao.TutorialDao;
import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.model.edu.Tutorial;
import com.mpi.astro.service.edu.EduService;

@Controller
@RequestMapping("/enrollment/")
public class EduController {
	
	// TODO Use studentService as an intermediary to dao
	
	private static final Logger logger = LoggerFactory.getLogger(EduController.class);

	// TODO Use the service only from within the controller.  May / will the way I'm retrieving information
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private CourseDao courseDao;
	
	@Autowired
	private TutorialDao tutorialDao;
	
	@Autowired
	private EduService eduService;
	
	@RequestMapping(method=RequestMethod.GET,value="view")
	public ModelAndView overview() {
		
		logger.debug("Education Landing page");
		ModelAndView mav = new ModelAndView();
		
		List<Course> courses = courseDao.getCourses();
		logger.debug("Course count: " + courses.size());
		mav.addObject("courses", courses);
		
		List<Student> students = studentDao.getStudents();
		logger.debug("Student count = "+students.size());
		mav.addObject("students",students);
		
		List<Tutorial> tutorials = tutorialDao.getTutorials();
		logger.debug("Tutorial count = "+tutorials.size());
		mav.addObject("tutorials",tutorials);
		
		mav.setViewName("edu/overview");
		return mav;
		
	}
	
	@RequestMapping(method=RequestMethod.GET,value="edit-student")
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
 		
 		// consider moving this logic into service, perhaps doing an outer join
 		Set<Course> currentCourses = student.getCourses();
 		
 		List<Course> availableCourses = courseDao.getCourses();
 		for(Iterator<Course> iter = availableCourses.iterator(); iter.hasNext();) {
 			Course c = iter.next();
 			if(currentCourses.contains(c)) iter.remove();
 		}
 		// filterForJoinable(currentCourse) // etc
 		
 		mav.addObject("available", availableCourses);
 		mav.addObject("student", student);
 		
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="edit-tutorial")
	public ModelAndView editTutorial(@RequestParam(value="id",required=false) Long id) {		
		
		logger.debug("Received request to edit tutorial id : "+id);				
		ModelAndView mav = new ModelAndView();		
 		mav.setViewName("edu/edit-tutorial");
 		Tutorial tut = null;
 		if (id == null) {
 			tut = new Tutorial();
 		} else {
 			tut = tutorialDao.find(id);
 		}
 		
 		mav.addObject("tutorial", tut);
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="edit-course")
	public ModelAndView editCourse(@RequestParam(value="id",required=false) Long id) {		
		
		logger.debug("Received request to edit course id : "+id);				
		ModelAndView mav = new ModelAndView();		
 		mav.setViewName("edu/edit-course");
 		Course course = null;
 		if (id == null) {
 			course = new Course();
 		} else {
 			course = courseDao.find(id);
 		}
 		
 		mav.addObject("course", course);
		return mav;
	}
	
	
	// TODO combine these methods into single post to separate url
	@RequestMapping(method=RequestMethod.POST,value="edit-student") 
	public String saveStudent(@ModelAttribute Student student, 
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("Received postback on student "+student);
		
		// Could this have been done through the ModelAttribute somehow?
		if(request.getParameter("add-courses") != null) {
			String[] adds = request.getParameter("add-courses").split(",");
			
			for(String s : adds) {
				Course cc = courseDao.find(Long.parseLong(s));
				logger.debug("Request to add: " + cc.getName());
				student.addCourse(cc);
				logger.debug("Course " + cc.getName() + " added");
			}
			
		} else logger.debug("No addition requests");
		
		studentDao.save(student);
		return "redirect:view";
	}
	@RequestMapping(method=RequestMethod.POST,value="edit-course") 
	public String saveCourse(@ModelAttribute Course course) {
		logger.debug("Received postback on student " + course);		
		courseDao.save(course);
		return "redirect:view";
	}
	@RequestMapping(method=RequestMethod.POST,value="edit-tutorial") 
	public String saveTutorial(@ModelAttribute Tutorial tut) {
		logger.debug("Received postback on student " + tut);		
		tutorialDao.save(tut);
		return "redirect:view";
	}
	// =========================================================
	
	
	
	@RequestMapping(value = "generate-course", method=RequestMethod.POST)
	public String testValues(@RequestParam("select-course") String courseIdParam, 
			@RequestParam("select-tutorial") String tutorialIdParam,
			HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		// TODO 
		/*
		 * ------- 1. For all students in course, print student, print tutorial
		 * 2. Include ApacheMQ api, successfully send to listening python
		 * 3. Create JSON object instead of string.
		 * 4. Unwrap json in python.
		 */
		
		Long courseId = Long.parseLong(courseIdParam);
		Long tutorialId = Long.parseLong(tutorialIdParam);
		
		Course course = courseDao.find(courseId);
		Tutorial tutorial = tutorialDao.find(tutorialId);
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Course: %s", course.getName()))
			.append("\n")
			.append(String.format("Prototype %s", tutorial.getPrototype()))
			.append("\n");
		
		List<Student> students = eduService.getStudentsInCourse(course);
		for(Student s : students) {
			sb.append(String.format("Student: %s %s",
					s.getFirstName(), s.getLastName()));
		}
		
		String message;
		try {
			message = eduService.sendAndReceive(sb.toString());
		} catch (Exception e) {
			message = e.getMessage();
			e.printStackTrace();
		}
		
		logger.debug(message);
		
		// TODO send via ActiveMQ
		
		return "redirect:view"; // change this
		
		
		/*
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
		
		return "redirect:http://www.google.com"; // for fun */
	}
}
