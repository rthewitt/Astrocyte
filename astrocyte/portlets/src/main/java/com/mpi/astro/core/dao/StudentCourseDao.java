package com.mpi.astro.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.model.edu.StudentCourse;
import com.mpi.astro.core.model.edu.StudentStatus;
import com.mpi.astro.core.model.edu.Tutorial;

@Repository
public class StudentCourseDao {
	
	@PersistenceContext
	public EntityManager entityManager;
	
	public StudentCourse find(Long id) {
		return entityManager.find(StudentCourse.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<StudentCourse> getAllEnrollments() {
		return entityManager.createQuery("select p from StudentCourse p").getResultList();
	}
	
	public StudentCourse getEnrollment(Student student, CourseInstance course) {
		return (StudentCourse)entityManager.createQuery("select sc from StudentCourse sc " +
				"where sc.pk.student = :student and sc.pk.course = :course")
				.setParameter("student", student).setParameter("course", course).getSingleResult();
	}
	
	@Transactional
	public StudentCourse save(StudentCourse enrollment) {
		if (enrollment.getPk().getCourse().getId() == null || enrollment.getPk().getStudent().getId() == null) {
			entityManager.persist(enrollment);
			return enrollment;
		} else {
			return entityManager.merge(enrollment);
		}		
	}	
	
	public StudentStatus getStudentStatus(Student student, long instanceId){
		@SuppressWarnings("unchecked")
		List<Integer> statz = (List<Integer>)entityManager.createQuery("select sc.tutorialNum, sc.lessonNum " +
				"from StudentCourse sc where sc.pk.student = :stud and sc.pk.course.id = :course_id")
				.setParameter("stud", student).setParameter("course_id", instanceId).getResultList();
		return new StudentStatus(statz.get(0), statz.get(1));
	}
	
	public StudentStatus getStudentStatus(Student student, String courseUUID){
		@SuppressWarnings("unchecked")
		List<Integer> statz = (List<Integer>)entityManager.createQuery("select sc.tutorialNum, sc.lessonNum " +
				"from StudentCourse sc where sc.pk.student = :stud and sc.pk.course.courseUUID = :course_uuid")
				.setParameter("stud", student).setParameter("course_uuid", courseUUID).getResultList();
		return new StudentStatus(statz.get(0), statz.get(1));
	}
	
	public StudentStatus getStudentStatus(Student student, CourseInstance course){
		@SuppressWarnings("unchecked")
		List<Object[]> rows = (List<Object[]>)entityManager.createQuery("select sc.tutorialNum, sc.lessonNum " +
				"from StudentCourse sc where sc.pk.student = :stud and sc.pk.course = :course")
				.setParameter("stud", student).setParameter("course", course).getResultList();
		// TODO throw exception if doesn't exist!
		Object[] statz = (Object[])rows.get(0);
		return new StudentStatus((Integer)statz[0], (Integer)statz[1]);
	}
	
	// TODO fix this, almost certainly broken by inheritance
	@Transactional
	public Tutorial getCurrentTutorialForStudent(Student student, CourseInstance course) {
		
		StudentCourse enrollment = getEnrollment(student, course);
		
		/*
		String originalQuery = "select t from Tutorial t " +
				"inner join t.courseAssociations as ct " +
				"inner join ct.pkey.course as c " +
				"inner join c.studAssociations as sc " +
				"where ct.pkey.course = sc.pk.course " +
				"and ct.order = sc.tutorialNum " +
				"and ct.pkey.tutorial = t " +
				"and sc = :enrollment"; */
		
		String query = "select t from Tutorial t " +
				"inner join t.courseAssociations as ct " +
				"inner join ct.pkey.course as c " +
				"inner join c.studAssociations as sci " +
				// HERE BE BROKEN
				"where ct.pkey.course = sci.pk.course.syllabus " +
				"and ct.order = sci.tutorialNum " +
				"and ct.pkey.tutorial = t " +
				"and sci = :enrollment";
				// HERE BE DRAGONS
		Tutorial lazyTut =  (Tutorial)entityManager.createQuery(query)
		.setParameter("enrollment", enrollment).getSingleResult();
		
		if(lazyTut.getLessons().size() > 0) 
			lazyTut.getLessons().get(0); // Eager
		return lazyTut;
	}
	
	public void clearForTest(){
		entityManager.clear();
	}
}
