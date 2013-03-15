package com.mpi.astro.core.model.builder;

import com.mpi.astro.core.model.edu.Tutorial;

public interface TutorialBuilder {
	
	public Tutorial getTutorial();
	
	public void createTutorial();
	public void createTutorial(String name);
	
	public void buildProjectDefinition(String project); // from step 1, admin page
	// authoring tool will be stubbed out and file provided during tests / demos
	public void buildLessons(String lessonFile); // lesson definitions from client authoring tool
	
	// TODO consider that tests will be created during authoring
	// For the passive builder, this either has no effect or internal QA.  Stub out for now
	// For interactive, test suite will be integrated into git repository, i.e. into maven directory for Java tuts
}