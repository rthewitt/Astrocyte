<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/liferay-portlet.tld" prefix="portlet" %>
<%@ page session="false"%>	

<html>
	<head>
		<title>Course Deployer</title>
		 <style>
  		#feedback { font-size: 1.4em; }
  		#student-select .ui-selecting { background: #FECA40; }
  		#student-select .ui-selected { background: #F39814; color: white; }
  		#student-select { list-style-type: none; margin: 0; padding: 0; width: 60%; }
  		#student-select li { margin: 3px; padding: 0.4em; font-size: 1.4em; height: 18px; }
  	</style>
  	<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
  	<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
  	<script src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
 	 <script>
  /*$(document).ready(function(){
	  $("#student-select").selectable();
  }); */
  $(function() {
	    $( "#student-select" ).selectable();
	  });
  
  function beforeDeploy() {
	  var enrolled = '';
	  $("#temporary-select .ui-selected").each(function(i, strId){
		  if(i>0) enrolled+=',';
		  enrolled+=strId.split('-')[1];
	  });
	  $();
  }
  	</script>
	</head>

	<portlet:defineObjects />

	<portlet:actionURL var="deployCourse">
		<portlet:param name="deploy" value="course"></portlet:param>
	</portlet:actionURL>
	
	<body>
		<span id="astro-remember" style="background-color:red; font-weight: bold;">jquery should not be in portlet!</span>
		<form name="generateForm" id="generate-form" method="post" action="${deployCourse}">
			<input type="hidden" id="enroll-students" name="enroll-students" value=""/>
			<label for="select-course">Select a Course</label>
			<select name="select-course" id="select-course">
				<c:forEach items="${courses}" var="course">
					<option value="${course.id}">${course.name}</option>
				</c:forEach>
			</select>
			
			<div id="temporary-select">
			<br /><br />
			<span style="font-weight: bold;">Temporarily, the tutorial must be selected manually:</span>
			<br /><br />
			<label for="select-tutorial">Select a Tutorial</label>
			<select name="select-tutorial" id="select-tutorial">
				<c:forEach items="${tutorials}" var="tutorial">
					<option value="${tutorial.id}">${tutorial.name}</option>
				</c:forEach>
			</select>
			<ul id="student-select">
				<c:forEach items="${students}" var="student">
					<li class="ui-widget-content" id="student-${student.id}">${student.firstName} ${student.lastName}</li>
				</c:forEach>
				</ul>
			</div>
			<input type="submit" value="Prepare Course" onclick="beforeDeploy(); return false;">
		</form>
	</body>
</html>