package com.mpi.astro.core.model.edu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.mpi.astro.core.util.AstrocyteConstants.COURSE_WORKFLOW;

@Entity
@Table(name="COURSE")
public class Syllabus extends BaseCourseDefinition implements Serializable, Course {

	private static final long serialVersionUID = 2594594478717652373L;
	
	@Column(name = "COURSE_NAME", nullable = false)
	private String name;
	
	@Column(name = "COURSE_DESC", nullable = false)
	private String description;
	
//	This was left over from refactor, and was referenced in hibernate query
//	@OneToMany(mappedBy = "pk.course")
//	private Set<StudentCourse> studAssociations = new HashSet<StudentCourse>(0);
	
	@OneToMany(mappedBy = "pkey.course")
	private Set<CourseTutorial> tutAssociations = new HashSet<CourseTutorial>(0);
	
	@OneToMany(mappedBy="syllabus", targetEntity=BaseCourseInstance.class, fetch=FetchType.LAZY)
	private List<CourseInstance> deployedCourses = new ArrayList<CourseInstance>(0);
	
	public Syllabus() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	// May also want restricted to "active"
	// Business logic may instead later force a single deployment of course
	public List<CourseInstance> getAllDeployedCourses() {
		return this.deployedCourses;
	}
	
//	public Set<StudentCourse> getStudAssociations() {
//		return studAssociations;
//	}
//
//	public void setStudAssociations(Set<StudentCourse> studAssociations) {
//		this.studAssociations = studAssociations;
//	}
	
	// Any reason to force unmodifiable?
	/* Also, would placing annotations on setters increase performance for getting just the
	   student ids for initialization / generation / future reporting? */
//	public Set<Student> getStudentsInCourse() {
//		Set <Student> ret = new HashSet<Student>();
//		for(StudentCourse sc : this.studAssociations)
//			ret.add(sc.getStudent());
//		return Collections.unmodifiableSet(ret);
//	}
	
//	public void addStudentAssociation(StudentCourse enrollment) {
//		this.studAssociations.add(enrollment);
//	}
	
	public Set<CourseTutorial> getTutAssociations() {
		return tutAssociations; 
	}

	public void setTutAssociations(Set<CourseTutorial> tutAssociations) {
		this.tutAssociations = tutAssociations;
	}

	public void addTutorialAssociation(CourseTutorial assoc) {
		this.tutAssociations.add(assoc); 
	}
	
	public Tutorial getTutorialByOrderNumber(int orderNum) {
		for(CourseTutorial ct : this.tutAssociations)
			if(ct.getOrder() == orderNum) return ct.getTutorial();
		return null;
	} 

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Syllabus other = (Syllabus) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		/*
		if (tutorials == null) {
			if (other.tutorials != null)
				return false;
		} else if (!tutorials.equals(other.tutorials))
			return false; */
		return true;
	}
}