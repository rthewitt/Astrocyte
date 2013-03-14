package com.mpi.astro.model.edu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpi.astro.service.edu.EduService;
import com.mpi.astro.util.AstrocyteConstants.STUDENT_STATE;

import edu.emory.mathcs.backport.java.util.Collections;

@Entity
@Table( name = "STUDENT" )
// Understand this addition.  What was the initial problem?
@NamedQueries(
		@NamedQuery(name=Student.SQL_FIND_ALL, query="select o from Student o")
)
public class Student implements Serializable {

	private static final long serialVersionUID = 8114286263271432681L;
	
	private static final Logger logger = LoggerFactory.getLogger(Student.class);
	
	public static final String SQL_FIND_ALL = "Student.SQL_FIND_ALL";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "STUDENT_ID", unique = true, nullable = false)
	private Long id;

	@Column (name = "FIRST_NAME")
	private String firstName;

	@Column (name = "LAST_NAME")
	private String lastName;
	
	@Enumerated(EnumType.STRING)
	@Column (name = "CURRENT_STATE")
	private STUDENT_STATE state = STUDENT_STATE.WORKING;
	
	// Do not allow cascading until you understand why it breaks everything here.  (Cycle or different contexts)
	@OneToMany(mappedBy = "pk.student") // , cascade=CascadeType.ALL
	private Set<StudentCourse> courseAssociations = new HashSet<StudentCourse>(0);

	public Student() {
	}

	public Student(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public STUDENT_STATE getState() {
		return this.state;
	}
	
	public void setState(STUDENT_STATE state) {
		this.state = state;
	}
	
	public Set<StudentCourse> getCourseAssociations() {
		return this.courseAssociations;
	}
	
	public void setCourseAssociations(Set<StudentCourse> assoc) {
		this.courseAssociations = assoc;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Course> getCourses() {
		Set<Course> courses = new HashSet<Course>();
		for(StudentCourse enrollment : this.getCourseAssociations())
			courses.add(enrollment.getCourse());
		return Collections.unmodifiableSet(courses);
	}
	
	public boolean isEnrolled(Course course) {
		logger.debug("Course "+course.getName()+" not found in convenience map, checking associations.");
		
		boolean enrolled = false;
		for(StudentCourse sc : courseAssociations)
			if(sc.getCourse().getId() == course.getId()) enrolled = true;
		logger.debug("Student " + this.id + (enrolled ? " was" : " was NOT") + " enrolled in " + course.getName());
		return enrolled;
	}
	
	// TODO determine safety of equality of Courses as keys given scope of identity.
	public void addCourseAssociation(StudentCourse enrollment) {
		this.courseAssociations.add(enrollment);
		Course coursePart = enrollment.getCourse();
	}

	public Long getId() {
		return id;
	}
	
	// May wish to separate this as an aspect
	public String getStudentId() {
		return id != null ? String.format("%07d", id) : "";
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {

		return super.toString() + " name = " + firstName + " " + lastName
				+ " id = " + id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}
	
	/*
	 * This will serve to better encapsulate student progress
	 * A few questions:
	 * Is it extraneous?  Is progress duplicated unnecessarily in Student_Course?
	 * Will the status object ever need to refresh itself?
	 * I think it should refresh, so that I can use a query like:
	 * if(statusMap.get(Course) == null) statusMap.put(Course, new statusMap); -> then refresh it.
	 */
	/*
	private class StudentStatus {
		
		private Course relevantCourse = null;
		private StudentCourse association = null;
		
		StudentStatus(Course course) {
			this.relevantCourse = course;
			for(StudentCourse sc : courseAssociations) {
				if(relevantCourse.getId() == sc.getCourse().getId())
					this.association = sc;
			}
		}
		public int getTutorialNum() {
			return this.association.getTutorialNum();
		}
		public int getLessonNum() {
			return this.association.getLessonNum();
		}
		public void advanceLesson() {
			this.association.setLessonNum(getLessonNum()+1);
		}
		public Tutorial getTutorial() {
			return relevantCourse.getTutorialByOrderNumber(getTutorialNum()); 
		}
	} */	

}
