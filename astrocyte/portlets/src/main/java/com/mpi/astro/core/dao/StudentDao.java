package com.mpi.astro.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.Student;

@Repository
public class StudentDao {
	
	@PersistenceContext
	public EntityManager entityManager;
	
	public Student find(Long id) {
		return entityManager.find(Student.class, id);
	}
	
	// Hibernate.initialize is not appropriate for collections
	@Transactional
	public Student getStudentInitialized(long id) {
		Student student = find(id);
		student.getCourses();
		return student;
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
	
	public List<Student> findAll() {
		return entityManager.createNamedQuery(Student.SQL_FIND_ALL, Student.class).getResultList();
	}
	
	public void clearForTest(){
		entityManager.clear();
	}
}
