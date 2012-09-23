<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en" >
<html>

   <head>
   <title>Glial Client</title>
   <style type="text/css">
      body {background-color:#b0c4de; }
      iframe {
         overflow: hidden;
         border-style: none;
      }
      #container {position: relative;}
      #left-container{position: absolute; top: 25px; left: 100px; width: 725px; height: 550px;}
      #media {background-color: white; width:700px; height: 450px;}
      #control {}
      #instructions{left: 100px; top: 20px; width: 700px; height: 400px;}
      #portal {position: absolute; right: 400px; width:550px; height:850px;}
   </style>
   <script type="text/javascript">
      var portal, media;
      var steps = ['one', 'two', 'three'];
      var currentStep = 0;

      function nextStep() {
         unloadGameFrame();
         //TODO load next?
         currentStep++;
         loadMedia(getSource());
      }
      
      function getSource() {
         return 'http://localhost/myelin/'+steps[currentStep]+'.html';
      }

      function setGameSource(src) {
         portal.contentWindow.location.replace(src);
      }

      function loadGameFrame() {
         setGameSource('/glial/portal_on.html');
         portal.contentWindow.focus();
      }
      
      function unloadGameFrame() { 
         setGameSource('/glial/portal_init.html');
      }

      function loadMedia(uri) {
         media.contentWindow.location.replace(uri);
      }
      
      function setup() {
         portal = document.getElementById('portal');
         media = document.getElementById('media');
         loadMedia(getSource());
      }

   </script>
   </head>

   <body onload="setup();">
      <div id="container">
         <div id="left-container">
            <iframe src="" id="media">
            This browser does not support iframes.  Required for use.
            </iframe>
            <br /><br />
            <div id="control">
               <input type="button" id="reload" name="reload" value="Load!" onclick="loadGameFrame();" />
               <input type="button" id="next-step" name="next-step" value="next-step" onclick="nextStep()" />
               <input type="button" id="unload" name="unload" value="unload" onclick="unloadGameFrame();" />
            </div>
            <div id="instructions"> 
               <h2>The actions below may be required in the case that I move the git repository OUT of the project.  I don't
               know that I want students working with Git directly...</h2>
               <br />
         		<a href="/glial/jgit?action=init">Check out student repository</a>
         		<br /><br />
         		<a href="/glial/jgit?action=push">Push student repository</a>
         		<br /><br />
         		<a href="/glial/jgit?action=update">Update student repository</a>
            </div>
         </div>
   

         <iframe src="/glial/portal_init.html" id="portal" onmouseover="portal.contentWindow.focus();">
            iframe for game / display
         </iframe>

      </div>
   </body>

</html>