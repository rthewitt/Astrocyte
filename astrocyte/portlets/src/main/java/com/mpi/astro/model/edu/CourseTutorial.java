package com.mpi.astro.model.edu;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.mpi.astro.util.AstrocyteConstants;

/**
 * This exists because we want tutorial order within course
 * to be preserved in the database itself, not hacked into
 * the code.  This is especially true as we're gong to opt
 * for Set over List for uniqueness constraint.
 * 
 * @author Ryan Hewitt
 */

@Entity
@Table(name = "COURSE_TUTORIAL")
@AssociationOverrides({
	@AssociationOverride(name = "pkey.course",
			joinColumns = @JoinColumn(name = "COURSE_ID")),
	@AssociationOverride(name = "pkey.tutorial",
			joinColumns = @JoinColumn(name = "TUTORIAL_ID"))
})
public class CourseTutorial implements Serializable {

	private static final long serialVersionUID = 6346055690795851915L;

	@EmbeddedId
	private CourseTutorialId pkey = new CourseTutorialId();
	
	// TODO make this unique to avoid multiple 0 entries
	// value of this will come from Syllabus List order during
	// construction
	@Column(name = "T_ORDER")
	private Integer order = AstrocyteConstants.INITIAL;
	
	public CourseTutorial() {}
	
	/*
	public CourseTutorial(Tutorial tutorial, Course course) {
		this.setTutorial(tutorial);
		this.setCourse(course);
	} */
	
	public CourseTutorialId getPkey() {
		return pkey;
	}
	
	public void setPkey(CourseTutorialId pk) {
		this.pkey = pk;
	}
	
	@Transient
	public Tutorial getTutorial() {
		return getPkey().getTutorial();
	}
	
	public void setTutorial(Tutorial tutorial) {
		getPkey().setTutorial(tutorial);
	}
	
	@Transient
	public Course getCourse() {
		return getPkey().getCourse();
	}
	
	public void setCourse(Course course) {
		getPkey().setCourse(course);
	}
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		
		CourseTutorial other = (CourseTutorial) o;
		
		if(getPkey() != null ? !getPkey().equals(other.getPkey())
				: other.getPkey() != null)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return (getPkey() != null ? getPkey().hashCode() : 0);
	}
	
}