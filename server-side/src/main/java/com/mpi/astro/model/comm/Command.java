package com.mpi.astro.model.comm;

// consider fromJsonString(), for MessageConsumer
import java.util.Map;

public interface Command {
	
	public String toJsonString();
	
	public void execute();
}