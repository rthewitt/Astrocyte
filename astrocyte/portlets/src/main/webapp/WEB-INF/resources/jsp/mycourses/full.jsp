<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/liferay-portlet.tld" prefix="portlet" %>
<%@ page session="false"%>			
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.mpi.astro.core.model.vm.VM" %>	
<%@ page import="com.mpi.astro.core.model.edu.CourseInstance" %>		
<html>
	<head>
		<title>Student Courses</title>
	</head>
	
	<portlet:defineObjects/>
	<% Map<String, VM> vMap = (Map<String, VM>) request.getAttribute("vMap"); %>
	<% Set<CourseInstance> courses = (Set<CourseInstance>)request.getAttribute("courses"); %>
		
		<h1>My Courses</h1>
		
		<% for(CourseInstance instance : courses) {  
				VM machine = vMap.get(instance.getCourseUUID());
		%>
			<div class="student-course-listing">
			  <a href="http://<%= machine.getPrivateIP() %>:3131"><h1><%= instance.getCourseUUID().split("-")[0] %></h1></a>
			  <span class="subnote">Virtual Machine: <a href="http://<%= machine.getPrivateIP() %>" target="_blank"><%= machine.getHostName() %></a></span>
			</div>
		<% } %>
		
	</body>
</html>