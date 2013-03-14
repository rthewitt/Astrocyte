package com.mpi.astro.service.edu;

import org.apache.activemq.transport.stomp.Stomp.Headers.Subscribe;
import org.apache.activemq.transport.stomp.StompConnection;
import org.apache.activemq.transport.stomp.StompFrame;

import javax.jms.*;

public class JMSListener{
	
	private String type;
	
	private JMSListener() {
		// must pass in type
	}
	
	// TODO use enum
	JMSListener(String type) {
		this.type = type;
	}
	
	public void Listen() {
		/*
		   
	     StompConnection connection = new StompConnection();
	     connection.open("localhost",61613);

	     connection.connect("system", "manager"); // no creds yet

	     connection.subscribe("/queue/test", Subscribe.AckModeValues.CLIENT);
	     
	     connection.begin("tx2");
	     
	     // TODO subscribe to this instead, so that we can log retrieval success
	     StompFrame message = connection.receive();
	     
	     messageReceived = message.getBody();
	     
	     connection.ack(message, "tx2");

	     connection.commit("tx2");

	     connection.disconnect();
	     
	     return messageReceived; */
	}
	
}