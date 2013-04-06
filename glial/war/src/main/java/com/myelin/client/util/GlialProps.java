package com.myelin.client.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO add default properties
public class GlialProps {
	
	public static String ASTROCYTE_URL = "http://localhost:8080";
	public static String MEDIA_LOCATION = "http://localhost/myelin/";
	public static String WEB_PROJECT = "/home/student/deploy/betaport";
	public static String STUDENT_PROJECT = "/home/student/build/student_project";
	public static String ASTROCYTE_HOST = "localhost";
	// SSH
	public static String SSH_DIR = "/home/student/.ssh";
	public static String SSH_USER = "myelin";
	public static String SSH_KEY = "private-key";
	public static String SSH_PASS = "password";
	
	public static String STUDENT_ID = null;
	public static String COURSE_UUID = null;
	
	public static String SERVER_GIT_DIR = "/home/myelin/git-repositories";
	
	private static final Logger log = LoggerFactory.getLogger(GlialProps.class);
	
	
	private static Properties props;
	static {
		props = getProps();
		
		if(props != null) {
			ASTROCYTE_URL = props.getProperty("astro.cyte");
			ASTROCYTE_HOST = props.getProperty("astro.host");
			MEDIA_LOCATION = props.getProperty("media.location"); // will be a descriptor file
			WEB_PROJECT = props.getProperty("web.project");
			STUDENT_PROJECT = props.getProperty("student.project");
			
			SSH_DIR = props.getProperty("ssh.dir");
			SSH_USER = props.getProperty("ssh.user");
			SSH_KEY = props.getProperty("ssh.key");
			SSH_PASS = props.getProperty("ssh.pass");
			
			SERVER_GIT_DIR = props.getProperty("server.git");
			
			STUDENT_ID = props.getProperty("student.sid", null);
			COURSE_UUID = props.getProperty("course.uuid", null);
		}
	}
	
	public static Properties getProps() {
		Properties props = new Properties();
		try {
			System.out.println("OUT: loading prop file");
			InputStream ps = Thread.currentThread().getContextClassLoader().getResourceAsStream("glial.properties");
			System.out.println("OUT: loaded prop file");
			props.load(ps);
			return props;
		} catch (FileNotFoundException e) {
			System.out.println("OUT: failed prop file");
			log.error("Could not find properties file glial.properties!");
			return null;
		} catch (IOException e) {
			System.out.println("OUT: failed prop file");
			log.error("Error loading properties file glial.properties!");
			return null;
		}
	}
}
