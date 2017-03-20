<%@ page session="true" language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>	
<html>
<head>
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Spread: Human Relevance Assessment</title>
	<style type="text/css">
		.main-div {
    		width: 50%;
    		margin: 0 auto;
		}
	</style>
	
<meta name="HandheldFriendly" content="true" />
<meta name="MobileOptimized" content="320" />
<meta name="Viewport" content="width=device-width" />
</head>
<body>

	<div class="container" style="margin: auto; width:70%; background-color: lightblue; margin-top: 1%;">
		<h1>Thank you, ${sessionScope.user.name} for taking time answering this relevance assessment.</h1>
		<c:url value="/assessment" var="labelingUrl" />
		<h2>If you want to continue and choose another query to assess, please click <a href="${labelingUrl}">here</a>.</h2>
	</div>
</body>
</html>