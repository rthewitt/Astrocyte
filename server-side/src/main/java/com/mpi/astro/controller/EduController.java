package com.mpi.astro.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
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
import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.model.edu.Tutorial;
import com.mpi.astro.service.edu.EduService;
import com.mpi.astro.util.AstrocyteUtils;
import com.mpi.astro.util.MyelinAction;

@Controller
@RequestMapping("/enrollment/")
public class EduController {
	
	// TODO Use studentService as an intermediary to dao
	
	private static final Logger logger = LoggerFactory.getLogger(EduController.class);

	@Autowired
	private EduService eduService ;
	
	@RequestMapping(method=RequestMethod.GET,value="view")
	public ModelAndView overview() {
		
		logger.debug("Education Landing page");
		ModelAndView mav = new ModelAndView();
		
		List<Course> courses = eduService.getAllCourses();
		logger.debug("Course count: " + courses.size());
		mav.addObject("courses", courses);
		
		List<Student> students = eduService.getAllStudents();
		logger.debug("Student count = "+students.size());
		mav.addObject("students",students);
		
		List<Tutorial> tutorials = eduService.getAllTutorials();
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
 			student = eduService.getStudent(id);
 		}
 		
 		// consider moving this logic into service, perhaps doing an outer join
 		Set<Course> currentCourses = student.getCourses();
 		
 		List<Course> availableCourses = eduService.getAllCourses();
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
 			tut = eduService.getTutorial(id);
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
 			course = eduService.getCourse(id);
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
				Course cc = eduService.getCourse(Long.parseLong(s));
				logger.debug("Request to add: " + cc.getName());
				student.addCourse(cc);
				logger.debug("Course " + cc.getName() + " added");
			}
			
		} else logger.debug("No addition requests");
		
		eduService.save(student);
		return "redirect:view";
	}
	@RequestMapping(method=RequestMethod.POST,value="edit-course") 
	public String saveCourse(@ModelAttribute Course course) {
		logger.debug("Received postback on student " + course);		
		eduService.save(course);
		return "redirect:view";
	}
	@RequestMapping(method=RequestMethod.POST,value="edit-tutorial") 
	public String saveTutorial(@ModelAttribute Tutorial tut) {
		logger.debug("Received postback on student " + tut);		
		eduService.save(tut);
		return "redirect:view";
	}
	// =========================================================
	
	
	@RequestMapping(value = "test-produce", method=RequestMethod.GET)
	public String testProduce() {
		/*
		try {
			testProducer.generateMessages();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return "redirect:view";
	}
	
	@RequestMapping(value = "generate-course", method=RequestMethod.POST)
	public String testValues(@RequestParam("select-course") String courseIdParam, 
			@RequestParam("select-tutorial") String tutorialIdParam,
			HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		long courseId = Long.parseLong(courseIdParam);
		long tutorialId = Long.parseLong(tutorialIdParam);
		
		eduService.initializeCourse(courseId, tutorialId);
		
		return "redirect:view"; // change this
	}
}
