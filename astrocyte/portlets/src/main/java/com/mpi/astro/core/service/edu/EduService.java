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
import com.mpi.astro.core.dao.MachineMappingDao;
import com.mpi.astro.core.dao.StudentCourseDao;
import com.mpi.astro.core.dao.StudentDao;
import com.mpi.astro.core.dao.TutorialDao;
import com.mpi.astro.core.dao.VMDao;
import com.mpi.astro.core.model.builder.CourseFactory;
import com.mpi.astro.core.model.edu.Course;
import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.CourseTutorial;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentCourse;
import com.mpi.astro.core.model.edu.StudentStatus;
import com.mpi.astro.core.model.edu.StudentVM;
import com.mpi.astro.core.model.edu.Tutorial;
import com.mpi.astro.core.model.vm.VM;
import com.mpi.astro.core.model.vm.VMType;
import com.mpi.astro.core.util.AstrocyteConstants;
import com.mpi.astro.core.util.AstrocyteConstants.STUDENT_STATE;
import com.mpi.astro.core.util.AstrocyteUtils;
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
	private VMDao vmDao;
	@Autowired
	private MachineMappingDao machineMappingDao;
	
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
	
	/* Consider re-adding if entityManager.find is more efficient than query
	public CourseInstance getDeployedCourse(long courseId) {
		return courseInstanceDao.find(courseId);
	} */
	
	public CourseInstance getDeployedCourse(String courseUUID) {
		return courseInstanceDao.find(courseUUID);
	}
	
	public List<CourseInstance> getAllDeployedCourses() {
		return courseInstanceDao.getCourses();
	}
	
	public Course getCourseDefinitionByName(String courseName) {
		return courseDao.findByName(courseName);
	}
	
	@Deprecated
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
	
	public Student getStudentEagerBySID(String studentId) {
		return studentDao.getStudentInitializedBySID(studentId);
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
	
	public VM save(VM vm) {
		return vmDao.save(vm);
	}
	
	public StudentVM save(StudentVM map) {
		return machineMappingDao.save(map);
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
	
	public STUDENT_STATE getStateForStudentInCourse(Student student, CourseInstance course) {
		StudentCourse enrollment = enrollmentDao.getEnrollment(student, course);
		return enrollment.getState();
	}
	
	@Transactional
	public void setStateForStudentInCourse(Student student, CourseInstance course, STUDENT_STATE state) {
		StudentCourse enrollment = enrollmentDao.getEnrollment(student, course);
		enrollment.setState(state);
		save(enrollment);
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
	
	public StudentVM getMappingForStudent(Student student, CourseInstance instance) {
		return machineMappingDao.getMapping(student, instance);
	}
	
	public StudentVM getMappingForStudent(Student student, String deployedCourseId) {
		CourseInstance instance = getDeployedCourse(deployedCourseId);
		return machineMappingDao.getMapping(student, instance);
	}
	
	public List<StudentVM> getMappingsForStudent(Student student) {
		return machineMappingDao.getMappingsForStudent(student);
	}
	
	// TODO move save logic from controller into service layer if possible 
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
		public CourseInstance deployCourse(Course def) {
			return deployCourse(def, null, 0L);
		}
		
		@Transactional
		public CourseInstance deployCourse(Course def, List<String> studentIds) {
			return deployCourse(def, studentIds, 0L);
		}
		
		@Transactional
		public CourseInstance deployCourse(Course def, List<String> studentIds, long tutId) {
			if(tutId == 0L) throw new IllegalArgumentException("Not enough students to deploy this type of course.");
			
			Student[] portalUsers = new Student[studentIds.size()];
			
			Tutorial tut = getTutorial(tutId);
			
			// test lazy solution:
			
			def = save(def);
			
			
			CourseTutorial association = new CourseTutorial();
			association.setCourse(def);
			association.setTutorial(tut);
			def.addTutorialAssociation(association);
			tut.addCourseAssociation(association);
			
			save(association);
			save(tut);
			def = save(def);
			
			CourseInstance course = courseFactory.createCourse(def);
			
			if(studentIds != null) {
				for(int x=0; x<studentIds.size(); x++) {
					Student student = getStudent(Long.valueOf(studentIds.get(x)));
					enrollStudent(student, course);
					portalUsers[x] = student;
					save(student);
				}
				course = save(course);
			}
			Set<Student> students = course.getStudentsInCourse();
			
			logger.debug("About to dispatch with student array length: " + students.size());
			myelinService.dispatchInit(course, tut, students);
			
			return course;
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
		public boolean deployLesson(String instanceId, Student student, String commitRef) {
			
			CourseInstance enrolledCourse = getDeployedCourse(instanceId);
			Tutorial tut = enrollmentDao.getCurrentTutorialForStudent(student, enrolledCourse);
			
			if(tut == null) {
				logger.error(String.format("Cannot update, student %s for course %s with id %s",
						student.getId(), getDeployedCourse(instanceId).getName(), instanceId) );
				return false;
			}
			
			logger.info("requesting update for student " + student.getId());
			
			myelinService.requestStudentMerge(enrolledCourse, tut.getPrototype(), 
					commitRef, student.getStudentId().toString());
			return true;
		}
		
		/**
		 * This is a placeholder workflow whereby students will be "started", flags will
		 * be set up for course management and links established for source.  Currently
		 * it's a temporary bridge to go from InitializeCourse -> InitializeVMPool
		 */
		public void initializeStudentStatuses(String courseUUID) {
			
			List<Student> students = getStudentsForCourse(courseUUID);
			List<String> studentIds = new ArrayList<String>();
			for(Student student : students) {
				studentIds.add(student.getStudentId());
			}
			
			String append = "-initialPool"; // make property
			int maxLen = 64 - append.length();
			String token = courseUUID.length() < maxLen ? courseUUID+append :
				courseUUID.substring(0, maxLen)+append;
			
			String initRef = AstrocyteUtils.getCheckpointStr(AstrocyteConstants.INITIAL);
				
			myelinService.dispatchVMRequest(courseUUID, initRef, studentIds, token);
		}
		
		// When lesson becomes available for an entire class, as determined by workflow
		public boolean deployLesson(String courseUUID, long tutorialId, String commitRef) {
			CourseInstance course = getDeployedCourse(courseUUID);
			if(course == null) return false;
			
			Tutorial tut = getTutorial(tutorialId);
			myelinService.requestClassMerge(course, tut.getPrototype(), commitRef);
			return true;
		}
		
		@Transactional
		public StudentVM associateStudentVM(String studentId, String host, String location) {
			Student student = getStudentEagerBySID(studentId);
			VM machine = new VM(host, location, VMType.AMAZON_EC2);
			// Was having session state issues, foreign key violation on mapping->VM
			machine = save(machine);
			student = save(student);
			
			StudentVM mapping = new StudentVM();
			mapping.setVM(machine);
			mapping.setStudent(student);
			mapping.setEnrollDate(new Date()); // This is crucial, non-nullable
			// Leaving it in the service layer because I will clear out when using a VM pool
			// nulling the value and forcing an update to this field through hibernate
			student.addMachineMapping(mapping);
			machine.addStudentAssociation(mapping);
			save(mapping);
			save(machine);
			save(student);
			
			return mapping;
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
