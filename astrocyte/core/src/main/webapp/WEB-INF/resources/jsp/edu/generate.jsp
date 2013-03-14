<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Path Checks</title>
		<style type="text/css">
			div.hidden {visibility:none;} 
		</style>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
		<script type="text/javascript">
			var div="<%= success %>";
			
			function showMessage() {
				alert('<%= request.getContextPath() %>');
				if(document.getElementById(div))
					document.getElementById(div).style.display = 'block';
			}
			
			function persistCourse() {
				// context?
				$.ajax({
					  url: "${pageContext.request.contextPath}/...",
					  context: document.body
					}).done(function() { 
					  $(this).addClass("done");
					});
			}
		</script>
	</head>
	<body onload="showMessage();">
		<div id="success" class="hidden">
		The course ${courseName} was successfully prepared.  Click <a href="${pageContext.request.contextPath}/student/register-course?course=${courseName}">here</a> to initialize.  
		</div>
		<div id=failure" class="hidden">
		Sorry, there was a problem during while preparing ${courseName}...
		</div>
	</body>
</html>