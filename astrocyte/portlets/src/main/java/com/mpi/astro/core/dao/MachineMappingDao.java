package com.mpi.astro.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentVM;

@Repository
public class MachineMappingDao {
	
	@PersistenceContext
	public EntityManager entityManager;
	
	// long should no longer be used here
	public StudentVM find(Long id) {
		return entityManager.find(StudentVM.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<StudentVM> getAllMappings() {
		return entityManager.createQuery("select vmp from StudentVM vmp").getResultList();
	}
	
	public StudentVM getMapping(Student student, CourseInstance course) {
		return (StudentVM)entityManager.createQuery("select vmp from StudentVM vmp " +
				"where vmp.vPk.student = :student and vmp.vPk.machine.currentCourse = :course")
				.setParameter("student", student).setParameter("course", course).getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<StudentVM> getMappingsForStudent(Student student) {
		return (List<StudentVM>)entityManager.createQuery("select vmp from StudentVM vmp " +
				"where vmp.vPk.student = :student").setParameter("student", student).getResultList();
	}
	
	@Transactional
	public StudentVM save(StudentVM mapping) {
		if (mapping.getVPk().getMachine().getHostName() == null || mapping.getVPk().getStudent().getId() == null) {
			entityManager.persist(mapping);
			return mapping;
		} else {
			return entityManager.merge(mapping);
		}		
	}	
}