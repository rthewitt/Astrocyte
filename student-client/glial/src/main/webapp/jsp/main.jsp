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
      #pull-request{background-color:red; color:white; font-weight:bold;}
   </style>
   <script type="text/javascript" src="/glial/js/jquery-1.7.2.min.js"></script>
   <script type="text/javascript">
      var portal, media, ajaxStatus, pullRequest;
      var steps = ['one', 'two', 'three'];
      var currentStep = 0;

      function nextStep() {
         unloadGameFrame();
         ajaxNextStep();
      }
      
      function ajaxAction(actionName, actionUrl, successText, callback) {
    	  ajaxStatus.text("Calling "+actionName);
    	  $.ajax({
        	  type: "GET",
        	  url: actionUrl,
        	  dataType: "json",
        	  statusCode: {
        		  200: function() {
        			  ajaxStatus.text(actionName+": Success - "+successText);
        			  if(callback !== undefined) callback();
        		  },
        		  500: function() {
        			  // Try searching for my text anyway, consider program errors to be 200 anyway? 
					ajaxStatus.text(actionName+": 500 Error");
        		  }
        	  }
        	});
      }
      
      var feedbackCB = function() {
    	  pullRequest.show();
      };
      
      // These functions could be combined.
      function ajaxNextStep() {
    	  ajaxAction("Next-step", "/glial/jgit?action=next-step", "submission accepted, waiting for feedback.", feedbackCB);
      }
      
      function ajaxInit() {
    	  ajaxAction("Init", "/glial/jgit?action=init", "code-base-updated");
      }
      
      function ajaxCompileSwap() {
    	  ajaxAction("Compile", "/glial/proj?project=betaport&delete=1&action=compile-swap", "Try to load!");
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
         ajaxStatus = $('#action-status');
         pullRequest = $('#pull-request');
         pullRequest.click(function(){
        	 ajaxAction("Update", "", "code-base updated!  May require compilation.");
        	 currentStep++;
             loadMedia(getSource());
             this.hide();
         });
         pullRequest.hide();
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
            	<input type="button" id="reload" name="reload" value="Init" onclick="ajaxInit();" />
               <input type="button" id="reload" name="reload" value="Load!" onclick="loadGameFrame();" />
               <input type="button" id="reload" name="reload" value="Compile" onclick="ajaxCompileSwap();" />
               <input type="button" id="next-step" name="next-step" value="Submit" onclick="nextStep()" />
               <input type="button" id="unload" name="unload" value="unload" onclick="unloadGameFrame();" />
               <br /><div id="pull-request">Lesson Available!</div>
            </div>
            <div id="instructions"> 
            	<span id="action-status"></span>
               <h2>Only use these if necessary, they no longer work asynchronously, if at all.</h2>
               <br />
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