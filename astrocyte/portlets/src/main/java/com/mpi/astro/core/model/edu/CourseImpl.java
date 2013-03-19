package com.mpi.astro.core.model.edu;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Parent;

import com.mpi.astro.core.util.AstrocyteConstants.COURSE_WORKFLOW;

@Entity
@Table(name="DEPLOYED_COURSE")
public class CourseImpl extends BaseCourse implements CourseInstance, Serializable {
	
	private static final long serialVersionUID = 6094379996028682456L;
	private static final SimpleDateFormat uuidFormat = new SimpleDateFormat("yyMMddHHmmssZ"); 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "DEPLOYED_ID", unique = true, nullable = false)
	private Long id;
	
	// Not an id because technically time zones complicate namespace issues
	@Column(name = "DEPLOYED_UUID", unique = true, nullable = false)
	private String courseUUID;
	
	@Column(name="DEPLOYED_DATE")
	private Date deployedDate;
	
	/**
	 * This is the new method of sharing data.  As each course is an implementation of a Syllabus (using Course interface)
	 * but may have data that the syllabus is not concerned with, I've decided to move in the direction of a shared
	 * prototype between course instances.  Embedded will be the parent object, which contains Course information.
	 * Changing this object will change all live courses which have been deployed. -3/18/2013 -RTH
	 */
	@Parent
	private Course syllabus; // consider making this final somehow. (Hibernate in the way)
	
	@OneToMany(mappedBy = "pk.course")
	private Set<StudentCourse> studAssociations = new HashSet<StudentCourse>(0);
	
	// Is this going to be syllabus or instance based?  I vote instance...
	@Enumerated(EnumType.STRING)
	@Column(name = "WORKFLOW", nullable = false)
	private COURSE_WORKFLOW workflow = COURSE_WORKFLOW.PASSIVE;
	
	private CourseImpl() {
	}
	
	public CourseImpl(Course definition) {
//		this(definition.getName(), definition.getDescription(), definition.getWorkflow());
		this.syllabus = definition;
		this.deployedDate = new Date();
		this.courseUUID = String.format("%s-%s", syllabus.getName(), 
				uuidFormat.format(deployedDate));
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String getCourseUUID() {
		return courseUUID;
	}
	
	public void setDeployedDate(Date d) {
		this.deployedDate = d;
	}
	
	public Date getDeployedDate() {
		return this.deployedDate;
	}

	public String getName() {
		return this.courseUUID;
	}
	
	public COURSE_WORKFLOW getWorkflow() {
		return this.workflow;
	}
	
	public void setWorkflow(COURSE_WORKFLOW wf) {
		this.workflow = wf;
	}
	
	// ============ Hibernate required methods ==================
	private void setSyllabus(Course syllabus) {
		this.syllabus = syllabus;
	}
	
	private Course getSyllabus() {
		return this.syllabus;
	}
	
	// ===========================================================
	
	public Course getCourseDefinition() {
		if(this.syllabus != null) {
			final Course safe = getSyllabus();
			return safe;
		} else return null;
	}

	public String getDescription() {
		return syllabus.getDescription();
	}
	
	public Set<StudentCourse> getStudAssociations() {
		return studAssociations;
	}

	public void setStudAssociations(Set<StudentCourse> studAssociations) {
		this.studAssociations = studAssociations;
	}
	
	// Any reason to force unmodifiable?
	/* Also, would placing annotations on setters increase performance for getting just the
	   student ids for initialization / generation / future reporting? */
	@SuppressWarnings("unchecked")
	public Set<Student> getStudentsInCourse() {
		Set <Student> ret = new HashSet<Student>();
		for(StudentCourse sc : this.studAssociations)
			ret.add(sc.getStudent());
		return Collections.unmodifiableSet(ret);
	}
	
	// WHY WASN'T THERE A COURSE SIDE OF THIS IMPLEMENTED?  MAYBE THIS WILL SOLVE PROBLEM? 12/29/2012
	public void addStudentAssociation(StudentCourse enrollment) {
		this.studAssociations.add(enrollment);
	}
	
	/* TODO consider implications of mutability.  Perhaps return unmodifiable copy, so that
	 	the tutorials can be modified from Syllabus but not from a deployed instance.
	 	This would indicate that you are NOT changing a single course. */ 
	public Set<CourseTutorial> getTutAssociations() {
//		return Collections.unmodifiableSet(s...)
		return syllabus.getTutAssociations();
	}
	
	public Tutorial getTutorialByOrderNumber(int orderNum) {
		return syllabus.getTutorialByOrderNumber(orderNum);
	} 

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((courseUUID == null) ? 0 : courseUUID.hashCode());
		result = (syllabus ==  null) ? 0 : syllabus.hashCode();
		result = (deployedDate.toString() == null) ? 0 : deployedDate.toString().hashCode();
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
		CourseImpl other = (CourseImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if(syllabus == null) {
			if(other.syllabus != null)
				return false;
		} else if(!syllabus.equals(other.syllabus))
			return false;
		if (courseUUID == null) {
			if (other.courseUUID != null)
				return false;
		} else if (!courseUUID.equals(other.courseUUID))
			return false;
		
		return true;
	}
}