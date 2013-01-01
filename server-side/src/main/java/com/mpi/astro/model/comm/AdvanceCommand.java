package com.mpi.astro.model.comm;

import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.util.AstrocyteConstants;
import com.mpi.astro.util.AstrocyteConstants.COURSE_WORKFLOW;
import com.mpi.astro.util.AstrocyteConstants.STUDENT_STATE;
import com.mpi.astro.util.AstrocyteUtils;
import com.mpi.astro.util.MyelinAction;

public class AdvanceCommand extends BaseCommand implements Command {
	
	protected String studentId = null;
	protected String commit = null; // fallback
	protected String statusTag = null;
	protected String advanceStatus = null;
	
	public AdvanceCommand(Map<String, String> context) {
		super(MyelinAction.ADVANCE_STUDENT, context.get("courseName"));
		this.studentId = context.get("studentId");
		this.commit = context.get("commit");
		this.statusTag = context.get("checkpoint");
		this.advanceStatus = context.get("status");
	}

	/*
	 * This is called from both post-receive and from thalamus after advancement
	 * Second case is receipt and permission to advance Student in DB
	 * Consider inclusion of CLASS based update
	 */
	@Transactional
	@Override
	public void execute() {
		
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
		int currentLesson = student.getLessonStatusForCourse(course);
		
		// TODO decide whether or not arguments can or will change meaning
		
		if("request".equals(this.advanceStatus)) {
			
			eduService.notifyProfessorPullRequest();
			
			if(!(statusTag.matches(AstrocyteConstants.CHECKPOINT_REGEX) &&
					Integer.parseInt(statusTag.substring(statusTag.lastIndexOf('-'))) == currentLesson )) {
				// TODO exception logging!
				System.out.println("repository state corrupted!");
				return;
			}
			
			switch(student.getState()) {
			case WORKING:
				if(course.getWorkflow() == COURSE_WORKFLOW.PASSIVE) {
					if(eduService.isEligibleForAdvance(student, course)) {
						student.setState(STUDENT_STATE.ADVANCING);
						// determine if entityManager should be flushed.
						eduService.save(student);
						eduService.deployLesson(course.getId(), student, 
								AstrocyteUtils.getCheckpointStr(currentLesson+1));
					} else System.out.println("Tutorial finished.  Unroll next if available...");
				}
				else {
					System.out.println("Advanced workflows not yet available.");
					return;
				}
				break;
			case ADVANCING:
			case APPROVAL:
			default:
				System.out.println("Cannot advance " + student.getStudentId() + " with state " + student.getState());
				return;
			}
			
		} else if("confirm".equals(this.advanceStatus)) {
			if(!(statusTag.matches(AstrocyteConstants.CHECKPOINT_REGEX) &&
					Integer.parseInt(statusTag.substring(statusTag.lastIndexOf('-'))) == ++currentLesson )) {
				System.out.println("Confirmation receipt not accepted after advance of " + this.studentId + " to " + this.statusTag);
				return;
			}
			if(student.getState() != STUDENT_STATE.ADVANCING) {
				System.out.println("Receipt for " + this.studentId + " discarded, student in state " + student.getState().toString());
				return;
			}
			// this could also be hql
			student.advanceStudentForCourse(course);
			student.setState(STUDENT_STATE.WORKING); // consider intermediate for ajax ping from client
			eduService.save(student);
		}
	}

	@Override
	protected Map<String, Object> getContext() {
		// TODO Auto-generated method stub
		return null;
	}

}