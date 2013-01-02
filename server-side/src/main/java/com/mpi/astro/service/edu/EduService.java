package com.mpi.astro.service.edu;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.dao.CourseDao;
import com.mpi.astro.dao.CourseTutorialDao;
import com.mpi.astro.dao.StudentCourseDao;
import com.mpi.astro.dao.StudentDao;
import com.mpi.astro.dao.TutorialDao;
import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.CourseTutorial;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.model.edu.StudentCourse;
import com.mpi.astro.model.edu.Tutorial;
import com.mpi.astro.util.PropertiesUtil;

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
	// this may change to a List<CT> as in Tut->Lessons, but with entity
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
		
		save(enrollment); // testing
	}
	
	//TODO add enrollStudent( Collection of Course ) @Transactional // entry-point for Controller
	
	// This should probably retrieve an unmodifiable set.
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
	
		public boolean isEligibleForAdvance(Student student, Course course) {
			return student.getLessonStatusForCourse(course) <
					student.getCurrentTutorialForCourse(course).getNumSteps();
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
