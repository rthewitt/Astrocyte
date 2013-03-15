package com.mpi.astro.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentStatus;

@Repository
public class CourseDao {

	@PersistenceContext
	public EntityManager entityManager;
	
	public Course find(Long id) {
		return entityManager.find(Course.class, id);
	}
	
	public Course findByName(String courseName) {
		return (Course)entityManager.createQuery("select c from Course c where c.name = :name")
		.setParameter("name", courseName).getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Course> getCourses() {
		return entityManager.createQuery("select c from Course c").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Student> getStudentsForCourse(Long id) {
		
		return (List<Student>) entityManager.createQuery("select s " +
				"from Student s join s.courseAssociations sc where sc.pk.course.id = :c_id")
			.setParameter("c_id", id).getResultList();
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
