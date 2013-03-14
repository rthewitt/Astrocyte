package com.mpi.astro.model.admin;

/**
 * 
 * @author Ryan Hewitt
 * Essentially just for intermediate tutorial creation
 */
public class TutorialViewDescriptor {
	private String name;
	private String description;
	private String descriptionFile;
	private String gitRepo;
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	public void setDescriptionFile(String file) {
		this.descriptionFile = file;
	}
	public String getDescriptionFile() {
		return this.descriptionFile;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public void setGitRepo(String gitRepo) {
		this.gitRepo = gitRepo;
	}
	public String getGitRepo() {
		return this.gitRepo;
	}
}