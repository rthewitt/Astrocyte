package com.mpi.astro.model.edu;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Ryan Hewitt
 * 
 * Lesson will be a node in the Tutorial stack.
 * Provides the location of the media that accompanies
 * the current state of the code.
 */
@Entity
@Table( name="LESSON" )
public class Lesson implements Serializable {
	private static final long serialVersionUID = -4962190931295411178L;
	
	@Id
	@Column(name = "LESSON_ID")
	private Integer id; // TODO make this a composite?  Something like course_name+id or course_id-id
	
	@ManyToOne
	@JoinColumn(name = "TUTORIAL_ID")
	private Tutorial tutorial; // I think this is required, although I find it distasteful
	
	@Column(name = "MEDIA_URI")
	private String mediaURI;
	
	public Lesson() {
		
	}
	
	public Lesson(int id, String uri) {
		this.id = id;
		this.mediaURI = uri;
	}
	
	
	public Integer getId() {
		return this.id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Tutorial getTutorial() {
		return tutorial;
	}
	
	public void setTutorial(Tutorial tutorial) {
		this.tutorial = tutorial;
	}

	/*
	 * Originally, this URL was going to be the sole media asset for a lesson
	 * Now I suggest that it be a file (which can be auto-generated from Authoring tool)
	 * that specifies main introduction, changed files, and code-highlighted mappings to
	 * content that will be retrieved, possibly by Ajax.  In this way, you can bring the 
	 * tutorial / instruction INTO the IDE.
	 */
	public String getMediaURI() {
		return mediaURI;
	}
	public void setMediaURI(String mediaURI) {
		this.mediaURI = mediaURI;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((mediaURI == null) ? 0 : mediaURI.hashCode());
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
		if (mediaURI == null) {
			if (other.mediaURI != null)
				return false;
		} else if (!mediaURI.equals(other.mediaURI))
			return false;

		return true;
	}
	
	
}