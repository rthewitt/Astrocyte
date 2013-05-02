<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/liferay-portlet.tld" prefix="portlet" %>
<%@ page session="false"%>				
<html>
	<head>
		<title>Astrocyte University</title>
		<style type="text/css">
			#danger-button {width:100%; overflow:hidden; margin-top:10px; padding-top: 20px;}
		</style>
	</head>
	
	<portlet:defineObjects/>

	<!-- Consider moving this elsewhere -->
	<portlet:renderURL var="editStudent">
		<portlet:param name="edit" value="student"></portlet:param>
	</portlet:renderURL>
	<portlet:renderURL var="editCourse">
		<portlet:param name="edit" value="course"></portlet:param>
	</portlet:renderURL>
	<portlet:renderURL var="editTutorial">
		<portlet:param name="edit" value="tutorial"></portlet:param>
	</portlet:renderURL>

	<body>
		<h1>Students</h1>
			<c:forEach items="${students}" var="v_student">
	   			<a href="<%= editStudent %>&id=${v_student.id}">
	   			${v_student.firstName} ${v_student.lastName}</a>
	   			<br />
			</c:forEach>
			<br /><br />
			<a href="<%= editStudent %>"> Add Student</a>
		<br /><br />
		
		<h1>Courses</h1>
		
		<c:forEach items="${courses}" var="course">
		<a href="<%= editCourse %>&id=${course.id}">${course.id} - ${course.name}</a>
			<br /><div class="description">${course.description}</div>
		</c:forEach>
		<br /><br />
		<a href="<%= editCourse %>"> Add Course</a>
		<br /><br />
		
		<h1>Tutorials</h1>
	
		<c:forEach items="${tutorials}" var="tutorial">
			<a href="<%= editTutorial %>&id=${tutorial.id}">${tutorial.id} - ${tutorial.name }</a>
			<br /><div class="description">${tutorial.description}</div>
		</c:forEach>
		<br /><br />
		<a href="<%= editTutorial %>"> Add Tutorial</a>
		
		<div id="danger-button"><a href=""><img src="/mpi-static/pub/images/big_red_button.png" width="63px" height="58px" /></a></div>
		<div style="width:100%;font-weight:bold;">ABSOLUTELY DO NOT PUSH THIS.</div> (Deletes everything and terminates instances)
	</body>
</html>