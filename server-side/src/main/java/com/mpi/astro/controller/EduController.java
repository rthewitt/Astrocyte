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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.Lesson;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.model.edu.StudentCourse;
import com.mpi.astro.model.edu.Tutorial;
import com.mpi.astro.service.edu.EduService;

@Controller
@RequestMapping("/enrollment/")
public class EduController {
	
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
	
	// Added this annotation in my attempt to initiate a conversation.  I'd like one context per request with reattachment
	// Primarily because I don't know how to get an Interceptor from Spring - would that even work with Stateless requests?
	@Transactional
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
 		
 		Set<Course> currentCourses = eduService.getCoursesForStudent(student);
 		
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
 		if (id == null)
 			tut = new Tutorial();
 		else
 			tut = eduService.getTutorial(id);
 		
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
	
	@Transactional
	@RequestMapping(method=RequestMethod.POST,value="edit-student") 
	public String saveStudent(@ModelAttribute Student detachedStudent, 
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("Received postback on student "+detachedStudent);
		
		// Come back to this, can't figure out why no associations existed
		for(StudentCourse xx : detachedStudent.getCourseAssociations()) {
			logger.debug("FINALLY one found in the detached object...");
		}
		
		
		// Reattaching to persistenceContext
		Student student = eduService.save(detachedStudent);
		
		for(StudentCourse xx : student.getCourseAssociations()) {
			logger.debug("Well, at least there was one here");
		}
		
		// Could this have been done through the ModelAttribute somehow?
		String addInput = request.getParameter("add-courses");
		if(addInput != null && ! addInput.isEmpty()) {
			String[] adds = request.getParameter("add-courses").split(",");
			
			for(String s : adds) {
				Course cc = eduService.getCourse(Long.parseLong(s));
				logger.debug("Request to add: " + cc.getName());
				if(!student.isEnrolled(cc)) {
					eduService.enrollStudent(student, cc);
					eduService.save(cc); // student saved further down
					}
				    
				logger.debug("Course " + cc.getName() + " added");
			}
			
		} else logger.debug("No course addition requests for student");
		
		eduService.save(student);
		
		return "redirect:view";
	}
	@Transactional // MAYBE this will force a db record ?
	@RequestMapping(method=RequestMethod.POST,value="edit-course") 
	public String saveCourse(@ModelAttribute Course course) {
		logger.debug("Received postback on course " + course);		
		eduService.save(course);
		return "redirect:view";
	}
	@RequestMapping(method=RequestMethod.POST,value="edit-tutorial") 
	public String saveTutorial(@ModelAttribute Tutorial tut, HttpServletRequest request) {
		logger.debug("Received postback on tutorial " + tut);		
		// TEMPORARY TODO remove
		List<Lesson> checkPoints = tut.getLessons();
		if(checkPoints == null || checkPoints.size() < 1) {
			logger.debug(checkPoints == null ? "lessons was NULL" : "existed, size was " + checkPoints.size());
			logger.debug("Adding new lessons");
			tut.addLesson(1, "TEST-VAL");
			tut.addLesson(2, "TEST-VAL");
		} else {
			logger.debug("Not null, size was: " + checkPoints.size());
			for(Lesson l : checkPoints)
				l.setTutorial(tut);
		}
		
		/* else {
			for(int x=0; x<tut.getLessons().size(); x++) {
				logger.debug("Searching for lesson: " + x);
				// get the submitted input tag via name convention
				String uri = request.getParameter("lesson-" + x);
				logger.debug("Setting lesson uri: " + uri);
				// if we're not dealing with copies, this should save correctly. 
				if(uri != null)
						checkPoints.get(x).setMediaURI(uri);
				else logger.error("URI parameter for lesson was not found.");
			}
		} */
		// -------------
		logger.debug("LESSONS CONTAINED IN TUTORIAL " + tut.getId() + "\n" +
				tut.getLessons().size() + " lessons");
		eduService.save(tut);
		return "redirect:view";
	}
	// =========================================================
	
	
	// using for testing jms producer
	@RequestMapping(value = "test-produce", method=RequestMethod.GET)
	public String testProduce() {
		return "redirect:view";
	}
	 
	
	@RequestMapping(value = "generate-course", method=RequestMethod.POST)
	public String generateCourse(@RequestParam("select-course") String courseIdParam, 
			@RequestParam("select-tutorial") String tutorialIdParam,
			HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		long courseId = Long.parseLong(courseIdParam);
		long tutorialId = Long.parseLong(tutorialIdParam);
		
		eduService.initializeCourse(courseId, tutorialId);
		
		return "redirect:/course/" + courseId + "/";
	}
	
	@RequestMapping(value = "test-redirect", method=RequestMethod.GET)
	public String testRedirect() {
		return "/test/redirect";
	}
	
}
