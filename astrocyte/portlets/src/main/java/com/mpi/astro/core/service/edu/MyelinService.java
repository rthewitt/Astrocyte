package com.mpi.astro.core.service.edu;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpi.astro.core.model.comm.BaseCommand;
import com.mpi.astro.core.model.comm.Command;
import com.mpi.astro.core.model.comm.InitCommand;
import com.mpi.astro.core.model.comm.UpdateCommand;
import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.Tutorial;
import com.mpi.astro.core.service.jms.JmsMessageProducer;
import com.mpi.astro.core.util.AstrocyteUtils;
import com.mpi.astro.core.util.MyelinAction;

@Service
public class MyelinService {
	
	private static final Logger logger = LoggerFactory.getLogger(MyelinService.class);
	
	@Autowired
	private JmsMessageProducer jmsDispatcher;
	
	/*
	 The methods below accept Course interface, which is not necessarily correct.
	 However it is technically correct because CourseImpl implements CourseInstance
	 returns the UUID as the name.  Consider the prolonged use cases for these interfaces.
	*/
	
	public void dispatchInit(Course c, Tutorial tut, Set<Student> students) {
		Command com = new InitCommand(c.getName(), tut.getPrototype(), students);
		dispatchCommand(com);
	}
	
	public void requestClassMerge(Course c, String prototypeURI, String commitRef) {
		Command com = new UpdateCommand(c.getName(), commitRef);
		dispatchCommand(com);
	}
	
	public void requestStudentMerge(Course c, String prototypeURI, String commitRef, String studentId) {
		Command com = new UpdateCommand(c.getName(), commitRef, studentId);
		dispatchCommand(com);
	}
	
	private void dispatchCommand(Command com) {
		jmsDispatcher.sendCommand(com.toJsonString());
	}
	
	public void dispatchRequest(MyelinAction potential, JSONObject data) {
		
		JSONObject command = new JSONObject();
		command.put("command", potential.toString());
		command.put("context", data);
		
		
		logger.info("Sending request for action: " + potential.toString());
		jmsDispatcher.sendCommand(command.toJSONString());
	}
	
	public Command commandFromJson(String jsonStr, EduService serviceRef) {
		Command command = null;
		MyelinAction type = null;
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(jsonStr);
			type = MyelinAction.valueOf((String)json.get("command"));
			Map<String, Object> ctx = (Map<String, Object>) json.get("context");
			Constructor<?> ctor = AstrocyteUtils.getCommandClass(type).getConstructor(Map.class);
			
			try {
				command = (Command)ctor.newInstance(ctx);
				((BaseCommand)command).setServiceReference(serviceRef);
			} catch (Exception e) {
				logger.error("Problem trying to convert to command type " + type.toString(), e);
			}
		} catch (ParseException e) {
			logger.error("Error parsing command " + jsonStr, e);
			return null;
		} catch(NoSuchMethodException nme) {
			String append = type != null ? "command type: " + type.toString() : "unknown type";
			logger.error("Constructor not found for " + append);
			return null;
		}
		
		return command;
	}
	
}
