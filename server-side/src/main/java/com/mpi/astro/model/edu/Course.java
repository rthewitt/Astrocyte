package com.mpi.astro.model.edu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import static com.mpi.astro.util.AstrocyteConstants.COURSE_WORKFLOW;

@Entity
@Table(name="COURSE")
public class Course implements Serializable {
	
	private static final long serialVersionUID = 6094379996028682456L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COURSE_ID", unique = true, nullable = false)
	private Long id;
	
	@Column(name = "COURSE_NAME", nullable = false)
	private String name;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "WORKFLOW", nullable = false)
	private COURSE_WORKFLOW workflow = COURSE_WORKFLOW.PASSIVE;
	
	@Column(name = "COURSE_DESC", nullable = false)
	private String description;
	
	@OneToMany(mappedBy = "pk.course")
	private Set<StudentCourse> studAssociations = new HashSet<StudentCourse>(0);
	
	@OneToMany(mappedBy = "pkey.course")
	private Set<CourseTutorial> tutAssociations = new HashSet<CourseTutorial>(0);
	
	public Course() {
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public COURSE_WORKFLOW getWorkflow() {
		return this.workflow;
	}
	
	public void setWorkflow(COURSE_WORKFLOW wf) {
		this.workflow = wf;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public Set<StudentCourse> getStudAssociations() {
		return studAssociations;
	}

	public void setStudAssociations(Set<StudentCourse> studAssociations) {
		this.studAssociations = studAssociations;
	}
	
	// WHY WASN'T THERE A COURSE SIDE OF THIS IMPLEMENTED?  MAYBE THIS WILL SOLVE PROBLEM? 12/29/2012
	public void addStudentAssociation(StudentCourse enrollment) {
		this.studAssociations.add(enrollment);
	}
	
	public Set<CourseTutorial> getTutAssociations() {
		return tutAssociations; 
	}

	public void setTutAssociations(Set<CourseTutorial> tutAssociations) {
		this.tutAssociations = tutAssociations;
	}

	public void saveTutorialAssociation(CourseTutorial assoc) {
		this.tutAssociations.add(assoc); 
	}
	
	public Tutorial getTutorialByOrderNumber(int orderNum) {
		for(CourseTutorial ct : this.tutAssociations)
			if(ct.getOrder() == orderNum) return ct.getTutorial();
		return null;
	} 
	
	// Note that if lazy loaded, session must be open
	public Set<Student> getStudents() {
		Set<Student> students = new HashSet<Student>();
		for(StudentCourse sc : studAssociations )
			students.add(sc.getStudent());
		return students;
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
		Course other = (Course) obj;
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