<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
	<title>Edit Student</title>
	<script type="text/javascript">
		
		function addCourse(courseId) {
			ac = document.getElementById('input-add-courses');
			var append = ac.value.length > 0 ? ',' + courseId : courseId;
			ac.value += append;
			var theLi = document.getElementById('li-'+courseId);
			document.getElementById("my-course-list").innerHTML += 
				"<li class=\"my-course-addition\">TO ADD: " + theLi.innerHTML + "</li>";
			theLi.style.display = 'none';
		}
	</script>
</head>

<body onload="startMe();" >
<h1>
	Editing Student ${student.id} - ${student.firstName}  ${student.lastName} 
</h1>
<form:form commandName="student" style="padding:8px">
    ID - ${student.studentId}<br/>
    <p>
        First Name<br/>
        <form:input path="firstName"/>
    </p>
    <p>
        Last Name<br/>
        <form:input path="lastName"/>
    </p>
    
    <div id="my-courses">
    	<span class="list-header" id="my-courses-title">My Courses</span>
    	<ul id="my-course-list">
    		<c:forEach items="${ student.courseAssociations }" var="assoc">
    			<li class="my-course">${assoc.course.name}</li>
    		</c:forEach>
    	</ul>
    </div>
    
    <input type="hidden" id="input-add-courses" name="add-courses" value="">
    
    <div id="available-courses">
    <span class="list-header" id="my-courses-title">Available Courses</span>
    	<ul>
    		<c:forEach items="${ available }" var="cc">
    			<li id="li-${cc.id}" class="available-course" onclick="addCourse(${cc.id})">${cc.name }</li>
    		</c:forEach>
    	</ul>
    </div>
    
    <input type="submit" value="Save"/>
</form:form>
</body>
</html>
