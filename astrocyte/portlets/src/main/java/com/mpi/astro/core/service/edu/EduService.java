package com.mpi.astro.core.service.edu;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.dao.CourseDao;
import com.mpi.astro.core.dao.CourseInstanceDao;
import com.mpi.astro.core.dao.CourseTutorialDao;
import com.mpi.astro.core.dao.StudentCourseDao;
import com.mpi.astro.core.dao.StudentDao;
import com.mpi.astro.core.dao.TutorialDao;
import com.mpi.astro.core.model.builder.CourseFactory;
import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.CourseInstance;
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
	private CourseInstanceDao courseInstanceDao;
	@Autowired
	private StudentCourseDao enrollmentDao;
	@Autowired
	private CourseTutorialDao tmpCTDao; 
	
	@Autowired
	private CourseFactory courseFactory;
	
	@Autowired
	private MyelinService myelinService;
	
	public String getPath() {
		return PropertiesUtil.getProperty(PropertiesUtil.PROP_DATA_DIR);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EduService.class);

	public Course getCourseDefinition(long courseId) {
		return courseId != 0L ? courseDao.find(courseId):
			courseFactory.createDefinition();
	}
	
	public CourseInstance getDeployedCourse(long courseId) {
		return courseInstanceDao.find(courseId);
	}
	
	public CourseInstance getDeployedCourse(String courseUUID) {
		return courseInstanceDao.find(courseUUID);
	}
	
	public List<CourseInstance> getAllDeployedCourses() {
		return courseInstanceDao.getCourses();
	}
	
	public Course getCourseDefinitionByName(String courseName) {
		return courseDao.findByName(courseName);
	}
	
	public List<Student> getStudentsForCourse(long instanceId) {
		return courseInstanceDao.getStudentsForCourseById(instanceId);
	}
	
	public List<Student> getStudentsForCourse(String uuid) {
		return courseInstanceDao.getStudentsForCourseByUUID(uuid);
	}
	
	public List<Course> getAllCourseDefinitions() {
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
	
	public CourseInstance save(CourseInstance c) {
		return courseInstanceDao.save(c);
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
	
	public Tutorial getCurrentTutorialForStudent(Student student, CourseInstance course) {
		// currently uses two separate queries.  Consider composite if necessary
		return enrollmentDao.getCurrentTutorialForStudent(student, course);
	}
	
	// TODO this is going to be a mess
	public List<Tutorial> getTutorialListForCourse(Course course) {
		List<Tutorial> tuts = new ArrayList<Tutorial>();
		// These will be ordered soon enough.
		Set<CourseTutorial> ta = course.getTutAssociations();
		
		for(CourseTutorial a : ta)
			tuts.add(a.getTutorial());
		
		return tuts;
	}
	
	// Why Should not the student/course be saved?!
	@Transactional
	public void enrollStudent(Student student, CourseInstance course) {
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
		throw new RuntimeException("Notification not implemented!");
		// update database?
		// if school has their own ec2 instance / site / control hub, send data to it?
	}
		
//		============ CREATE A SEPARATE SERVICE FOR ADMIN LEVEL TASKS ================
		
		// Remember that we want the course repository created during creation.
		// perhaps a strategy in the factory?
		@Transactional
		public void deployCourse(Course def) {
			deployCourse(def, null, 0L);
		}
		
		@Transactional
		public void deployCourse(Course def, List<String> studentIds) {
			deployCourse(def, studentIds, 0L);
		}
		
		@Transactional
		public void deployCourse(Course def, List<String> studentIds, long tutId) {
			if(tutId == 0L) throw new IllegalArgumentException("Not enough students to deploy this type of course.");
			
			Tutorial tut = getTutorial(tutId);
			// TODO figure out just how bad this CourseTutorial assoc is going to be with two course objects
			//=========TEMPORARY==============
			CourseTutorial association = new CourseTutorial();
			association.setCourse(def);
			association.setTutorial(tut);
			def.addTutorialAssociation(association);
			tut.addCourseAssociation(association);
			
			save(association);
			save(tut);
			def = save(def);
			//=================================
			CourseInstance course = courseFactory.createCourse(def);
			
			if(studentIds != null) {
				for(String id : studentIds) {
					Student student = getStudent(Long.valueOf(id));
					enrollStudent(student, course);
					save(student);
				}
				course = save(course);
			}
			Set<Student> students = course.getStudentsInCourse();
			
			logger.debug("About to dispatch with student array length: " + students.size());
			// TODO change this so that myelinService dispatches with the first tutorial in list
			// TODO change tutorials to ordered list.  Consider module based non-ordered
			myelinService.dispatchInit(course, tut, students);
		}
		
		public StudentStatus getStudentStatus(Student student, CourseInstance course) {
			return enrollmentDao.getStudentStatus(student, course);
		}
	
		// TODO verify holds up to changes
		public boolean isEligibleForAdvance(Student student, CourseInstance course) {
			StudentStatus current = enrollmentDao.getStudentStatus(student, course);
			Tutorial currentTut = enrollmentDao.getCurrentTutorialForStudent(student, course);
			boolean canAdvance = current.getLessonNum() < currentTut.getNumSteps();
			logger.debug("Student " + student.getId() + (canAdvance ? " can " : " cannot ") + "advance.\n" +
					"Current: " + current.getLessonNum() + "\nAvailable Lessons: " + currentTut.getNumSteps());
			return canAdvance;
		}
		
		@Transactional
		public void advanceStudentForCourse(Student student, CourseInstance course) {
//			StudentStatus current = enrollmentDao.getStudentStatus(student, course);  // will used later as vehicle
			
			// TODO provide conditional.  If another tutorial exists, move on and zero out lessonNum in DB
			// if las tutorial and final lesson num, graduate the student (after workflow?)
			StudentCourse enrollment = enrollmentDao.getEnrollment(student, course);
			enrollment.setLessonNum(enrollment.getLessonNum()+1); // temporary without constraints
			save(enrollment);
		}
		
	    // When lesson becomes available for a student, as determined by workflow
		public boolean deployLesson(long instanceId, Student student, String commitRef) {
			
			CourseInstance enrolledCourse = getDeployedCourse(instanceId);
			Tutorial tut = enrollmentDao.getCurrentTutorialForStudent(student, enrolledCourse);
			
			if(tut == null) {
				logger.error(String.format("Cannot update, student %s for course %s with id %s",
						student.getId(), getCourseDefinition(instanceId).getName(), instanceId) );
				return false;
			}
			
			logger.info("requesting update for student " + student.getId());
			
			myelinService.requestStudentMerge(enrolledCourse, tut.getPrototype(), 
					commitRef, student.getStudentId().toString());
			return true;
		}
		
		// When lesson becomes available for an entire class, as determined by workflow
		public boolean deployLesson(long courseId, long tutorialId, String commitRef) {
			Course course = getCourseDefinition(courseId);
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
		
		public String testMethod() {
			return "eduService says hello";
		}
	
}
