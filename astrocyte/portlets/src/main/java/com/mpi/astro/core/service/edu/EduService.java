package com.mpi.astro.core.service.edu;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.dao.CourseDao;
import com.mpi.astro.core.dao.CourseTutorialDao;
import com.mpi.astro.core.dao.StudentCourseDao;
import com.mpi.astro.core.dao.StudentDao;
import com.mpi.astro.core.dao.TutorialDao;
import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.CourseTutorial;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentCourse;
import com.mpi.astro.core.model.edu.StudentStatus;
import com.mpi.astro.core.model.edu.Tutorial;
import com.mpi.astro.core.util.PropertiesUtil;

// TODO add Transactional annotation to service methods as necessary.
// TODO remove object management from Controller and use only service
// TODO create interface layer for services
// TODO consider inheritance for domain instead of ENUM types
// TODO separate controllers to represent phrases of the transactional conversation
@Service
public class EduService {
	
	@Autowired
	private StudentDao studentDao;
	@Autowired
	private TutorialDao tutorialDao;
	@Autowired
	private CourseDao courseDao;
	@Autowired
	private StudentCourseDao enrollmentDao;
	@Autowired
	private CourseTutorialDao tmpCTDao; 
	
	@Autowired
	private MyelinService myelinService;
	
	public String getPath() {
		return PropertiesUtil.getProperty(PropertiesUtil.PROP_DATA_DIR);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EduService.class);

	/**
	 * Retrieve course for the given id
	 * @param courseId
	 * @return course object
	 */
	public Course getCourse(long courseId) {
		return courseDao.find(courseId);
	}
	
	public Course getCourseByName(String courseName) {
		return courseDao.findByName(courseName);
	}
	
	public List<Student> getStudentsForCourse(long courseId) {
		return courseDao.getStudentsForCourse(courseId);
	}
	
	public List<Course> getAllCourses() {
		return courseDao.getCourses();
	}
	
	public Tutorial getTutorial(long id) {
		return tutorialDao.find(id);
	}
	
	public Tutorial getTutorialEager(long id) {
		return tutorialDao.getWithLessons(id);
	}
	
	public List<Tutorial> getAllTutorials() {
		return tutorialDao.getTutorials();
	}
	
	public Student getStudent(long id) {
		return studentDao.find(id);
	}
	
	public Student getStudentEager(long id) {
		return studentDao.getStudentInitialized(id);
	}
	
	public List<Student> getAllStudents() {
		return studentDao.getStudents();
	}
	
	public Student save(Student s) {
		return studentDao.save(s);
	}
	
	public Course save(Course c) {
		return courseDao.save(c);
	}
	
	public Tutorial save(Tutorial t) {
		return tutorialDao.save(t);
	}
	
	public StudentCourse save(StudentCourse enrollment) {
		return enrollmentDao.save(enrollment);
	}
	
	public CourseTutorial save(CourseTutorial association) {
		return tmpCTDao.save(association);
	}
	
	public Tutorial getCurrentTutorialForStudent(Student student, Course course) {
		// currently uses two separate queries.  Consider composite if necessary
		return enrollmentDao.getCurrentTutorialForStudent(student, course);
	}
	
	// If this works, decide if Dao is actually necessary or not
	// I would rather not treat associations as first-order persistent objects
	@Transactional
	public void enrollStudent(Student student, Course course) {
		StudentCourse enrollment = new StudentCourse();
		enrollment.setStudent(student);
		enrollment.setCourse(course);
		enrollment.setEnrollDate(new Date());
		
		student.addCourseAssociation(enrollment);
		course.addStudentAssociation(enrollment); // ADDED 12/29/2012
		
		save(enrollment);
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
			course.addTutorialAssociation(association);
			tutorial.addCourseAssociation(association);
			
			save(association);
			save(course);
			save(tutorial);
			
//			Set<Student> students = new HashSet<Student>(getStudentsForCourse(courseId));
			Set<Student> students = course.getStudentsInCourse();
			
			logger.debug("About to dispatch with student array length: " + students.size());
			myelinService.dispatchInit(course, tutorial, students);
		}
		
		public StudentStatus getStudentStatus(Student student, Course course) {
			return enrollmentDao.getStudentStatus(student, course);
		}
	
		public boolean isEligibleForAdvance(Student student, Course course) {
			StudentStatus current = enrollmentDao.getStudentStatus(student, course);
			Tutorial currentTut = enrollmentDao.getCurrentTutorialForStudent(student, course);
			boolean canAdvance = current.getLessonNum() < currentTut.getNumSteps();
			logger.debug("Student " + student.getId() + (canAdvance ? " can " : " cannot ") + "advance.\n" +
					"Current: " + current.getLessonNum() + "\nAvailable Lessons: " + currentTut.getNumSteps());
			return canAdvance;
		}
		
		@Transactional
		public void advanceStudentForCourse(Student student, Course course) {
//			StudentStatus current = enrollmentDao.getStudentStatus(student, course);  // will used later as vehicle
			
			// TODO provide conditional.  If another tutorial exists, move on and zero out lessonNum in DB
			// if las tutorial and final lesson num, graduate the student (after workflow?)
			StudentCourse enrollment = enrollmentDao.getEnrollment(student, course);
			enrollment.setLessonNum(enrollment.getLessonNum()+1); // temporary without constraints
			save(enrollment);
		}
		
	    // When lesson becomes available for a student, as determined by workflow
		public boolean deployLesson(long courseId, Student student, String commitRef) {
			
			Course course = getCourse(courseId);
			Tutorial tut = enrollmentDao.getCurrentTutorialForStudent(student, course);
			
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
		
		public void clearForTest(){
			studentDao.clearForTest();
			tutorialDao.clearForTest();
			courseDao.clearForTest();
			enrollmentDao.clearForTest();
			tmpCTDao.clearForTest(); 
		}
	
}
