<%@page language="java" %>
<%@page import="com.mpi.astro.core.util.AstrocyteConstants" %>
<%@page import="com.mpi.astro.core.model.arcade.impl.JavaGame" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<% 
JavaGame alpha = (JavaGame)request.getAttribute("game"); 
%>
<html>
<head>
<title>Brennan's Alpha Game</title>
</head>
<body>
   <h1>Alpha Game: A Space Shooter</h1><br /><br />The dynamic information from the Java object for today is: <%= alpha.getVendor() %>
    <script src=
      "http://www.java.com/js/deployJava.js"></script>
    <script>
        // using JavaScript to get location of JNLP
        // file relative to HTML page
	deployJava.launchButtonPNG='http://www.freevectorgraphics.org/d/file/201103/81b147bb0534b59dded39b95f5bbd267.png';
        //var dir = location.href.substring(0,
        //    location.href.lastIndexOf('/')+1);
        //var url = dir + "alpha-game.jnlp";
        var url = "/Astrocyte/arcade/game/alpha?format=java-ws";
        deployJava.createWebStartLaunchButton(url, '1.6.0');
    </script>    
</body>
</html>
