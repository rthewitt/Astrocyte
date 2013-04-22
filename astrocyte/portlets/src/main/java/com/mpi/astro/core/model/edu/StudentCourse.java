package com.mpi.astro.core.model.edu;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.mpi.astro.core.util.AstrocyteConstants;
import com.mpi.astro.core.util.AstrocyteConstants.STUDENT_STATE;

/**
 * Strategy borrowed from:
 * http://www.mkyong.com/hibernate/hibernate-many-to-many-example-join-table-extra-column-annotation/
 * 
 * In a sense, this object could more accurately be described as a Progress container
 * 
 * Composite object to represent ManyToMany relationship between Student and Course, with additional columns
 * @author Ryan Hewitt
 */
@Entity
@Table(name = "STUDENT_COURSE")
@AssociationOverrides({
	@AssociationOverride(name = "pk.student",
			joinColumns = @JoinColumn(name = "STUDENT_ID")),
	@AssociationOverride(name = "pk.course",
			joinColumns = @JoinColumn(name = "COURSE_ID"))
})
public class StudentCourse implements Serializable {

	private static final long serialVersionUID = 3105539532934783763L;
	
	@EmbeddedId
	private StudentCourseId pk = new StudentCourseId();
	
	@Temporal(TemporalType.DATE)
	@Column(name = "ENROLL_DATE", nullable=false, length = 10)
	private Date enrollDate;
	
	@Column(name = "LESSON_NUM")
	private Integer lessonNum = AstrocyteConstants.NOT_STARTED;
	
	@Column(name = "TUTORIAL_NUM")
	private Integer tutorialNum = AstrocyteConstants.NOT_STARTED;
	
	@Enumerated(EnumType.STRING)
	@Column (name = "CURRENT_STATE")
	private STUDENT_STATE state = STUDENT_STATE.WORKING;
	
	public StudentCourse() {}
	
	public StudentCourseId getPk() {
		return pk;
	}
	
	public void setPk(StudentCourseId pk) {
		this.pk = pk;
	}
	
	public STUDENT_STATE getState() {
		return this.state;
	}
	
	public void setState(STUDENT_STATE state) {
		this.state = state;
	}
	
	// TODO these integer fields are bad design.  They should be foreign keys, constrained by the database.
	// Except how do I handle 0/0?  Tutorial is not required for Course, Lesson is not required for Tutorial.
	// This means quite frankly that there's a difference between Course and CourseDefinition.  Also between
	// Tutorial and TutorialDefinition.  Consider.
	public Integer getLessonNum() {
		return lessonNum;
	}
	
	public void setLessonNum(Integer lessonNum) {
		this.lessonNum = lessonNum;
	}
	
	public Integer getTutorialNum() {
		return tutorialNum;
	}
	
	public void setTutorialNum(Integer tutorialNum) {
		this.tutorialNum = tutorialNum;
	}
	
	@Transient
	public Student getStudent() {
		return getPk().getStudent();
	}
	
	public void setStudent(Student student) {
		getPk().setStudent(student);
	}
	
	@Transient
	public CourseInstance getCourse() {
		return getPk().getCourse();
	}
	
	public void setCourse(CourseInstance course) {
		getPk().setCourse(course);
	}
	
	public Date getEnrollDate() {
		return this.enrollDate;
	}
	
	public void setEnrollDate(Date enrollDate) {
		this.enrollDate = enrollDate;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		
		StudentCourse other = (StudentCourse) o;
		
		if(getPk() != null ? !getPk().equals(other.getPk())
				: other.getPk() != null)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return (getPk() != null ? getPk().hashCode() : 0);
	}
}