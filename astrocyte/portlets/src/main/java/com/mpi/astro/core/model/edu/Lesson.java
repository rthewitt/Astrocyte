package com.mpi.astro.core.model.edu;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

import org.hibernate.annotations.Parent;

/**
 * 
 * @author Ryan Hewitt
 * 
 */
@Embeddable
public class Lesson implements Serializable {
	
	private static final long serialVersionUID = 6602616479188456171L;

	@Parent
	private Tutorial tutorial;
	
	@Column(name = "CLIENT_JSON")
	@Lob
	private String clientJSON = "{}";
	
	public Lesson() {
	}
	
	public Lesson(String jsonDescription) {
		this.clientJSON = jsonDescription;
	}
	
	public Tutorial getTutorial() {
		return tutorial;
	}
	
	public void setTutorial(Tutorial tutorial) {
		this.tutorial = tutorial;
	}

	/*
	 * This will be sent to the client for each lesson,
	 * and will describe media placements in the code 
	 */
	public String getClientJSON() {
		return clientJSON;
	}
	public void setClientJSON(String clientJSON) {
		this.clientJSON = clientJSON;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientJSON == null) ? 0 : clientJSON.hashCode());
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
		Lesson other = (Lesson) obj;
		if (clientJSON == null) {
			if (other.clientJSON != null)
				return false;
		} else if (!clientJSON.equals(other.clientJSON))
			return false;

		return true;
	}
	
	
}