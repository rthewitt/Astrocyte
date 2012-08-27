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
	Editing Student ${tutorial.id} - ${tutorial.name} 
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
    <input type="submit" value="Save"/>
</form:form>
</body>
</html>
