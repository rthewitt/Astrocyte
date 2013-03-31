package com.mpi.astro.core.model.edu;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class CourseTutorialId implements Serializable {
	
	private static final long serialVersionUID = 784303429890146551L;
	
	@ManyToOne
	private Tutorial tutorial;
	@ManyToOne(targetEntity=BaseCourseDefinition.class)
	private Course course;
	
	public Tutorial getTutorial() {
		return tutorial;
	}
	
	public void setTutorial(Tutorial tutorial) {
		this.tutorial = tutorial;
	}
	
	public Course getCourse() {
		return course;
	}
	
	public void setCourse(Course course) {
		this.course = course;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		CourseTutorialId other = (CourseTutorialId) o;
		
		if(tutorial != null ? !tutorial.equals(other.tutorial) : other.tutorial != null) return false;
		if(course != null ? !course.equals(other.course) : other.course != null) return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int result;
		result = (tutorial != null ? tutorial.hashCode() : 0);
		result = 31 * result + (course != null ? course.hashCode() : 0);
		return result;
	}
	
}