package com.mpi.astro.model.edu;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table( name = "STUDENT" )
public class Student implements Serializable {

	private static final long serialVersionUID = 8114286263271432681L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "STUDENT_ID", unique = true, nullable = false)
	private Long id;

	@Column (name = "FIRST_NAME")
	private String firstName;

	@Column (name = "LAST_NAME")
	private String lastName;
	
	/**
	 * This set used to be a simple many-to-many with a join table.  The need for status columns
	 * in the join table lead to a slighly more complex association.  Object type also changed
	 * Old version can be referenced by commit: a041353da6bec41ebc00f3c8a6e9636231b7724b
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.student", cascade=CascadeType.ALL)
	private Set<StudentCourse> courseAssociations = new HashSet<StudentCourse>(0);

	public Student() {
	}

	public Student(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Set<StudentCourse> getCourseAssociations() {
		return this.courseAssociations;
	}
	
	public void setCourseAssociations(Set<StudentCourse> assoc) {
		this.courseAssociations = assoc;
	}
	
	public void saveCourseAssociation(StudentCourse course) {
		this.courseAssociations.add(course);
	}

	public Long getId() {
		return id;
	}
	
	// May wish to separate this as an aspect
	public String getStudentId() {
		return id != null ? String.format("%07d", id) : "";
	}
	
	public Tutorial getCurrentTutorialForCourse(Course course) {
		for(StudentCourse sc : this.courseAssociations)
			if(course.getId() == sc.getCourse().getId())
				return course.getTutorialByOrderNumber(sc.getTutorialNum());
		return null;
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

}
