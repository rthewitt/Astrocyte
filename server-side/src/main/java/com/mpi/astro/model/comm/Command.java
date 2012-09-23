package com.mpi.astro.model.comm;

// consider fromJsonString(), for MessageConsumer
import com.mpi.astro.service.edu.EduService;

public interface Command {
	
	public String toJsonString();
	
	public void execute();
}