package com.mpi.astro.model.edu;

import java.io.Serializable;

import javax.persistence.ManyToOne;

public class StudentCourseId implements Serializable {

	private static final long serialVersionUID = -8869705074497650857L;
	
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