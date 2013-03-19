package com.mpi.astro.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.dao.StudentDao;
import com.mpi.astro.core.model.builder.CodeTutorialBuilder;
import com.mpi.astro.core.model.builder.TutorialBuilder;
import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.CourseImpl;
import com.mpi.astro.core.model.edu.CourseTutorial;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentStatus;
import com.mpi.astro.core.model.edu.Tutorial;
import com.mpi.astro.core.service.edu.EduService;
import com.mpi.astro.core.util.AstrocyteConstants;
import com.mpi.astro.core.util.AstrocyteConstants.STUDENT_STATE;
import com.mpi.astro.core.util.AstrocyteUtils;

/*
 * This is effectively an integration test suite, not a unit test suite. 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = true) // Look into txmanager
@ContextConfiguration("classpath:/AstroTest-context.xml")
public class AstroTest {
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private EduService eduService;
	
	@Autowired
	private EntityManagerFactory emf;

	@BeforeClass
	public static void setupClass() {
	}
	
	@Before
	public void setup() {
		
		List<Student> students = new ArrayList<Student>();
		students.add(new Student("Ryan", "Hewitt"));
		students.add(new Student("Alice", "Test"));
		
		Course CS104 = new CourseImpl();
		CS104.setName("CS104");
		CS104.setDescription("TEST ONLY");
		CS104.setWorkflow(AstrocyteConstants.COURSE_WORKFLOW.PASSIVE);
		
		// TODO IMPORTANT! remove remote dependencies!!
		String jsonStr =  AstrocyteUtils
				.getExternalTutorialDescriptionAsString("http://myelinprice.com/3-step-tutorial.json");
		
		TutorialBuilder tb = new CodeTutorialBuilder();
		tb.createTutorial("Test tut");
		tb.buildProjectDefinition("Fake Prototype");
		tb.buildLessons(jsonStr); 
		
		Tutorial tut = tb.getTutorial();
		tut.setDescription("TEST TUTORIAL");
		
		// TODO should be a service method
		CourseTutorial ass = new CourseTutorial();
		ass.setCourse(CS104);
		ass.setTutorial(tut);
		CS104.addTutorialAssociation(ass);
		tut.addCourseAssociation(ass);
		
		eduService.save(tut);
		eduService.save(CS104);
		
		for(Student s : students)
			eduService.save(s);
	}
	/*
	@Test
	@Transactional
	public void testInitial() {

		List<Student> list = studentDao.getStudents();
		assertEquals(2, list.size());
		assertEquals(1, eduService.getAllCourses().size());
		assertEquals(1, eduService.getAllTutorials().size());
	}
	
	
	@Test
	@Transactional
	public void testEnrollment() { 
		List<Student> list = studentDao.getStudents();

		Course only = eduService.getCourseByName("CS104");
		List<Student> students = eduService.getAllStudents();
		for(Student s : students)
			eduService.enrollStudent(s, only);
		Student first = students.get(0);
		assertTrue(first.isEnrolled(only));
	}
	*/
	
	@Test
	@Transactional
	public void testAdvance() {
		// Was trying to isolate the session to simulate production loading failure
		/*
		EntityManager isolate = emf.createEntityManager();
		isolate.clear();
		EntityTransaction tx = isolate.getTransaction();
		tx.begin();
		tx.setRollbackOnly();
		*/
		
		// Ok, this causes course tutorial association to be null
		// I would understand loading issues, but why no associations?
//		eduService.clearForTest();
		
//		Course only = eduService.getCourseByName("CS104");
		List<Course> cs = eduService.getAllCourseDefinitions();
		assertEquals(1, cs.size());
		Course only = cs.get(0);
		
		// How in the world could this possibly lead to an EntityNotFoundException?
		// Is it because I don't have a proxy to the transactional session in test context?
//		eduService.save(only);
		
		assertEquals(1, only.getTutAssociations().size());
		
		assertEquals(0, eduService.getStudentsForCourse(only.getId()).size());
		Student first = eduService.getAllStudents().get(0);
//		eduService.enrollStudent(first, only); TODO replace, changed 3/17/2013 in order to compile
		assertTrue(first.isEnrolled(only));
		
//		assertTrue(eduService.isEligibleForAdvance(first, only)); TODO replace, changed 3/17/2013 in order to compile
		
		StudentStatus current = eduService.getStudentStatus(first, only);
		
		int initial = current.getLessonNum();
		assertEquals(0, initial);
		int newLesson = initial++;
		
		// TODO establish service logic to handle transactional state changes!!
		first.setState(STUDENT_STATE.ADVANCING);

		eduService.advanceStudentForCourse(first, only);
		
		/* Ugly verification
		Set<CourseTutorial> backward = first.getCurrentTutorialForCourse(only).getCourseAssociations();
		assertEquals(1, backward.size());
		Course whut = ((CourseTutorial)backward.toArray()[0]).getCourse();
		int fin = ((StudentCourse)whut.getStudAssociations().toArray()[0]).getLessonNum();
		assertEquals(1, fin);
		*/
		
		
//		assertEquals(initial, first.getLessonStatusForCourse(only));
		assertEquals(newLesson, eduService.getStudentStatus(first, only).getLessonNum());
		
		// not using now
//		isolate.close();
	} 
	
}