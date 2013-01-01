package com.mpi.astro.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.model.edu.StudentCourse;

@Repository
public class StudentCourseDao {
	
	@PersistenceContext
	public EntityManager entityManager;
	
	public StudentCourse find(Long id) {
		return entityManager.find(StudentCourse.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<StudentCourse> getStudents() {
		return entityManager.createQuery("select p from StudentCourse p").getResultList();
	}
	
	@Transactional
	public StudentCourse save(StudentCourse enrollment) {
		if (enrollment.getPk().getCourse().getId() == null || enrollment.getPk().getStudent().getId() == null) {
			entityManager.persist(enrollment);
			return enrollment;
		} else {
			return entityManager.merge(enrollment);
		}		
	}	
}
