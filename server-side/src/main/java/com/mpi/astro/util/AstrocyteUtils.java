package com.mpi.astro.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mpi.astro.model.arcade.impl.JavaGame;
import com.mpi.astro.model.comm.AdvanceCommand;
import com.mpi.astro.model.comm.InitCommand;
import com.mpi.astro.model.comm.UpdateCommand;
import com.mpi.astro.model.edu.Student;

public class AstrocyteUtils {
	
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