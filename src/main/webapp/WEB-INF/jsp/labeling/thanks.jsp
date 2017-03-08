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
	<title>Labeling</title>
	<style type="text/css">
		.main-div {
    		width: 50%;
    		margin: 0 auto;
		}
	</style>
</head>
<body>
	<h1>Thank you, ${sessionScope.personName} for taking time answering this assessment survey.</h1>
	<c:url value="/labeling" var="labelingUrl" />
	<h2>If you want to continue and choose another query to assess, please click <a href="${labelingUrl}">here</a>.</h2>
</body>
</html>