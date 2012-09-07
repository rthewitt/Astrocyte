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
    	Type<br />
    	${tutorial.type}
    </p>
    <p>
    	Repository Location<br />
    	<form:input path="prototype" />
    </p>
    <p>
        Description<br/>
        <form:input path="description"/>
    </p>
    <p>
    	<!-- Consider adding link to repository browser read-only -->
    	<c:forEach items="${tutorial.lessons}" varStatus="iter" var="less">
    		<span class="lesson-number">Lesson Number: ${less.tagNum}</span><br />
    		<form:hidden path="lessons[${iter.index}].tagNum"/>
    		<form:label path="lessons[${iter.index}].mediaURI">Lesson Media</form:label><br />
    		<form:input path="lessons[${iter.index}].mediaURI"/><br />
    	</c:forEach>
    </p>
    <input type="submit" value="Save"/>
</form:form>
</body>
</html>
