package com.mpi.astro.core.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpi.astro.core.model.arcade.impl.JavaGame;
import com.mpi.astro.core.model.comm.AdvanceCommand;
import com.mpi.astro.core.model.comm.InitCommand;
import com.mpi.astro.core.model.comm.UpdateCommand;
import com.mpi.astro.core.model.edu.Student;

public class AstrocyteUtils {
	
	protected static final Logger logger = LoggerFactory.getLogger(AstrocyteUtils.class);
	
	private static final JavaGame alphaGame = new JavaGame();
	
	static {
		alphaGame.setAppName("com.alpha.game.Alpha Game");
		alphaGame.setArtifact("alpha-game-1.0-SNAPSHOT.jar");
		alphaGame.setAuthor("Fierfek");
		alphaGame.setJavaVersion("1.6");
		alphaGame.setMainClass("com.alpha.game.Alpha");
		alphaGame.setThumbNail("http://www.freevectorgraphics.org/d/file/201103/81b147bb0534b59dded39b95f5bbd267.png");
		alphaGame.setTitle("Alpha");
		alphaGame.setVendor("Myelin Price Interactive");
	}
	
	
	// eventually, this will come from a service
	public static JavaGame getAlphaGame() {
		return alphaGame;
	}
	
	// TODO move keys into static location, perhaps Constants class or Command class 
	// TODO merge duplicate code for generation
	
	public static JSONObject getJSONCourseInit(String courseName, List<Student> students, String prototype) {
		JSONObject obj = new JSONObject();
		
		obj.put("courseName", courseName);
		
		JSONArray stArr = new JSONArray();
		Set<Student> unique = new HashSet<Student>(students);
		
		for(Student s : unique) {
			Map<String, String> stud = new HashMap<String, String>();
			stud.put("studentId", s.getStudentId());
			stud.put("name", String.format("%s %s", s.getFirstName(), s.getLastName()) );
			stud.put("studentId", s.getStudentId());
			
			stArr.add(stud);
		}
		
		obj.put("students", stArr);
		
		Map<String, String> proto = new HashMap<String, String>();
		
		proto.put("repository", prototype);
		proto.put("initTag", "INIT");
		
		obj.put("prototype", proto);
		
		return obj;
	}
	
	public static String getCourseNameFromCourseUUI(String courseUUID) {
		return courseUUID.split("-")[0];
	}
	
	// TODO reroute through auto-commit form in JSP after JSON web-service
	public static String getExternalTutorialDescriptionAsString(String URI) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(URI);
		try {
			HttpResponse response = client.execute(get);
			return IOUtils.toString(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			logger.error("Problem getting JSON descriptor for tutorial", e);
		} catch (IOException e) {
			logger.error("Problem getting response from httpClient", e);
		}
		return null;
	}
	
	public static String getCheckpointStr(int number) {
		return AstrocyteConstants.BASE_TAG+"-"+number;
	}
	
	public static Class<?> getCommandClass(MyelinAction action){
		
		switch(action) {
		case INITIALIZE:
			return InitCommand.class;
		case UPDATE_CLASS:
			return UpdateCommand.class;
		case UPDATE_STUDENT:
			return UpdateCommand.class;
		case ADVANCE_STUDENT:
			return AdvanceCommand.class;
		default:
			return null;
		}
	}
	
}