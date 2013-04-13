package com.mpi.astro.core.model.edu;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.mpi.astro.core.model.vm.VM;

/**
 * This may be overkill.  I could technically have used foreign key
 * or even a simple join table.  However I may need to add data to
 * the association, such as credentials or runtime information.
 * 
 * Also, it probably doesn't hurt to have a many to many if multiple enroll.
 *
 */
@Entity
@Table(name = "STUDENT_VM")
@AssociationOverrides({
	@AssociationOverride(name = "vPk.student",
			joinColumns = @JoinColumn(name = "STUDENT_ID")),
	@AssociationOverride(name = "vPk.machine",
			joinColumns = @JoinColumn(name = "VM_HOST"))
})
public class StudentVM implements Serializable {
	
	private static final long serialVersionUID = -8885837007172419489L;

	@EmbeddedId
	private StudentVMId vPk = new StudentVMId();
	
	@Temporal(TemporalType.DATE)
	@Column(name = "ASSOC_DATE", nullable=false, length = 10)
	private Date assocDate;
	
	@ManyToOne(targetEntity=BaseCourseInstance.class)	
	@JoinColumn(name = "CURRENT_COURSE")
	private CourseInstance currentCourse;
	
	public StudentVM(){}
	
	public StudentVMId getVPk() {
		return this.vPk;
	}
	
	public void setVPk(StudentVMId vPk) {
		this.vPk = vPk;
	}
	
	public CourseInstance getCurrentCourse() {
		return this.currentCourse;
	}
	
	public void setCurrentCourse(CourseInstance instance) {
		this.currentCourse = instance;
	}
	
	@Transient
	public Student getStudent() {
		return getVPk().getStudent();
	}
	
	public void setStudent(Student student) {
		getVPk().setStudent(student);
	}
	
	@Transient
	public VM getVM() {
		return getVPk().getMachine();
	}
	
	public void setVM(VM machine) {
		getVPk().setMachine(machine);
	}
	
	public Date getAssocDate() {
		return this.assocDate;
	}
	
	public void setEnrollDate(Date assocDate) {
		this.assocDate = assocDate;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		
		StudentVM other = (StudentVM) o;
		
		if(getVPk() != null ? !getVPk().equals(other.getVPk())
				: other.getVPk() != null)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return (getVPk() != null ? getVPk().hashCode() : 0);
	}
	
}