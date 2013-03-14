<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>List Students</title>
</head>
<body>
<h1>Listing Students</h1>
<c:forEach items="${students}" var="v_student">
   <a href="edit?id=${v_student.id}">${v_student.studentId} -
   ${v_student.firstName} ${v_student.lastName}</a>
   <br />
</c:forEach>
<a href="edit"> Add Student</a>
<br /><br />
<form id="just-in-case" method="post" action="${pageContext.request.contextPath}/student/generate-course">
<label for="course-name">Enter a Course Name</label>
<input name="course-name" id="input-course-name" type="text" maxlength="7">
<input type="submit" value="Prepare Course">
</form>
</body>
</html>