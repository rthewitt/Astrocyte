package com.mpi.astro.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.Tutorial;
import com.mpi.astro.core.model.vm.VM;

@Repository
public class VMDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	public VM find(String id) {
		return entityManager.find(VM.class, id);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<VM> getVirtualMachines() {
		return entityManager.createQuery("select vm from VM vm").getResultList();
	}
	
	@Transactional
	public VM save(VM machine) {
		if (machine.getHostName() == null) {
			entityManager.persist(machine);
			return machine;
		} else {
			return entityManager.merge(machine);
		}		
	}	
}