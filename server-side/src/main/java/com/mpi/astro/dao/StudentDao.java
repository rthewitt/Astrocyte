package com.mpi.astro.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.model.edu.Student;

@Repository
public class StudentDao {
	
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
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	/* Was an attempt to solve lazy loading problem, but session was closed.
	public Student find(Long id, boolean eager) {
		Student s = entityManager.find(Student.class, id); 
		if(eager) Hibernate.initialize(s.getCourses());
		return s;
	}*/
	
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
