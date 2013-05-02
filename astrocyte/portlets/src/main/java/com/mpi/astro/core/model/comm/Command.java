package com.mpi.astro.core.model.comm;

// consider fromJsonString(), for MessageConsumer
import com.mpi.astro.core.service.edu.EduService;

public interface Command {
	
	public String toJsonString();
	
	public void execute();
	
	public void setAsTest();
}