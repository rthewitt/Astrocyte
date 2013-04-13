package com.mpi.astro.core.model.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mpi.astro.core.model.vm.VMRole;
import com.mpi.astro.core.util.MyelinAction;

public class ProvisionVMCommand extends BaseCommand implements Command {
	
	protected List<String> studentIds = new ArrayList<String>();
	protected String initRef = null;
	protected String token = null; // used as idempotent token for AWS request
	protected VMRole imageType = VMRole.STUDENT;
	
	// remember, prototype will be needed, BRANCH for default or something
	public ProvisionVMCommand(String courseUUID, String initRef, List<String> studentIds, String token) {
		super(MyelinAction.PROVISION_VM, courseUUID);
		this.initRef = initRef;
		this.studentIds = studentIds; 
		this.token = token;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
	}

	@Override
	protected Map<String, Object> getContext() {
		Map<String, Object> ctx = new HashMap<String, Object>();
		if(studentIds.size() < 1 || initRef == null || token == null) {
			logger.error("ProvisionVMCommand cannot accept null values.");
			return null; // TODO handle appropriately
		}
		ctx.put("initRef", initRef);
		ctx.put("token", token);
		
		if(studentIds != null) ctx.put("studentIds", studentIds);
		
		ctx.put("prototype", ""); // will be used for one off tuts and authorship
		ctx.put("courseUUID", this.courseUUID);
		ctx.put("type", imageType.toString());
		return ctx;
	}

}
