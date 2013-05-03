package com.mpi.astro.core.model.edu;

import java.util.Date;
import java.util.Set;

import com.mpi.astro.core.util.AstrocyteConstants.COURSE_WORKFLOW;

public interface CourseInstance {
	
	public String getName();

	public void setName(String name);

	public String getDescription();

	public void setDescription(String description);
	
	public Set<CourseTutorial> getTutAssociations();

	public void setTutAssociations(Set<CourseTutorial> tutAssociations);

	public void addTutorialAssociation(CourseTutorial assoc);
	
	public Tutorial getTutorialByOrderNumber(int orderNum);
	
	public String getCourseUUID();
	
	public Long getAstroGroupId();
	
	public Course getCourseDefinition();
	
	public COURSE_WORKFLOW getWorkflow();
	
	public void setWorkflow(COURSE_WORKFLOW wf);
	
	public Set<StudentCourse> getStudAssociations();
	
	public Date getDeployedDate();

	public void setStudAssociations(Set<StudentCourse> studAssociations);
	
	public Set<Student> getStudentsInCourse();
	
	public void addStudentAssociation(StudentCourse enrollment);
	
	public void associateWithPortal(String identifier);
}