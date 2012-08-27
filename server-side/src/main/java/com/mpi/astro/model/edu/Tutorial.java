package com.mpi.astro.model.edu;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * Tutorial will probably be a subclass of something like Activity.
 * Eventually it will contain valuable information, perhaps rev-list checkpoints
 * or tag data on the given repo for traversal.  Ironically, proto design pattern
 * may be a decent choice for repo types.
 */
@Entity
@Table(name="TUTORIAL")
public class Tutorial implements Serializable {
	
	private static final long serialVersionUID = 5418688806412246017L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="TUTORIAL_ID")
	private Long id;
	
	@Column(name="TUTORIAL_NAME", nullable=false)
	private String name;
	
	@Column(name="TYPE")
	private TutorialType type = TutorialType.SIMPLE;
	
	// This may eventually be an object class
	// e.g., create localRepository with JGit.
	@Column(name="PROTO_URI")
	private String prototype;
	
	@Column
	private String description;
	
	public Tutorial() {
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getPrototype() {
		return this.prototype;
	}
	
	public void setPrototype(String prototype) {
		this.prototype = prototype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TutorialType getType() {
		return type;
	}

	public void setType(TutorialType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
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
		Tutorial other = (Tutorial) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (prototype == null) {
			if (other.prototype != null)
				return false;
		} else if (!prototype.equals(other.prototype))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}
	
	
}