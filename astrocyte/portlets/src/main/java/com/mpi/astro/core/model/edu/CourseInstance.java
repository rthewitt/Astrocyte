package com.mpi.astro.core.model.edu;

import java.util.Date;
import java.util.Set;

public interface CourseInstance extends Course {
	
	public String getCourseUUID();
	
	public Course getCourseDefinition();
	
	public Set<StudentCourse> getStudAssociations();
	
	public Date getDeployedDate();

	public void setStudAssociations(Set<StudentCourse> studAssociations);
	
	public Set<Student> getStudentsInCourse();
	
	public void addStudentAssociation(StudentCourse enrollment);
}