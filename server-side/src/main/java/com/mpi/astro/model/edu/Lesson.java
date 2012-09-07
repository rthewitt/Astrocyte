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
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "LESSON_ID")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "TUTORIAL_ID")
	private Tutorial tutorial; // I think this is required, although I find it distasteful
	
	@Column(name = "TAG_NUMBER")
	private Integer tagNum; // BASE_TAG_NAME-{tagNum} by convention in repo
	
	@Column(name = "MEDIA_URI")
	private String mediaURI;
	
	public Lesson() {
		
	}
	
	public Lesson(int num, String uri) {
		this.tagNum = num;
		this.mediaURI = uri;
	}
	
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Tutorial getTutorial() {
		return tutorial;
	}
	
	public void setTutorial(Tutorial tutorial) {
		this.tutorial = tutorial;
	}
	
	public Integer getTagNum() {
		return tagNum;
	}
	public void setTagNum(Integer tagNum) {
		this.tagNum = tagNum;
	}
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
		if (tagNum == null) {
			if (other.tagNum != null)
				return false;
		} else if (!tagNum.equals(other.tagNum))
			return false;
		if (mediaURI == null) {
			if (other.mediaURI != null)
				return false;
		} else if (!mediaURI.equals(other.mediaURI))
			return false;

		return true;
	}
	
	
}