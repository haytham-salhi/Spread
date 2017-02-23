<%@ page session="true" language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
	<h1>Hello ${sessionScope.personName}. You have selected to work on ${sessionScope.searchEngine}</h1>
	
	<h2>Please select a query:</h2>
<!-- 	<ul class="list-group"> -->
<%-- 		<c:forEach var="query" items="${ambiguousQueries}"> --%>
<%-- 			<li class="list-group-item">${query.name}</li> --%>
<%-- 	    </c:forEach> --%>
<!-- 	</ul> -->
	<div class="main-div">
		<table class="table" border="0">
			<c:forEach var="query" items="${ambiguousQueries}">
				<tr>
					<c:url value="/labeling/query/${query.id}" var="queryUrl" />
					<td id="${query.id}"><a href="${queryUrl}">${query.name}</a></td>
					<td>Status</td>
				</tr>
		    </c:forEach>
		</table>
	</div>
	
	
</body>
</html>