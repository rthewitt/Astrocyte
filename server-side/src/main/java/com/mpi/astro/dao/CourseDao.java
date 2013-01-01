package com.mpi.astro.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.Tutorial;

@Repository
public class CourseDao {

	@PersistenceContext
	public EntityManager entityManager;
	
	public Course find(Long id) {
		return entityManager.find(Course.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Course> getCourses() {
		return entityManager.createQuery("select c from Course c").getResultList();
	}
	
	@Transactional
	public Course save(Course course) {
		if (course.getId() == null) {
			entityManager.persist(course);
			return course;
		} else {
			return entityManager.merge(course);
		}		
	}	
	
}
