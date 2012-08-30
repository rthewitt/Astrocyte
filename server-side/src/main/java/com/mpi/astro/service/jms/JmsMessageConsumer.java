package com.mpi.astro.service.jms;

import javax.jms.TextMessage;

import org.apache.log4j.Logger;

public class JmsMessageConsumer {

	private static final Logger logger = Logger.getLogger(JmsMessageConsumer.class);
	
	
	public String handleCommand(TextMessage message) {
		try {
			logger.info("Received message: " + message.getText());
			Thread.sleep(3000); // instead of sleeping, actually translate and handle the JSON
			return "processed: " + message.getText();
		} catch(Exception e) {
			return "error: handle"; // Actually, JSON based error could map to a command
		}
	}
	
}