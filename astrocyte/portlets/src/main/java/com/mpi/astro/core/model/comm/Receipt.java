package com.mpi.astro.core.model.comm;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentStatus;
import com.mpi.astro.core.service.edu.MyelinService;
import com.mpi.astro.core.util.AstrocyteConstants;
import com.mpi.astro.core.util.AstrocyteUtils;
import com.mpi.astro.core.util.MyelinAction;
import com.mpi.astro.core.util.AstrocyteConstants.COURSE_WORKFLOW;
import com.mpi.astro.core.util.AstrocyteConstants.STUDENT_STATE;

public class Receipt extends BaseCommand implements Command {
	
	protected MyelinAction originalCommand = null;
	protected String commandId = null; // may use for commands in which courseUUID is insufficient for idempotency
	protected boolean status = false; // fallback
	protected String message = "";
	
	public Receipt(Map<String, String> context) {
		super(MyelinAction.RECEIPT, context.get("courseUUID"));
		this.originalCommand = MyelinAction.valueOf(context.get("type"));
//		status = Boolean.valueOf(context.get("status"));
		this.status = "success".equals(context.get("status").toLowerCase()) ? true : false;
		if(!status && context.containsKey("message"))
			this.message = context.get("message");
	}

	/*
	 * Currently co-exists with makeshift receipt workflow of Advance
	 * TODO migrate over if necessary
	 */
	@Transactional
	@Override
	public void execute() {
		String logtxt = String.format("Receipt of command %s with id: %s\nstatus: %s\n%s", 
				this.originalCommand, 
				(this.commandId == null ? "NONE" : this.commandId), 
				this.status, this.message);
		if(!this.status) {
			logger.error(logtxt);
			return;
			
		} else logger.info(logtxt);
		
		switch(this.originalCommand) {
		case INITIALIZE:
			eduService.initializeStudentStatuses(courseUUID);
		}
	}

	@Override
	protected Map<String, Object> getContext() {
		// TODO Auto-generated method stub
		return null;
	}

}