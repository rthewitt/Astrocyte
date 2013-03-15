package com.mpi.astro.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.Tutorial;

@Repository
public class TutorialDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Tutorial find(Long id) {
		return entityManager.find(Tutorial.class, id);
	}
	
	public Tutorial getWithLessons(Long id) {
		Tutorial tut = find(id);
		// TODO write a join query for practice
		if(tut.getLessons().size() > 0) tut.getLessons().get(0).getClientJSON();
		return tut;
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
	
	public void clearForTest(){
		entityManager.clear();
	}
	
}
