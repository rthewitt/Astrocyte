package com.mpi.astro.portlet.student.mycourses;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.portlet.RenderRequest;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentVM;
import com.mpi.astro.core.model.vm.VM;
import com.mpi.astro.core.service.edu.EduService;
import com.mpi.astro.portlet.BaseAstroPortlet;

@Controller
@RequestMapping("VIEW")
public class MyCoursesView extends BaseAstroPortlet {
	
	private static final Logger logger = LoggerFactory.getLogger(MyCoursesView.class);
	
	@Autowired
	private EduService eduService;
	
	@RenderMapping
	public ModelAndView myCoursesRender(RenderRequest request) throws ServletException {
		long currentUserId = Long.valueOf(request.getRemoteUser());
		ThemeDisplay themeDisplay  =(ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		User lrStudent;
		
		try {
			lrStudent = UserLocalServiceUtil.getUser(currentUserId);
		} catch (PortalException e) {
			logger.error("Portal threw exception getting logged in user with id: " + currentUserId, e);
			return null;
		} catch (SystemException e) {
			logger.error("System threw exception getting logged in user with id: " + currentUserId, e);
			return null;
		}
		
		logger.debug("MyCourses view render, current student sn: " + lrStudent.getScreenName());
		
		Student student;
		try{
			student = eduService.getStudentEagerBySID(lrStudent.getScreenName());
		}catch(NoResultException ne){
			logger.warn(String.format("logged in user %s does not correlate to Astrocyte student!", lrStudent.getScreenName()));
			return null;
		}
		

		Set<CourseInstance> courses = student.getCourses();
		logger.debug(String.format("Course count for student %s: %s", student.getStudentId(), courses.size()));
		
		Map<String, VM> myMachines = new HashMap<String, VM>();
		
		for(CourseInstance ci : courses) {
			// equality was failing before due to the way I'm getting these objects.
			StudentVM mapping = eduService.getMappingForStudent(student, ci.getCourseUUID());
			// In the near future this will be commonplace, and we'll just need to grab a machine from the pool.
			if(mapping == null) 
				throw new ServletException("Machine not found for course "+ci.getCourseUUID()+"!");
			else {
				myMachines.put(ci.getCourseUUID(), mapping.getVM());
			}
		}
		
		return new ModelAndView("mycourses/full")
		.addObject("courses", courses)
		.addObject("vMap", myMachines);
	}
	
}