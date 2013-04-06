package com.mpi.astro.core.model.comm;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentStatus;
import com.mpi.astro.core.util.AstrocyteConstants;
import com.mpi.astro.core.util.AstrocyteUtils;
import com.mpi.astro.core.util.MyelinAction;
import com.mpi.astro.core.util.AstrocyteConstants.COURSE_WORKFLOW;
import com.mpi.astro.core.util.AstrocyteConstants.STUDENT_STATE;

public class AdvanceCommand extends BaseCommand implements Command {
	
	protected String studentId = null;
	protected String commit = null; // fallback
	protected String statusTag = null;
	protected String advanceStatus = null;
	
	public AdvanceCommand(Map<String, String> context) {
		super(MyelinAction.ADVANCE_STUDENT, context.get("courseUUID"));
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
		
		// no longer a long, is now the studentId / screenname
		Student student = eduService.getStudentEagerBySID(this.studentId);
		 
		String courseName = AstrocyteUtils.getCourseNameFromCourseUUID(this.courseUUID);
		Course course = eduService.getCourseDefinitionByName(courseName);
		CourseInstance enrolledCourse = eduService.getDeployedCourse(this.courseUUID);
		
		 	
		
		// Consider throwing an error here
		// Remember getCourses() fails on equality, due either to hibernate or poor equality override
		if(course == null || enrolledCourse == null || !student.isEnrolled(enrolledCourse) ||
				!(course.getId() == enrolledCourse.getCourseDefinition().getId())) {
			logger.error("Problem advancing student "+student.getId()+" for course "+course.getName()+
					"\nThis will likely lead to data inconsistency!");
			return;
		}
		
		
		StudentStatus current = eduService.getStudentStatus(student, enrolledCourse);
		
		if("request".equals(this.advanceStatus)) {
			
			// NOT IMPLEMENTED, WILL ERROR OUT
//			eduService.notifyProfessorPullRequest();
			
			if(!(statusTag.matches(AstrocyteConstants.CHECKPOINT_REGEX) &&
					Integer.parseInt(statusTag.substring(statusTag.lastIndexOf('-'))) == current.getLessonNum() )) {
				System.out.println("repository state corrupted!");
				// TODO throw exception
			}
			
			switch(student.getState()) {
			case WORKING:
				if(enrolledCourse.getWorkflow() == COURSE_WORKFLOW.PASSIVE) {
					if(eduService.isEligibleForAdvance(student, enrolledCourse)) {
						student.setState(STUDENT_STATE.ADVANCING);
						// determine if entityManager should be flushed.
						eduService.save(student);
						eduService.deployLesson(enrolledCourse.getCourseUUID(), student, 
								AstrocyteUtils.getCheckpointStr(current.getLessonNum()+1));
						logger.info("Deployment request made for student " + student.getId());
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
			eduService.advanceStudentForCourse(student, enrolledCourse);
			student.setState(STUDENT_STATE.NOTIFY_STUDENT); // consider intermediate for ajax ping from client
			eduService.save(student);
			logger.info("Student " + student.getId() + " advanced to lesson " + claimedStatus);
		}
	}

	@Override
	protected Map<String, Object> getContext() {
		// TODO Auto-generated method stub
		return null;
	}

}