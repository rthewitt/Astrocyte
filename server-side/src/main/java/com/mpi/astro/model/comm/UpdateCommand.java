package com.mpi.astro.model.comm;

import java.util.HashMap;
import java.util.Map;

import com.mpi.astro.util.MyelinAction;

public class UpdateCommand extends BaseCommand implements Command {
	
	protected String commitRef = null;
	protected String studentId = null;
	
	
	public UpdateCommand(String courseName, String commitRef) {
		super(MyelinAction.UPDATE_CLASS, courseName);
		this.commitRef = commitRef;
	}
	
	// remember, prototype will be needed, BRANCH for default or something
	public UpdateCommand(String courseName, String commitRef, String studentId) {
		super(MyelinAction.UPDATE_STUDENT, courseName);
		this.commitRef = commitRef;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
	}

	@Override
	protected Map<String, Object> getContext() {
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("commitRef", commitRef);
		
		if(studentId != null) ctx.put("student", studentId);
		
		ctx.put("prototype", ""); // may be needed for students if PROTO is not a branch
		ctx.put("courseName", this.courseName);
		return ctx;
	}

}
