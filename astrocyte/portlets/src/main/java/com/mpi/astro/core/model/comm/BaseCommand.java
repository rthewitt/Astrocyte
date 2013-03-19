package com.mpi.astro.core.model.comm;

import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpi.astro.core.service.edu.EduService;
import com.mpi.astro.core.util.MyelinAction;

public abstract class BaseCommand implements Command {
	
	protected static final Logger logger = LoggerFactory.getLogger(BaseCommand.class);
	
	protected static MyelinAction action;
	
	protected String courseUUID;
	
	protected EduService eduService;
	
	protected BaseCommand() {}
	
	public void setServiceReference(EduService service) {
		this.eduService = service;
	}
	
	// some commands, like update, will differ only in this regard
	protected BaseCommand(MyelinAction action, String courseUUID) {
		this.action = action;
		this.courseUUID = courseUUID;
	}
	
	protected static MyelinAction getAction() {
		return action;
	}
	
	protected abstract Map<String, Object> getContext();

	@Override
	public String toJsonString() {
		JSONObject obj = new JSONObject();
		obj.put("command", action.toString());
		
		obj.put("context", getContext());
		
		return obj.toJSONString();
	}

}
