package com.mpi.astro.model.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpi.astro.model.edu.Tutorial;

abstract class AbstractTutorialBuilder implements TutorialBuilder {
	
	protected static final Logger logger = LoggerFactory.getLogger(AbstractTutorialBuilder.class);
	
	protected Tutorial tutorial;

	@Override
	public void createTutorial() {
		this.tutorial = new Tutorial();
	}
	
	@Override
	public void createTutorial(String name) {
		createTutorial();
		this.tutorial.setName(name);
	}
	
	public Tutorial getTutorial() {
		return tutorial;
	}

	@Override
	public abstract void buildProjectDefinition(String project);

	@Override
	public abstract void buildLessons(String lessonFile);
	
}