package com.mpi.astro.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.CourseTutorial;

@Repository
public class CourseTutorialDao {

	@PersistenceContext
	public EntityManager entityManager;
	
	public CourseTutorial find(Long id) {
		return entityManager.find(CourseTutorial.class, id);
	}
	
	@Transactional
	public CourseTutorial save(CourseTutorial association) {
		if (association.getCourse().getId() == null || association.getTutorial().getId() == null) {
			entityManager.persist(association);
			return association;
		} else {
			return entityManager.merge(association);
		}		
	}	
	
	public void clearForTest(){
		entityManager.clear();
	}
}