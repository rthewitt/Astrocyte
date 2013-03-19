package com.mpi.astro.core.model.builder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.CourseImpl;
import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.CourseTutorial;
import com.mpi.astro.core.model.edu.Syllabus;
import com.mpi.astro.core.model.edu.Tutorial;
import com.mpi.astro.core.service.edu.EduService;

/**
 * @author Ryan Hewitt
 *
 * This abstraction is to make it easier to extend in the future.  I intend to have
 * different types of courses with inheritence, also allow self-paced, class-paced,
 * etc.  The factories can be managed via Spring and will centralize my changes without
 * cluttering up my service layer.  Instances can then be autowired with @Qualifier.
 */
@Component
public class CourseFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(CourseFactory.class);
	
	@Autowired
	private EduService eduService;
	
	// will expand
	public Course createDefinition() {
		return new Syllabus();
	}
	
	@Transactional
	public CourseInstance createCourse(Course definition) {
		// Course accepts basic data (Name, number, etc) and collection of Tutorials
		// Tutorials can have various workflows.  Not using inheritance, as that may be used for something like:
		// CodeTutorial extends CourseActivity or something in the future after refactoring.
		CourseInstance course = new CourseImpl(definition);
		
		course.setDescription("Sample course from CourseFactory");
		
		List<Tutorial> tutorialProgression = eduService.getTutorialListForCourse(definition);
		
		// Currently cannot handle list, and zero is legitimate problem
		if(tutorialProgression.size() != 1)
			throw new RuntimeException("Cannot [yet] create course with "+tutorialProgression.size()+" tutorials");
		
		logger.debug("course during building: " + course.getName());
		logger.debug(eduService.testMethod());
		course = eduService.save(course);
		
		for(Tutorial tutorial : tutorialProgression) {
			CourseTutorial association = new CourseTutorial();
			association.setCourse(course);
			association.setTutorial(tutorial);
			course.addTutorialAssociation(association);
			tutorial.addCourseAssociation(association);
			
			eduService.save(association);
			eduService.save(tutorial);
		}
		eduService.save(course);
		
		return course;
	}
}