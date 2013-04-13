package com.mpi.astro.core.model.edu;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.mpi.astro.core.util.AstrocyteConstants.COURSE_WORKFLOW;

// According to StackOverflow I may want to switch to the mapping below if I wish to use the
// base classes in mappings:  http://stackoverflow.com/questions/2912988/persist-collection-of-interface-using-hibernate
//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class BaseCourseInstance implements CourseInstance {
	
	@Id
	@Column(name = "COURSE_UUID", unique = true, nullable = false)
	protected String courseUUID;
	
	@ManyToOne(targetEntity=BaseCourseDefinition.class)
	@JoinColumn(name="COURSE_DEFINITION")
	protected Course syllabus; // consider making this final somehow. (Hibernate in the way)
	
	@Enumerated(EnumType.STRING)
	@Column(name = "WORKFLOW", nullable = false)
	private COURSE_WORKFLOW workflow = COURSE_WORKFLOW.PASSIVE;
	
	@OneToMany(mappedBy = "currentCourse")
	private Set<StudentVM> mappings;
	
	private static final String CANNOT_MODIFY_DEFINITION = "Cannot manipulate a deployed course flow or definition" +
			"\ntry modifying the global definition.";
	
	public String getCourseUUID() {
		return courseUUID;
	}
	
	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}
	
	protected void setSyllabus(Course syllabus) {
		this.syllabus = syllabus;
	}
	
	protected Course getSyllabus() {
		return this.syllabus;
	}
	
	public COURSE_WORKFLOW getWorkflow() {
		return this.workflow;
	}
	
	public void setWorkflow(COURSE_WORKFLOW wf) {
		this.workflow = wf;
	}
	
	public void setName(String name) {
		throw new UnsupportedOperationException(CANNOT_MODIFY_DEFINITION);
	}
	public void addTutorialAssociation(CourseTutorial assoc) {
		throw new UnsupportedOperationException(CANNOT_MODIFY_DEFINITION);
	}
	public void setDescription(String description) {
		throw new UnsupportedOperationException(CANNOT_MODIFY_DEFINITION);
	}
	public void setTutAssociations(Set<CourseTutorial> tutAssociations) {
		throw new UnsupportedOperationException(CANNOT_MODIFY_DEFINITION);
	}
	public Set<StudentVM> getMappings() {
		return this.mappings;
	}
	public void setMappings(Set<StudentVM> mappings) {
		this.mappings = mappings;
	}
}