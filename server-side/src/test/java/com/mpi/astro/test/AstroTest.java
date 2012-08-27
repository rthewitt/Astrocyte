package com.mpi.astro.test;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.dao.StudentDao;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.service.edu.EduService;

/*
 * As I'll be injecting services and DAOs, this is effectively
 * an integration test suite, not a unit test suite. 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional //No needed yet, maybe
@TransactionConfiguration(transactionManager="transactionManager")
@ContextConfiguration("classpath:/AstroTest-context.xml")
public class AstroTest {
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private EduService studentService;
	
	private static final List<Student> studentList = new ArrayList<Student>();
	static {
		studentList.add(new Student("Ryan", "Hewitt"));
		studentList.add(new Student("Alice", "Test"));
		studentList.add(new Student("Susan", "Nann"));
		studentList.add(new Student("Jacob", "Something"));
		studentList.add(new Student("Billy", "Smith"));
	}
	

	@BeforeClass
	public static void setupClass() {
		
	}
	
	@Before
	public void setup() {
		saveStudents();
	}
	
	@Test
	public void first() {
		Assert.assertTrue(testInitial());
	}
	
	public boolean testInitial() {
		List<Student> list = studentDao.getStudents();
		if(list.size() != studentList.size()) return false;
		for(Student s : list) {
			System.out.println(s);
		}
		return true;
	}
	
	@Test
	public void TestFileWrite() {
		studentService.writeStudentsToFile("JUNIT-WRITE-TEST");
	}
	
	public void saveStudents() {
		// TODO deleteAll() implementation
		for(Student s : studentList) {
			studentDao.save(s);
		}
	}
}