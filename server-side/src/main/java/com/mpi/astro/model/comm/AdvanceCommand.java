package com.mpi.astro.model.comm;

import java.util.Map;
import java.util.Set;

import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.util.MyelinAction;

public class AdvanceCommand extends BaseCommand implements Command {
	
	protected String studentId = null;
	protected String commit = null; // fallback
	
	public AdvanceCommand(Map<String, String> context) {
		super(MyelinAction.ADVANCE_STUDENT, context.get("courseName"));
		this.studentId = context.get("studentId");
		this.commit = context.get("commit");
	}

	@Override
	public void execute() {
		eduService.notifyProfessorPullRequest();
		Student student = eduService.getStudent(Long.parseLong(this.studentId));
		Set<Course> validCourses = eduService.getCoursesForStudent(student);
		
		Course course = null;
		for(Course c : validCourses) {
			if(c.getName().equals(this.courseName)) {
				course = c;
				break;
			}
		}
		// TODO Log, throw exception
		if(course == null) ; // Big problem
		
		String commitRef = eduService.getNextCheckpoint(student, course); // currently hard-coded
		
		student.getCurrentTutorialForCourse(course);
		
		eduService.deployLesson(course.getId(), student, commitRef);
	}

	@Override
	protected Map<String, Object> getContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
