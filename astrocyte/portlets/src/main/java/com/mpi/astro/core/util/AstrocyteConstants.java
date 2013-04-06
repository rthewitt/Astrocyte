package com.mpi.astro.core.util;

public class AstrocyteConstants {
	
	/*
	 *  L-Dopa
	 */
	public static final String ARCADE_URL = "localhost/";
	
	public enum GameType {
		Java,
		PlayN,
		HTML5
	}
	
	public static enum STUDENT_STATE {
		WORKING,
		ADVANCING,
		APPROVAL,
		NOTIFY_STUDENT
	}
	
	public static enum COURSE_WORKFLOW {
		// Student is advanced without question
		// Case 1: Ungraded tutorial without unit-tests
		// Case 2: Non-interactive tutorial
		PASSIVE,
		// Student is manually approved by Teacher / Admin 
		CODE_REVIEW,
		// Will either be handled by VM or build server
		UNIT_TEST
	}
	
	// Used for lesson & tutorial progress initial values
	// may change to -1 if database 0-index is more appropriate
	public static final int NOT_STARTED = 0;
	public static final int INITIAL = 0;
	
	public static final String BASE_TAG="check"; // checkpoint current
	public static final String CHECKPOINT_REGEX = "^"+BASE_TAG+"-\\d*$";

}