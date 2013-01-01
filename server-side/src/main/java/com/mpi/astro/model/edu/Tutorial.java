package com.mpi.astro.model.edu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Tutorial has changed once again, as a stack was an unnecessary duplication.
 * Courses contain a persistent list of tutorials.
 */
@Entity
@Table(name="TUTORIAL")
public class Tutorial implements Serializable {
	
	private static final Logger logger = LoggerFactory.getLogger(Tutorial.class);
	
	private static final long serialVersionUID = 5418688806412246017L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="TUTORIAL_ID")
	private Long id;
	
	@Column(name="TUTORIAL_NAME", nullable=false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name="TYPE")
	private TutorialType type = TutorialType.SIMPLE;
	
	@Column(name="PROTO_URI")
	private String prototype;
	
	@OneToMany(mappedBy="tutorial")
	@OrderBy("id")
	private List<Lesson> lessons = new ArrayList<Lesson>(0);
	
	@OneToMany(mappedBy = "pkey.tutorial")
	private Set<CourseTutorial> courseAssociations = new HashSet<CourseTutorial>(0);
	
	@Column(name="DESCRIPTION")
	private String description;
	
	public Tutorial() {
	}
	
	public Long getId() {
		return id;
	}
	
	public Set<CourseTutorial> getCourseAssociations() {
		return courseAssociations;
	}

	public void setCourseAssociations(Set<CourseTutorial> courseAssociations) {
		this.courseAssociations = courseAssociations;
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
	
	public List<Lesson> getLessons() {
		return lessons;
	}
	
	public void setLessons(List<Lesson> lessons) {
		for(Lesson lesson : lessons) {
			lesson.setTutorial(this);
		}
		this.lessons = lessons;
	}
	
	// hardly necessary...
	public void addLesson(int tagIndex, String mediaURI) {
		Lesson lesson = new Lesson(tagIndex, mediaURI);
		this.lessons.add(lesson);
		logger.debug("INSIDE TUTORIAL addLesson: size is now " + this.lessons.size());
		lesson.setTutorial(this); // this is dumb
	}
	
	public int getNumSteps() {
		return lessons.size();
	}
	
	/**
	 *  Returns an immutable Map of tag number -> media uri
	 *  used as convenience for course design and representation.
	 */
	public Map<Integer, String> getLessonMappings() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		for(Lesson l : lessons)
			map.put(l.getId(), l.getMediaURI());
		return Collections.unmodifiableMap(map);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lessons == null) ? 0 : lessons.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((prototype == null) ? 0 : prototype.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lessons == null) {
			if (other.lessons != null)
				return false;
		} else if (!lessons.equals(other.lessons))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (prototype == null) {
			if (other.prototype != null)
				return false;
		} else if (!prototype.equals(other.prototype))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
}