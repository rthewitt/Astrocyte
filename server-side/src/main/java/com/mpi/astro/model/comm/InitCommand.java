package com.mpi.astro.model.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;

import com.mpi.astro.model.edu.Student;
import com.mpi.astro.util.MyelinAction;


public class InitCommand extends BaseCommand implements Command {
	
	protected List<Student> students;
	protected String prototype;
	
	// TODO make sure prototype is handled appropriately, should be more than a string
	public InitCommand(String courseName, String repo, List<Student> students) {
		super(MyelinAction.INITIALIZE, courseName);
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
		
		ctx.put("courseName", this.courseName);
		
		List<Map<String, String>> stArr = new ArrayList<Map<String, String>>();
		Set<Student> unique = new HashSet<Student>(students);
		
		for(Student s : unique) {
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