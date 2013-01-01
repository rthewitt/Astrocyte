package com.mpi.astro.model.builder;

import com.mpi.astro.model.edu.Course;

public class CourseFactory {
	// Accepts CourseDefinitionObject
	// Creates ConcreteCourse
	public Course createCourse(Object courseDefinition) {
		// Course accepts basic data (Name, number, etc) and collection of Tutorials
		// Tutorials can have various workflows.  Not using inheritance, as that may be used for something like:
		// CodeTutorial extends CourseActivity or something in the future after refactoring.
		return null;
	}
}