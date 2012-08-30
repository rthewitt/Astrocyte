package com.mpi.astro.service.edu;

import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.model.edu.Tutorial;
import com.mpi.astro.service.jms.JmsMessageProducer;
import com.mpi.astro.util.AstrocyteUtils;
import com.mpi.astro.util.MyelinAction;

@Service
public class MyelinService {
	
	private static final Logger logger = LoggerFactory.getLogger(MyelinService.class);
	
	@Autowired
	private JmsMessageProducer jmsDispatcher;
	
	public void dispatchInit(Course c, Tutorial tut, List<Student> students) {
		JSONObject payload = AstrocyteUtils.getJSONCourseInit(c.getName(), 
				students, tut.getPrototype());
		
		dispatchRequest(MyelinAction.INITIALIZE, payload);
	}
	
	public void dispatchMergeRequest(Course c, String prototypeURI, String commitRef) {
		
		dispatchRequest(MyelinAction.MERGE_REQUEST, 
				AstrocyteUtils.getJSONCourseMerge(c.getName(), prototypeURI, commitRef));
		
	}
	
	public void dispatchRequest(MyelinAction potential, JSONObject data) {
		
		JSONObject command = new JSONObject();
		command.put("command", potential.toString());
		command.put("context", data);
		
		
		logger.info("Sending request for action: " + potential.toString());
		jmsDispatcher.sendCommand(command.toJSONString());
	}
	
}