package com.mpi.astro.model.edu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import static com.mpi.astro.util.AstrocyteConstants.STUDENT_STATE;

@Entity
@Table( name = "STUDENT" )
@NamedQueries(
		@NamedQuery(name=Student.SQL_FIND_ALL, query="select o from Student o")
)
public class Student implements Serializable {

	private static final long serialVersionUID = 8114286263271432681L;
	
	public static final String SQL_FIND_ALL = "Student.SQL_FIND_ALL";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "STUDENT_ID", unique = true, nullable = false)
	private Long id;

	@Column (name = "FIRST_NAME")
	private String firstName;

	@Column (name = "LAST_NAME")
	private String lastName;
	
	@Column (name = "CURRENT_STATE")
	private STUDENT_STATE state = STUDENT_STATE.WORKING;
	
	/**
	 * This set used to be a simple many-to-many with a join table.  The need for status columns
	 * in the join table lead to a slighly more complex association.  Object type also changed
	 * Old version can be referenced by commit: a041353da6bec41ebc00f3c8a6e9636231b7724b
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.student", cascade=CascadeType.ALL)
	private Set<StudentCourse> courseAssociations = new HashSet<StudentCourse>(0);
	
	@Transient
	private Map<Course, StudentStatus> statusMap = new HashMap<Course, StudentStatus>();

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
	
	// adds to map for convenience, intuitive methods
	public void saveCourseAssociation(StudentCourse course) {
		this.courseAssociations.add(course);
		Course coursePart = course.getCourse();
		this.statusMap.put(coursePart, new StudentStatus(coursePart));
	}

	public Long getId() {
		return id;
	}
	
	// May wish to separate this as an aspect
	public String getStudentId() {
		return id != null ? String.format("%07d", id) : "";
	}
	
	public Tutorial getCurrentTutorialForCourse(Course course) {
		ensureConvenienceMapping(course);
		return statusMap.get(course).getTutorial();
	}
	
	public void advanceStudentForCourse(Course course) {
		ensureConvenienceMapping(course);
		statusMap.get(course).advanceLesson();
	}
	
	public int getLessonStatusForCourse(Course course) {
		ensureConvenienceMapping(course);
		return statusMap.get(course).getLessonNum();
	}
	
	public void ensureConvenienceMapping(Course course) {
		if(statusMap.get(course) == null)
			statusMap.put(course, new StudentStatus(course));
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
	}	

}
