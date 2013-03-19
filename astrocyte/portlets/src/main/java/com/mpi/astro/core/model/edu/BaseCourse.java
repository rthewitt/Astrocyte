package com.mpi.astro.core.model.edu;

import java.util.Set;

// Yes, this feels important.  I should probably revisit my inheritance tree.
abstract class BaseCourse implements CourseInstance {
	
	private static final String CANNOT_MODIFY_DEFINITION = "Cannot manipulate a deployed course flow or definition" +
			"\ntry modifying the global definition.";
	
	
	abstract public String getCourseUUID();
	
	public void setName(String name) {
		throw new UnsupportedOperationException(CANNOT_MODIFY_DEFINITION);
	}
	public void addTutorialAssociation(CourseTutorial assoc) {
		throw new UnsupportedOperationException(CANNOT_MODIFY_DEFINITION);
	}
	public void setDescription(String description) {
		throw new UnsupportedOperationException(CANNOT_MODIFY_DEFINITION);
	}
	public void setTutAssociations(Set<CourseTutorial> tutAssociations) {
		throw new UnsupportedOperationException(CANNOT_MODIFY_DEFINITION);
	}
}