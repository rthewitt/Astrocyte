package com.mpi.astro.service.edu;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.dao.CourseDao;
import com.mpi.astro.dao.StudentDao;
import com.mpi.astro.dao.TutorialDao;
import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.CourseTutorial;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.model.edu.StudentCourse;
import com.mpi.astro.model.edu.Tutorial;
import com.mpi.astro.util.PropertiesUtil;

// I had left this annotation off - has it affected the construction of the code-base?
// For instance, did I have more than one instance leading to errors along the way?
@Service
public class EduService {
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private TutorialDao tutorialDao;
	
	@Autowired
	private CourseDao courseDao;
	
	@Autowired
	private MyelinService myelinService;
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	public String getPath() {
		return PropertiesUtil.getProperty(PropertiesUtil.PROP_DATA_DIR);
	}
	
//	private static final Logger logger = Logger.getLogger(EduService.class);
	private static final Logger logger = LoggerFactory.getLogger(EduService.class);
	
	
	// no longer valid, keeping for posterity / example
	public List<Student> getStudentsInCourse(Long courseId) {
	
		List<Student> students = new ArrayList<Student>();
		
		/*
		String sql = "SELECT s.* FROM STUDENT s " +
				"INNER JOIN STUDENT_COURSE sc ON sc.STUDENT_ID = s.STUDENT_ID " +
				"INNER JOIN COURSE c ON c.COURSE_ID = sc.COURSE_ID " +
				"WHERE c.COURSE_ID = 1;";
		*/
		
		String hql = "select distinct s from Student s join s.courses c where c.id =:course_id ";
		Query query = entityManager.createQuery(hql);
		query.setParameter("course_id", courseId);
		students = (List<Student>)query.getResultList();
		
		return students;
	}

	/**
	 * Retrieve course for the given id
	 * @param courseId
	 * @return course object
	 */
	public Course getCourse(long courseId) {
		return courseDao.find(courseId);
	}
	
	public List<Course> getAllCourses() {
		return courseDao.getCourses();
	}
	
	public Tutorial getTutorial(long id) {
		return tutorialDao.find(id);
	}
	
	public List<Tutorial> getAllTutorials() {
		return tutorialDao.getTutorials();
	}
	
	public Student getStudent(long id) {
		return studentDao.find(id);
	}
	
	public List<Student> getAllStudents() {
		return studentDao.getStudents();
	}
	
	public List<Student> getStudentsInCourse(Course course) {
			return getStudentsInCourse(course.getId());
	}
	
	public void save(Student s) {
		studentDao.save(s);
	}
	
	public void save(Course c) {
		courseDao.save(c);
	}
	
	public void save(Tutorial t) {
		tutorialDao.save(t);
	}
	
	// lack of conditional logic causes stackoverflow when already enrolled during student save
	public void enrollStudent(Student student, Course course) {
		StudentCourse enrollment = new StudentCourse();
		enrollment.setStudent(student);
		enrollment.setCourse(course);
		enrollment.setEnrollDate(new Date());
		
		student.saveCourseAssociation(enrollment);
	}
	
	public Set<Course> getCoursesForStudent(Student student) {
		Set<Course> courses = new HashSet<Course>();
		for(StudentCourse enrollment : student.getCourseAssociations()) {
			courses.add(enrollment.getCourse());
		}
		return courses;
	}
	
	public Set<Course> getCoursesForStudentById(long studentId) {
		return getCoursesForStudent(studentDao.find(studentId));
	}
	
	// also consider using git build-in email functionality
	public void notifyProfessorPullRequest() {
		// TODO update database?
		// if school has their own ec2 instance / site / control hub, send data to it?
	}
		
//		============ CREATE A SEPARATE SERVICE FOR ADMIN LEVEL TASKS ================
		
		// TODO create Syllabus object and CourseFactory to set up 
		// repositories / tags prior to initialization
	@Transactional
		public void initializeCourse(long courseId, long tutorialId) {
			
			final Course course = getCourse(courseId);
			Tutorial tutorial = getTutorial(tutorialId);
			
			CourseTutorial association = new CourseTutorial(); // testing constructor
			association.setCourse(course);
			association.setTutorial(tutorial);
			course.saveTutorialAssociation(association);
			// will cascade.  Change all of this to use factory
			save(course); // WHY IS THIS BROKEN?
			
			Set testSet = course.getStudAssociations(); // ADDED TODO finish test
			logger.debug("Before dispatch, getStudAssociations() test results in "+testSet.size()+" associations.");
			
			Set<Student> students = course.getStudents();
			logger.debug("About to dispatch with student array length: " + students.size());
			
			myelinService.dispatchInit(course, tutorial, students);
		}
		
	    // When lesson becomes available for a student, as determined by workflow
		public boolean deployLesson(long courseId, Student student, String commitRef) {
			
			Course course = getCourse(courseId);
			Tutorial tut = 
					student.getCurrentTutorialForCourse(course);
			
			if(tut == null) {
				logger.error(String.format("Cannot update, student %s for course %s with id %s",
						student.getId(), getCourse(courseId).getName(), courseId) );
				return false;
			}
			
			logger.info("requesting update for student " + student.getId());
			
			myelinService.requestStudentMerge(course, tut.getPrototype(), 
					commitRef, student.getStudentId().toString());
			return true;
		}
		
		// When lesson becomes available for an entire class, as determined by workflow
		public boolean deployLesson(long courseId, long tutorialId, String commitRef) {
			Course course = getCourse(courseId);
			if(course == null) return false;
			
			Tutorial tut = getTutorial(tutorialId);
			myelinService.requestClassMerge(course, tut.getPrototype(), commitRef);
			return true;
		}
	
}
