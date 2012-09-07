package com.mpi.astro.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.model.edu.Tutorial;

@Repository
public class TutorialDao {

	// This is a hack to keep lazy loading from causing a failure on collections
	// Needed because the app is not container managed.  TODO determine CONS
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	public Tutorial find(Long id) {
		return entityManager.find(Tutorial.class, id);
	}
	
	public Tutorial findInitialized(Long id) {
		Tutorial t = entityManager.find(Tutorial.class, id);
//		Hibernate.initialize(t.getLessons());
		return t;
	}
	
	@SuppressWarnings("unchecked")
	public List<Tutorial> getTutorials() {
		return entityManager.createQuery("select t from Tutorial t").getResultList();
	}
	
	@Transactional
	public Tutorial save(Tutorial tutorial) {
		if (tutorial.getId() == null) {
			entityManager.persist(tutorial);
			return tutorial;
		} else {
			return entityManager.merge(tutorial);
		}		
	}	
	
}
