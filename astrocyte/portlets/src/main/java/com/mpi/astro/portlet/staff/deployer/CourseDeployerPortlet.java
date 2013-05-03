package com.mpi.astro.portlet.staff.deployer;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import scala.actors.threadpool.Arrays;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.Tutorial;
import com.mpi.astro.core.service.edu.AstroService;
import com.mpi.astro.core.service.edu.EduService;
import com.mpi.astro.portlet.BaseAstroPortlet;

@Controller
@RequestMapping("VIEW")
public class CourseDeployerPortlet extends BaseAstroPortlet {
	
	// TODO autowire logs
	private static final Logger logger = LoggerFactory.getLogger(CourseDeployerPortlet.class);
	
	@Autowired
	EduService eduService;
	@Autowired
	AstroService astroService;

	@RenderMapping
	public ModelAndView deployView() {
		logger.debug("Course Deployer view render");
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
		
		mav.setViewName("course-deployer/view");
		return mav;	
	}
	
	// note that when refreshed, the page errors out due to select-course/tutorial being stripped
		// TODO solve this issue
		@ActionMapping(params="deploy=course")
		public void generateCourse(@RequestParam("select-course") String courseIdParam, 
				@RequestParam("select-tutorial") String tutorialIdParam,
				@RequestParam(value="stub-out", required=false) String stubDeployment,
				ActionRequest request, ActionResponse response) throws IOException{
			logger.debug("A request was made to deploy course with id: " + courseIdParam);
			
			long courseId = Long.parseLong(courseIdParam);
			
			Course def = eduService.getCourseDefinition(courseId);
			
			// ====================== Should be removed soon ============
			long tutorialId = Long.parseLong(tutorialIdParam);
			String enrollStudents = request.getParameter("enroll-students");
			// ==========================================================
			
			List<String> studentIds = (List<String>)Arrays.asList(enrollStudents.split(","));
			
			CourseInstance deployed;
			if(StringUtils.isEmpty(stubDeployment) || "false".equalsIgnoreCase(stubDeployment))
				deployed = eduService.deployCourse(def, studentIds, tutorialId);
			else deployed = eduService.deployCourse(def, studentIds, tutorialId, true);
			/*
			int numUsers = enrolledUsers.length;
			String[][] names = new String[numUsers][2];
			String userIds[] = new String[numUsers];
			for(int i = 0; i<numUsers; i++) {
				userIds[i] = enrolledUsers[i].getStudentId();
				names[i][0] = enrolledUsers[i].getFirstName();
				names[i][1] = enrolledUsers[i].getLastName();
			} */
			List<Student> enrolledUsers = eduService.getStudentsForCourse(deployed.getCourseUUID());
			long currentUserId = Long.valueOf(request.getRemoteUser());
			ThemeDisplay themeDisplay  =(ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
			try {
				long communityId = astroService.createCommunityForCourseInstance(themeDisplay, 
						enrolledUsers.toArray(new Student[enrolledUsers.size()]), deployed);
				deployed.associateWithPortal(String.valueOf(communityId)); // This may shit from LiferayId, don't want it bound
			} catch(Exception e) {
				logger.error("Course was deployed, but there was an error creating Lifery group.", e);
				response.setRenderParameter("astrolifeError", "Course was deployed, but there was an error creating Lifery group.");
				return;
			}
			
			response.setRenderParameter("astrolifeError", "Your course has been deployed. (Action under development)");
		}
}
