package com.mpi.astro.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.model.edu.CourseTutorial;

@Repository
public class CourseTutorialDao {

	@PersistenceContext
	private EntityManager entityManager;
	
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
}