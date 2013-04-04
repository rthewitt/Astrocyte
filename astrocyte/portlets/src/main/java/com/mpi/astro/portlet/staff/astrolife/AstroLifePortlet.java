package com.mpi.astro.portlet.staff.astrolife;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.mpi.astro.core.model.admin.TutorialViewDescriptor;
import com.mpi.astro.core.model.builder.CodeTutorialBuilder;
import com.mpi.astro.core.model.builder.TutorialBuilder;
import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentCourse;
import com.mpi.astro.core.model.edu.Tutorial;
import com.mpi.astro.core.service.edu.EduService;
import com.mpi.astro.core.util.AstrocyteUtils;
import com.mpi.astro.portlet.BaseAstroPortlet;

@Controller
@RequestMapping("VIEW")
public class AstroLifePortlet extends BaseAstroPortlet {
	
	private static final Logger logger = LoggerFactory.getLogger(AstroLifePortlet.class);
	
	@Autowired
	private EduService eduService;
	
	
	@RenderMapping
	public ModelAndView overview() {
		logger.debug("Astrolife view render");
		ModelAndView mav = new ModelAndView();
		
		List<Course> courses = eduService.getAllCourseDefinitions();
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
//	@RequestMapping(method=RequestMethod.GET,value="edit-student") // No longer using webmvc, instead mvc-portlet
	@Transactional
	@RenderMapping(params="edit=student")
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
 		
 		// Below served also to initialize the courseAssociations, not just available
 		/*
 		Set<CourseInstance> currentCourses = student.getCourses();
 		
 		List<Course> availableCourses = eduService.getAllCourseDefinitions();
 		for(Iterator<Course> iter = availableCourses.iterator(); iter.hasNext();) {
 			Course c = iter.next();
 			if(currentCourses.contains(c)) iter.remove();
 		}
 		// filterForJoinable(currentCourse) // etc
 		
 		mav.addObject("available", availableCourses); */
 		mav.addObject("student", student);
 		
		return mav;
	}
	
	@Transactional
	@RenderMapping(params="edit=tutorial")
	public ModelAndView editTutorial(@RequestParam(value="id",required=false) Long id) {		
		
		logger.debug("Received request to edit tutorial id : "+id);				
		ModelAndView mav = new ModelAndView();		
 		mav.setViewName("edu/edit-tutorial");
 		
 		if (id == null) {
 			TutorialViewDescriptor container = new TutorialViewDescriptor();
 			mav.addObject("container", container);
 		}
 		else {
 			Tutorial tut = null;
 			tut = eduService.getTutorialEager(id);
 			mav.addObject("tutorial", tut);
 		}
 			
		return mav;
	}
	
	@RenderMapping(params="edit=course")
	public ModelAndView editCourse(@RequestParam(value="id",required=false) Long id) {		
		
		logger.debug("Received request to edit course id : "+id);				
		ModelAndView mav = new ModelAndView();		
 		mav.setViewName("edu/edit-course");
 		Course course = null;
 		
 		if(id == null) id = 0L;
 		course = eduService.getCourseDefinition(id);
 		
 		mav.addObject("course", course);
		return mav;
	}
	
	@Transactional 
	@ActionMapping(params="edit=student")
	public void saveStudent(@ModelAttribute Student detachedStudent, 
			ActionRequest request, ActionResponse response) {
		logger.debug("Received postback on student "+detachedStudent);
		
		// TODO determine if this was ever resolved.  Search logs.
		for(StudentCourse xx : detachedStudent.getCourseAssociations()) {
			logger.debug("FINALLY one found in the detached object...");
		}
		
		
		// Reattaching to persistenceContext
		Student student = eduService.save(detachedStudent);
		
		student.setStudentId(student.getStudentId()); // This should work correctly
		eduService.save(student); // is this required?  I forget
	}
	
	@ModelAttribute
	public Course createUser() {
	    return eduService.getCourseDefinition(0L);
	}
	
	@Transactional
	@ActionMapping(params="edit=course") 
	public void saveCourse(@ModelAttribute Course course) {
		logger.debug("Received postback on course " + course);		
		eduService.save(course);
	}
	
	@ActionMapping(params="edit=tutorial") 
	public void saveTutorial(@ModelAttribute Tutorial tut, ActionRequest request) {
		logger.debug("Received postback on tutorial " + tut);		
		
		logger.debug("LESSONS CONTAINED IN TUTORIAL " + tut.getId() + "\n" +
				tut.getLessons().size() + " lessons");
		eduService.save(tut);
	}
	
	// TODO refactor builder -> service layer with Spring injection
	// But do note, you don't want a singleton.
	// I don't remember switching this over, have I used it in the JSP?
	@ActionMapping(params="import=tutorial")
	public void createTutorial(@ModelAttribute TutorialViewDescriptor container, 
			ActionRequest request, ActionResponse response) {
		Tutorial newTut = null;
		
		TutorialBuilder builder = new CodeTutorialBuilder();
		builder.createTutorial(container.getName());
		
		// This may or may not be moved to client
		// It's possible that this will be used for the full Project
		// and when cloud9 is loaded, the prototype will be created manually.
		builder.buildProjectDefinition(container.getGitRepo());
		
		String jsonStr =  AstrocyteUtils
				.getExternalTutorialDescriptionAsString(container.getDescriptionFile());
		
		// TODO this breaks because httpclient throws errors.  Handle all exceptions in portlet!
		if(jsonStr == null) {
			String temporaryError = "Problem getting JSON string in controller, was null";
			logger.error(temporaryError);
			response.setRenderParameter("astrolifeError", temporaryError);
		}
		builder.buildLessons(jsonStr);
		
		newTut = builder.getTutorial();
		newTut.setDescription(container.getDescription());
		eduService.save(newTut);
	}
	
	@RenderMapping(params="astrolifeError")
	public ModelAndView bigProblemLittlePortlet(RenderRequest request) {
		String errorText = 
				(request.getParameter("astrolifeError") != null ? request.getParameter("astrolifeError") :
					"A problem occured during previous action.");
		logger.debug("Error in astrolife portlet: " + errorText);
		return new ModelAndView("edu/failure").addObject("errorText", errorText);
	}
}