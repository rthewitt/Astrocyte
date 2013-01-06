package com.mpi.astro.model.comm;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.model.edu.StudentStatus;
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
		
		Student student = eduService.getStudentEager(Long.parseLong(this.studentId));
		
		// TODO either change thalamus to use courseId, or force courseName to be unique 
		// guarantee transfer without changes to string
		Course course = eduService.getCourseByName(this.courseName);
		
		// Consider throwing an error here
		if(course == null || !student.getCourses().contains(course)) {
			logger.error("Problem advancing student "+student.getId()+" for course "+course.getName()+
					"\nThis will likely lead to data inconsistency!");
			return;
		}
			
		StudentStatus current = eduService.getStudentStatus(student, course);
		
		if("request".equals(this.advanceStatus)) {
			
			eduService.notifyProfessorPullRequest();
			
			if(!(statusTag.matches(AstrocyteConstants.CHECKPOINT_REGEX) &&
					Integer.parseInt(statusTag.substring(statusTag.lastIndexOf('-'))) == current.getLessonNum() )) {
				System.out.println("repository state corrupted!");
				// TODO throw exception
			}
			
			switch(student.getState()) {
			case WORKING:
				if(course.getWorkflow() == COURSE_WORKFLOW.PASSIVE) {
					if(eduService.isEligibleForAdvance(student, course)) {
						student.setState(STUDENT_STATE.ADVANCING);
						// determine if entityManager should be flushed.
						eduService.save(student);
						eduService.deployLesson(course.getId(), student, 
								AstrocyteUtils.getCheckpointStr(current.getLessonNum()+1));
					} else logger.info("Tutorial finished.  Unroll next if available...");
				}
				else {
					logger.warn("Advanced workflows not yet available!");
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
			int claimedStatus = Integer.parseInt(statusTag.substring(statusTag.lastIndexOf('-')+1));
			if(!(statusTag.matches(AstrocyteConstants.CHECKPOINT_REGEX) &&
					claimedStatus == current.getLessonNum()+1 )) {
				logger.warn(String.format("Suspect Advance Confirmation receipt indicates next lesson is %d - " +
						"Current lesson on record for student is %d", claimedStatus, current.getLessonNum()));
				logger.warn("Confirmation receipt not accepted after advance of " + this.studentId + " to " + this.statusTag);
				return;
			}
			if(student.getState() != STUDENT_STATE.ADVANCING) {
				System.out.println("Receipt for " + this.studentId + " discarded, student in state " + student.getState().toString());
				return;
			}
			// Move more of this logic into the service layer, use custom exceptions
			eduService.advanceStudentForCourse(student, course);
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