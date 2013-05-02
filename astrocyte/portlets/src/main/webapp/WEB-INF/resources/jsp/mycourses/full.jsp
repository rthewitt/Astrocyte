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
	<% Map<String, String> coursePages = (Map<String, String>) request.getAttribute("coursePages"); %>
	<% Set<CourseInstance> courses = (Set<CourseInstance>)request.getAttribute("courses"); %>
		
		<h1 id="mycourses-title">My Courses</h1>
		
		<% for(CourseInstance instance : courses) {  
				VM machine = vMap.get(instance.getCourseUUID());
				String coursePage = coursePages.get(instance.getCourseUUID());
		%>
			<div class="student-course-listing">
			  <a href="<%= coursePage %>>"><span style="display:inline-block"><h1><%= instance.getCourseUUID().split("-")[0] %></h1></span></a><br />
			  <span class="subnote">Go directly to code: <a href="http://<%= machine.getPrivateIP() %>:3131"><%= machine.getHostName() %></a></span><br />
			  <span class="subnote">Verification Link: <a href="http://<%= machine.getPrivateIP() %>" target="_blank"><%= machine.getHostName() %></a></span>
			</div>
		<% } %>
		
	</body>
</html>