package com.mpi.astro.core.model.vm;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.mpi.astro.core.model.edu.StudentCourse;
import com.mpi.astro.core.model.edu.StudentVM;

/**
 *  I will almost certainly have to refactor this into a hierarchy
 */
@Entity
@Table( name = "VM" )
public class VM implements Serializable {
//	private String instanceId; // Would be provider specific
	@Id
	@Column(name = "VM_HOST", unique = true, nullable = false)
	private String hostName;
	
	@Enumerated(EnumType.STRING)
	@Column (name = "VM_TYPE")
	private VMType type;
	
	@Column (name = "VM_PRIVATE_IP")
	private String privateIP;
	
	// will allow multiple when I use a single shared machine with PASSIVE workflow, read-only.
	@OneToMany(mappedBy = "vPk.machine")
	private Set<StudentVM> studAssociations = new HashSet<StudentVM>(0);
	
	public VM() {}
	
	public VM(String hostName, String ip, VMType type) {
		this.hostName = hostName;
		this.type = type;
		this.privateIP = ip;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((privateIP == null) ? 0 : privateIP.hashCode());
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
		VM other = (VM) obj;
		if (hostName == null) {
			if (other.hostName != null)
				return false;
		} else if (!hostName.equals(other.hostName))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (privateIP == null) {
			if (other.privateIP != null)
				return false;
		} else if (!privateIP.equals(other.privateIP))
			return false;
		return true;
	}
	
	public Set<StudentVM> getStudAssociations() {
		return studAssociations;
	}
	public void setStudAssociations(Set<StudentVM> studAssociations) {
		this.studAssociations = studAssociations;
	}
	public void addStudentAssociation(StudentVM mapping) {
		this.studAssociations.add(mapping);
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public VMType getType() {
		return type;
	}

	public void setType(VMType type) {
		this.type = type;
	}

	public String getPrivateIP() {
		return privateIP;
	}

	public void setPrivateIP(String privateIP) {
		this.privateIP = privateIP;
	}
	
}