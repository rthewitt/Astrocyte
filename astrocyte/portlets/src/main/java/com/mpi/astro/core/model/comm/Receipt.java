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
	
	Map<String, Object> fullContext = null;
	
	public Receipt(Map<String, Object> context) {
		super(MyelinAction.RECEIPT, (String)context.get("courseUUID"));
		this.originalCommand = MyelinAction.valueOf((String)context.get("type"));
//		status = Boolean.valueOf(context.get("status"));
		this.status = "success".equals(((String)context.get("status")).toLowerCase()) ? true : false;
		if(!status && context.containsKey("message"))
			this.message = (String)context.get("message");
		this.fullContext = context;
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
			break;
		case PROVISION_VM:
			try {
				Map<String, Object> instanceMap = (Map<String, Object>)fullContext.get("instanceMap");
				for(String studentId : instanceMap.keySet()) {
					Map<String, String> instanceDesc = (Map<String, String>)instanceMap.get(studentId);
					eduService.associateStudentVM(studentId, instanceDesc.get("host"), instanceDesc.get("location"));
				}
			} catch(Exception e) {
				logger.error("Problem associating student VM", e);
			}
			break;
		}
	}

	@Override
	protected Map<String, Object> getContext() {
		// TODO Auto-generated method stub
		return null;
	}

}