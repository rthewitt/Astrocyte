package com.mpi.astro.core.model.builder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CodeTutorialBuilder extends AbstractTutorialBuilder 
implements TutorialBuilder {

	@Override
	public void buildProjectDefinition(String project) {
		this.tutorial.setPrototype(project); // git repository
	}
	
	/**
	 * @Accepts a JSON based lesson plan from client
	 */
	// Must be built internally for validation and to segregate lessons for interactive tutorials
	// Also, data may be provided for a lesson that the student should not see client side, such
	// as workflow handling and testing specifics
	// TODO consider changing to JSONObject accepts, wired through WebService
	@Override
	public void buildLessons(String lessonFile) {
		try {
			logger.debug("Lesson file string as received by utils: \n" + lessonFile);
			
			JSONObject tutorialDef = (JSONObject) new JSONParser().parse(lessonFile);
			
			logger.debug("Lesson file as jsonObject:\n" + tutorialDef.toString() + 
					"\nand as JSONString:\n" + tutorialDef.toJSONString());
			
			JSONArray lessonArray = (JSONArray)tutorialDef.get("lessons");
			for(Object lessonObj : lessonArray) {
				// This is where I'll do any sort of internal meta-lesson stripping, should I need it.
				tutorial.addLesson(((JSONObject)lessonObj).toJSONString());
			}
		} catch (ParseException e) {
			logger.error("Trouble parsing tutorial json in CodeTutorialBuilder", e);
		}
	}
}