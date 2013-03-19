package com.mpi.astro.core.model.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;

import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.util.MyelinAction;


public class InitCommand extends BaseCommand implements Command {
	
	protected Set<Student> students;
	protected String prototype;
	
	// TODO make sure prototype is handled appropriately, should be more than a string
	public InitCommand(String courseUUID, String repo, Set<Student> students) {
		super(MyelinAction.INITIALIZE, courseUUID);
		this.prototype = repo;
		this.students = students;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
	}

	@Override
	protected Map<String, Object> getContext() {
		
		Map<String, Object> ctx = new HashMap<String, Object>();
		
		ctx.put("courseUUID", this.courseUUID);
		
		List<Map<String, String>> stArr = new ArrayList<Map<String, String>>();
		
		for(Student s : students) {
			Map<String, String> stud = new HashMap<String, String>();
			stud.put("studentId", s.getStudentId());
			stud.put("name", String.format("%s %s", s.getFirstName(), s.getLastName()) );
			stud.put("studentId", s.getStudentId());
			
			stArr.add(stud);
		}
		
		ctx.put("students", stArr);
		
		Map<String, String> proto = new HashMap<String, String>();
		
		// TODO handle this according to base tags, remove hard-coding
		proto.put("repository", prototype);
		proto.put("initTag", "INIT");
		
		ctx.put("prototype", proto);
		
		return ctx;
	}
}