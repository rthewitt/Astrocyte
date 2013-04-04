package com.mpi.astro.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.Student;

@Repository
public class CourseInstanceDao {

	@PersistenceContext
	public EntityManager entityManager;
	
	public CourseInstance find(String courseUUID) {
		return (CourseInstance)entityManager.createQuery("select c from com.mpi.astro.core.model.edu.CourseInstance c where c.courseUUID = :uuid")
		.setParameter("uuid", courseUUID).getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<CourseInstance> getCourses() {
		return entityManager.createQuery("select c from com.mpi.astro.core.model.edu.CourseInstance c").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	public List<Student> getStudentsForCourseById(Long id) {
		
		return (List<Student>) entityManager.createQuery("select s " +
				"from Student s join s.courseAssociations sc where sc.pk.course.id = :c_id")
			.setParameter("c_id", id).getResultList();
	}
	
	// Consider performance on proxy object collections
	@SuppressWarnings("unchecked")
	public List<Student> getStudentsForCourseByUUID(String uuid) {
		
		return (List<Student>) entityManager.createQuery("select s " +
				"from Student s join s.courseAssociations sc where sc.pk.course.courseUUID = :c_uuid")
			.setParameter("c_uuid", uuid).getResultList();
	}
	
	@Transactional
	public CourseInstance save(CourseInstance course) {
		if (course.getCourseUUID() == null) {
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