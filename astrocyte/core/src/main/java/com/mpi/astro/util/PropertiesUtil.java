package com.mpi.astro.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class PropertiesUtil extends
PropertyPlaceholderConfigurer {
	
	
	// property file keys
		public final static String PROP_ROOT_DIR = "myelin.base";
		public final static String PROP_INTERNAL = "myelin.internal";
		public final static String PROP_PUBLIC_PROF = "myelin.public_prof";
		public final static String PROP_PUBLIC_STUD = "myelin.public_student";
		public final static String PROP_BIN_DIR = "myelin.bin.dir";
		public final static String PROP_DATA_DIR = "myelin.data.dir";
		public final static String PROP_INTERNAL_DIR = "myelin.internal.dir";
		public final static String PROP_PUBLIC_PROF_DIR = "myelin.public_prof.dir";
		public final static String PROP_PUBLIC_STUD_DIR = "myelin.public_student.dir";
		public final static String PROP_PROTO = "myelin.proto";
		public final static String PROP_PROTO_ID = "myelin.proto.id";
	
	
	private static Map<String, String> propertiesMap;
	
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactory,
			Properties props) throws BeansException {
		super.processProperties(beanFactory, props);
		
		propertiesMap = new HashMap<String, String>();
		
		for(Object key : props.keySet()) {
			String keyStr = key.toString();
			// Uses fix for deprecated method, verify
			propertiesMap.put(keyStr, resolvePlaceholder(keyStr, props));
		}
	}
	
	public static String getProperty(String name) {
		return propertiesMap.get(name);
	}
	

}
