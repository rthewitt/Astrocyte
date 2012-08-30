package com.mpi.astro.service.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;



public class JmsMessageProducer {

    private static final Logger logger = Logger.getLogger(JmsMessageProducer.class);

    protected static final String MESSAGE_COUNT = "messageCount";

    private JmsTemplate template = null;
    
    
    public JmsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	private int messageCount = 10;

	/**
	 * Send a JSON formatted command to Myelin Hub
	 */
    public void sendCommand(final String json) {
    	
    	template.send(new MessageCreator() {
        	@Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(json);
                
                // TODO change
                logger.info("Sending message: " + json);
                
                return message;
            }
        });
    }

}