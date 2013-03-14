<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
	<title>Edit Tutorial</title>
</head>
<body>
<% if(request.getAttribute("tutorial") != null) { %>
<h1>
	Editing Tutorial ${tutorial.id} - ${tutorial.name} 
</h1>
<form:form commandName="tutorial" style="padding:8px">
    ID - ${tutorial.id}<br/>
    <p>
        Name<br/>
        <form:input path="name"/>
    </p>
    <p>
        Description<br/>
        <form:input path="description"/>
    </p>
    <p>
    	Repository Location<br />
    	<form:input path="prototype" cssStyle="width:300px;" />
    </p>
    <p>
    	<!-- Consider adding link to repository browser read-only -->
    	<c:forEach items="${tutorial.lessons}" varStatus="iter" var="less">
    		<span class="lesson-number">Lesson number [Indexed]: ${iter.index})</span><br />
    		<form:label path="lessons[${iter.index}].clientJSON">Lesson Media</form:label><br />
    		<form:input path="lessons[${iter.index}].clientJSON" cssStyle="width:300px;" /><br />
    	</c:forEach>
    </p>
    <input type="submit" value="Save"/>
</form:form>
<% }else if(request.getAttribute("container") != null) { %>
<h1>
	Create new Tutorial: 
</h1>
<form:form commandName="container" style="padding:8px" action="new-tutorial">
	<p>
        Name<br/>
        <form:input path="name"/>
    </p>
    <p>
        Description<br/>
        <form:input path="description"/>
    </p>
    <p>
        Git Repository<br/>
        <form:input path="gitRepo"/>
    </p>
    <p>
        Generated Description File Location<br/>
        <form:input path="descriptionFile"/>
    </p>
    <input type="submit" value="Save"/>
</form:form>
<% } else { %>
<h1>
	Serious problem.  Nothing in view.
</h1>
<% } %>
</body>
</html>
