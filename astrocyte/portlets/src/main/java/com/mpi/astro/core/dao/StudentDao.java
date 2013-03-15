package com.mpi.astro.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.Student;

@Repository
public class StudentDao {
	
	// TODO delete this silliness when you determine a working strategy.  References EXTENDED context
	/**
	 * The annotation configuration below is based on my current lack of understanding of managed vs unmanaged
	 *  environments.  I do not believe Tomcat is "managed", transactionally speaking, but I keep reading that Spring is.
	 *  In any case, without external management, the entityManager defaults to the current or shared instance, and failing that,
	 *  one is instantiated in an attempt to make entityManager thread-safe.
	 *  
	 *  I've been trying to avoid using eager loading for obvious reasons.  
	 *  My first attempt was to try to add a spring interceptor
	 *  
	 *  {@link org.springframework.orm.hibernate3.support.OpenSessionInViewFilter} 
	 *  
	 *  which, if I were using pure hibernate, would have opened the transactional session during the processing of the views.
	 *  I then realized I was using JPA, so the correct class would have been:
	 *  
	 *  {@link org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter} 
	 *  
	 *  However, changing the entityManager was another solution proposed and accepted.  See below for details:
	 *  
	 *  http://stackoverflow.com/questions/2547817/what-is-the-difference-between-transaction-scoped-persistence-context-and-extend
	 */
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
	
	// Why would Denis add this? What benefit does it yield?
	// Does not appear to be used, probably for illustration purposes
	public List<Student> findAll() {
		return entityManager.createNamedQuery(Student.SQL_FIND_ALL, Student.class).getResultList();
	}
	
	public void clearForTest(){
		entityManager.clear();
	}
}
