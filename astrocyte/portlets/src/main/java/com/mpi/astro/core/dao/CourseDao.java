package com.mpi.astro.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.BaseCourseDefinition;
import com.mpi.astro.core.model.edu.Course;

@Repository
public class CourseDao {

	@PersistenceContext
	public EntityManager entityManager;
	
	public Course find(Long id) {
		return (Course)entityManager.find(BaseCourseDefinition.class, id);
//		return entityManager.find(Course.class, id);
	}
	
	public Course findByName(String courseName) {
		return (Course)entityManager.createQuery("select c from com.mpi.astro.core.model.edu.Course c where c.name = :name")
		.setParameter("name", courseName).getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Course> getCourses() {
		return entityManager.createQuery("select c from com.mpi.astro.core.model.edu.Course c").getResultList();
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
	
	public void clearForTest(){
		entityManager.clear();
	}
}
