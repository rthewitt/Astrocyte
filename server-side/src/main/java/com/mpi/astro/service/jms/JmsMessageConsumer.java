package com.mpi.astro.service.jms;

import java.util.Map;

import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import com.mpi.astro.model.comm.Command;
import com.mpi.astro.model.comm.UpdateCommand;
import com.mpi.astro.service.edu.EduService;
import com.mpi.astro.service.edu.MyelinService;

public class JmsMessageConsumer {

	private static final Logger logger = Logger.getLogger(JmsMessageConsumer.class);
	
	@Autowired
	private EduService eduService;
	
	@Autowired
	private MyelinService myelinService;
	
	
	// TODO understand where the return value would have gone
	public String handleCommand(TextMessage message) {
		try {
			logger.info("Received message: " + message.getText());
			Thread.sleep(3000); // instead of sleeping, actually translate and handle the JSON
			return "processed: " + message.getText();
		} catch(Exception e) {
			return "error: handle"; // Actually, JSON based error could map to a command
		}
	}
	
	
	// We're using a String instead of an actual JMS style message.  Understand the benefit of wrapping
	public void handleCommand(String message) {
		try {
			logger.info("Received message: " + message);
			// create pull request for [human] professor?
			// have professor-bot listening on post-receive or on broadcast?
			// currently simulating professor Cpanel via controller on astrocyte
			// notify panel of student update(s)
			// SIMULATE pull request
			// increment via controller [student/all], commitRef on PROTO
			// will be an input currently (PROTO commit) - but later will pull from database / Tutorial object
			// TODO transition student(s) project state to GRADING or AWAITING_APPROVAL, etc
			
			// ======== Temporary testing ===========
			// going to test using update now, calling with TAG instead of commit-ref
			
			try {
				JSONObject json = (JSONObject)new JSONParser().parse(message);
				if("ADVANCE".equals( (String)json.get("command") )) {
//					eduService.deployLesson(1L, 1L, commitRef)
					eduService.notifyProfessorPullRequest(); // TODO move to execute()
					// assume it's good
				}
			} catch(Exception e) {
				throw new Exception("Problem during test", e);
			}
			
			Command command = myelinService.commandFromJson(message);
			
			// ======================================
			
			if(command == null) throw new Exception("Command could not be resolved from JMS message");
			
			command.execute();
			
		} catch(Exception e) {
			logger.error(e);
		}
	}
	
}