package com.mpi.astro.core.model.edu;

import java.util.Set;

import com.mpi.astro.core.util.AstrocyteConstants.COURSE_WORKFLOW;

public interface Course {
	public Long getId();

	public void setId(Long id);

	public String getName();

	public void setName(String name);
	
	public COURSE_WORKFLOW getWorkflow();
	
	public void setWorkflow(COURSE_WORKFLOW wf);

	public String getDescription();

	public void setDescription(String description);
	
	public Set<CourseTutorial> getTutAssociations();

	public void setTutAssociations(Set<CourseTutorial> tutAssociations);

	public void addTutorialAssociation(CourseTutorial assoc);
	
	public Tutorial getTutorialByOrderNumber(int orderNum);
}
