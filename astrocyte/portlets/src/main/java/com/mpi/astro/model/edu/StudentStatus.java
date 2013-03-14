package com.mpi.astro.model.edu;

// Just a container object to make things cleaner
public class StudentStatus {
	private int tutorialNum;
	private int lessonNum;
	
	public StudentStatus(int tut, int lesson) {
		this.tutorialNum = tut;
		this.lessonNum = lesson;
	}
	
	public StudentStatus(Integer[] intz) {
		if(intz.length != 2) throw new IllegalArgumentException("Incorrect status formulation of length " + intz.length);
		this.tutorialNum = intz[0];
		this.lessonNum = intz[1];
	}
	
	public int getTutorialNum() {
		return tutorialNum;
	}
	public void setTutorialNum(int tutorialNum) {
		this.tutorialNum = tutorialNum;
	}
	public int getLessonNum() {
		return lessonNum;
	}
	public void setLessonNum(int lessonNum) {
		lessonNum = lessonNum;
	}
}