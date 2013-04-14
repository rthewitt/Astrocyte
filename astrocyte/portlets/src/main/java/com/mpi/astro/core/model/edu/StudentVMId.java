package com.mpi.astro.core.model.edu;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import com.mpi.astro.core.model.vm.VM;

@Embeddable
public class StudentVMId implements Serializable {
	
	private static final long serialVersionUID = 3353897806255331746L;
	
	@ManyToOne
	private Student student;
	@ManyToOne
	private VM machine;
	
	public StudentVMId(){}
	
	public Student getStudent() {
		return student;
	}
	
	public void setStudent(Student student) {
		this.student = student;
	}
	
	public VM getMachine() {
		return this.machine;
	}
	
	public void setMachine(VM machine) {
		this.machine = machine;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		StudentVMId other = (StudentVMId) o;
		
		if(student != null ? !student.equals(other.student) : other.student != null) return false;
		if(machine != null ? !machine.equals(other.machine) : other.machine != null) return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int result;
		result = (student != null ? student.hashCode() : 0);
		result = 31 * result + (machine != null ? machine.hashCode() : 0);
		return result;
	}
	
}
