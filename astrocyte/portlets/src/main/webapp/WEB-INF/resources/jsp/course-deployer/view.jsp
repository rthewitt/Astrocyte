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
		 #generate-form {padding-left:100px;}
		 #generate-form input {margin-bottom: 10px;}
  		#feedback { font-size: 1.4em; }
  		#student-select .ui-selecting { background: #FECA40; }
  		#student-select .ui-selected { background: #F39814; color: white; }
  		#student-select { list-style-type: none; margin: 0; padding: 0; width: 60%; }
  		#student-select li { margin: 3px; padding: 0.4em; font-size: 1.4em; height: 18px; }
  		.temp-select {margin-bottom: 10px;}
  	</style>
  	<!-- Adding to all portlets
  	<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
  	<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
  	<script src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script> -->
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
		  enrolled+=strId.id.split('-')[1];
	  });
	  $('#enroll-students').val(enrolled);
	  $('#generate-form').submit();
  }
  	</script>
	</head>

	<portlet:defineObjects />

	<portlet:actionURL var="deployCourse">
		<portlet:param name="deploy" value="course"></portlet:param>
	</portlet:actionURL>
	
	<body>
		<form name="generateForm" id="generate-form" method="post" action="${deployCourse}">
			<input type="hidden" id="enroll-students" name="enroll-students" value=""/>
			<div id="temporary-comb">
			  <div>
			    <label for="select-course">Select a Course</label><br />
			    <select class="temp-select" name="select-course" id="select-course">
				    <c:forEach items="${courses}" var="course">
					    <option value="${course.id}">${course.name}</option>
				    </c:forEach>
			    </select>
			  </div>
			  <div>
			    <label for="select-tutorial">Select a Tutorial</label><br />
			    <select class="temp-select" name="select-tutorial" id="select-tutorial">
				    <c:forEach items="${tutorials}" var="tutorial">
					    <option value="${tutorial.id}">${tutorial.name}</option>
				    </c:forEach>
			    </select>
			  </div>
			</div>
			<div id="temporary-select">
			<ul id="student-select">
				<c:forEach items="${students}" var="student">
					<li class="ui-widget-content" id="student-${student.id}">${student.firstName} ${student.lastName}</li>
				</c:forEach>
				</ul>
			</div>
			<div>
			  <input type="submit" value="Prepare Course" onclick="beforeDeploy(); return false;" style="margin-top: 10px;">
			  <div 	>
			    <label for="stub-out">Testing Only</label><input id="stub-out" name="stub-out" value="stub" type="checkbox" style="margin-left: 5px;"/>
			  </div>
			</div>
		</form>
	</body>
</html>