package com.mpi.astro.model.edu;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Embeddable
public class StudentCourseId implements Serializable {
	
	// I am using transient on the getters one level up, but I'm using field-based discovery
	// so it seems unnecessary.  However this is an embedded object
	// Should I make these fields transient as well?

	private static final long serialVersionUID = -8869705074497650857L;
	
	// Book example places the objects in the enclosing class, uses just id.
	// Does the ManyToOne translate through the hierarchy appropriately?
	@ManyToOne
	private Student student;
	@ManyToOne
	private Course course;
	
	public Student getStudent() {
		return student;
	}
	
	public void setStudent(Student student) {
		this.student = student;
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
		
		StudentCourseId other = (StudentCourseId) o;
		
		if(student != null ? !student.equals(other.student) : other.student != null) return false;
		if(course != null ? !course.equals(other.course) : other.course != null) return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int result;
		result = (student != null ? student.hashCode() : 0);
		result = 31 * result + (course != null ? course.hashCode() : 0);
		return result;
	}
	
}