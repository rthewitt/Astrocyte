package com.mpi.astro.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.model.edu.Student;

@Repository
public class StudentDao {

	// Spring is injecting the JPA wrapper object of a Hibernate session.
	// The PersistenceContext defaults to a shared EntityManager.
	@PersistenceContext
	private EntityManager entityManager;
	
	public Student find(Long id) {
		return entityManager.find(Student.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Student> getStudents() {
		return entityManager.createQuery("select p from Student p").getResultList();
	}
	
	@Transactional
	public Student save(Student student) {
		if (student.getId() == null) {
			entityManager.persist(student);
			return student;
		} else {
			return entityManager.merge(student);
		}		
	}	
	
}
