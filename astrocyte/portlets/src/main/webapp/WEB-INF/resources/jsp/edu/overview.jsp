<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/liferay-portlet.tld" prefix="portlet" %>
<%@ page session="false"%>				
<html>
	<head>
		<title>Astrocyte University</title>
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
	<portlet:actionURL var="deployCourse">
		<portlet:param name="deploy" value="course"></portlet:param>
	</portlet:actionURL>

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
		<br /><br />
	
	<!-- TODO Not yet converted to Liferay actionUrl!-->
		<form name="generateForm" id="generate-form" method="post" action="${deployCourse}">
			<label for="select-course">Select a Course</label>
			<select name="select-course" id="select-course">
				<c:forEach items="${courses}" var="course">
					<option value="${course.id}">${course.name}</option>
				</c:forEach>
			</select>
			<br /><br />
			<label for="select-tutorial">Select a Tutorial</label>
			<select name="select-tutorial" id="select-tutorial">
				<c:forEach items="${tutorials}" var="tutorial">
					<option value="${tutorial.id}">${tutorial.name}</option>
				</c:forEach>
			</select>
			<input type="submit" value="Prepare Course">
		</form>
	</body>
</html>