<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/liferay-portlet.tld" prefix="portlet" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<html>
<head>
	<title>Edit Student</title>
</head>

<portlet:defineObjects/>

<portlet:actionURL var="saveStudent">
	<portlet:param name="edit" value="student" />
	<c:if test="${not empty student.id}">
		<portlet:param name="id" value="${student.id}" />
	</c:if>
</portlet:actionURL>

<body onload="startMe();" >
<h1>
	Editing Student ${student.id} - ${student.firstName}  ${student.lastName} 
</h1>
<form:form commandName="student" style="padding:8px" action="<%= saveStudent %>">
    ID - ${student.studentId}<br/>
    <p>
        First Name<br/>
        <form:input path="firstName"/>
    </p>
    <p>
        Last Name<br/>
        <form:input path="lastName"/>
    </p>
     
    <input type="submit" value="Save"/>
</form:form>
</body>
</html>
