package com.mpi.astro.service.edu;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.transport.stomp.Stomp.Headers.Subscribe;
import org.apache.activemq.transport.stomp.StompConnection;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.mpi.astro.dao.CourseDao;
import com.mpi.astro.dao.StudentDao;
import com.mpi.astro.dao.TutorialDao;
import com.mpi.astro.model.edu.Course;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.model.edu.Tutorial;
import com.mpi.astro.util.AstrocyteConstants;
import com.mpi.astro.util.AstrocyteUtils;
import com.mpi.astro.util.MyelinAction;
import com.mpi.astro.util.PropertiesUtil;

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
	
	// also consider using git build-in email functionality
	public void notifyProfessorPullRequest() {
		// TODO update database?
		// if school has their own ec2 instance / site / control hub, send data to it?
	}
	
	// TODO delete
	public static String dispatchRequest(String msg) throws Exception {
		   
		   String messageReceived = "Currently not listening!";
		   
	      StompConnection connection = new StompConnection();
	      connection.open("localhost",61613);

	      connection.connect("system", "manager");
	      
	      connection.begin("tx1");
	      connection.send("/queue/test", msg, "tx1", null);
//	      connection.send("/queue/test", "me now", "tx1", null);
	      connection.commit("tx1");

	      connection.subscribe("/queue/test", Subscribe.AckModeValues.CLIENT);
	      
//	      connection.begin("tx2");
	      
	      // TODO subscribe to this instead, so that we can log retrieval success
//	      StompFrame message = connection.receive();
	      
//	      messageReceived = message.getBody();
	      
//	      connection.ack(message, "tx2");
	
//	      connection.commit("tx2");

	      connection.disconnect();
	      
	      return messageReceived;
	   }
	
	
	// TODO anticipate collisions, UUID and pass through to script
		public boolean writeStudentsToFile(String fileName) {
			String path = PropertiesUtil.getProperty(PropertiesUtil.PROP_DATA_DIR);
//			File file = new File(path+"/"+fileName);
			List<Student> students = studentDao.getStudents();
			
			
			try {
				File tmp = new File(path+"/"+fileName+".course");
				if(!tmp.exists())
					tmp.createNewFile();
//				File tmp = File.createTempFile(fileName, ".course", new File(path));
				
				FileWriter fw = new FileWriter(tmp);;
				
				// necessary to nest?
				try {
					int len = students.size();
					StringBuilder sb = new StringBuilder();
					StringBuilder nums = new StringBuilder();
					
					sb.append("NUM_STUDENTS=" + len);
					
					// add unix arrays to temp file
					for(int x=0; x<len; x++) {
						Student stud = students.get(x);
						// TODO move userName gen into studentService, add to dao
						String sId = stud.getStudentId();
						String lName = stud.getLastName();
						int nameClip = lName.length() < 5 ?  lName.length() : 5;
						
						String userName = String.format("%-4s", lName.substring(0, nameClip)).toLowerCase().replace(' ', '_')
								+ sId.substring(sId.length()-4);
						
						sb.append("\n");
						sb.append(String.format("names[%d]=\"%s\"", x, userName));
						nums.append("\n");
						nums.append(String.format("nums[%d]=\"%s\"", x, sId));
					}
					
					// add prototypical student
					sb.append("\n");
					nums.append("\n");
					sb.append( String.format("names[%d]=\"%s\"", 
							len, PropertiesUtil.getProperty(PropertiesUtil.PROP_PROTO)) );
					
					nums.append( String.format("nums[%d]=\"%s\"", 
							len, PropertiesUtil.getProperty(PropertiesUtil.PROP_PROTO_ID)) );
					sb.append("\n");
					sb.append(nums.toString());
					
					fw.write(sb.toString());
				} catch(Exception e) {
					throw new IOException(e);
				} finally {
					fw.close();
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
//		============ CREATE A SEPARATE SERVICE FOR ADMIN LEVEL TASKS ================
		
		// Change so that tutorial discovery comes from DB?
		public void initializeCourse(long courseId, long tutorialId) {

			// TODO handle database changes, state information
			
			Course course = getCourse(courseId);
			Tutorial tutorial = getTutorial(tutorialId);
			List<Student> students = getStudentsInCourse(course);
			
			myelinService.dispatchInit(course, tutorial, students);
		}
		
		// make progress a function of student?
		public String getStudentProgressTag(long studentId) {
			return "check-1"; // TODO change this, intermediate value!!
		}
		
		public boolean advanceStudent(long courseId, long tutorialId, long studentId,
				String base) {
			Course course = getCourse(courseId);
			Tutorial tut = getTutorial(tutorialId);
			
			if(course == null || tut == null) return false;
			
			// TODO make progress a function of student?  Not sure
			getStudentProgressTag(studentId);
			
			
			
			
			return true;
		}
		
		public boolean deployLesson(long courseId, long tutorialId, String commitRef) {
			Course course = getCourse(courseId); // Here we'll probably be grabbing commit ref from db
			if(course == null) return false;
			
			Tutorial tut = getTutorial(tutorialId); // TODO get from db instead
			myelinService.dispatchMergeRequest(course, tut.getPrototype(), commitRef);
			return true; // We may subscribe to topic / queue to verify transmission
		}
	
}
