<%@ page session="true" language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
	<div class="container" style="margin: auto; width:70%; background-color: #6495ED; margin-top: 1%;">
		<div class="row" >
			<h1 style="color: white">Hello, ${sessionScope.user.name}!</h1>
		</div>

		<div class="row">
			<h2 style="color: white">Please select a query from the list below to start assessing.</h2>
		</div>
	</div>
	
<!-- 	<ul class="list-group"> -->
<%-- 		<c:forEach var="query" items="${ambiguousQueries}"> --%>
<%-- 			<li class="list-group-item">${query.name}</li> --%>
<%-- 	    </c:forEach> --%>
<!-- 	</ul> -->
	<div class="main-div">
		<table class="table" border="0">
			<tr>
                    <td></td>
                    <td><h4>Clear Query</h4></td>
                    <td><h4>Done by</h4></td>
            </tr>
			<c:forEach var="queryView" items="${queryViews}" varStatus="status">
				<tr>
					<c:url value="/assessment/query/${queryView.query.id}" var="queryUrl" />
					<td>${status.index + 1}</td>
					<td id="${queryView.query.id}"><a href="${queryUrl}">${queryView.query.name}</a></td>
					<td>
<%-- 						${queryView.respondents},  --%>
						<small>|</small>
						<c:forEach var="name" items="${queryView.respondentNames}">
							<small>${name} | </small>
						</c:forEach>
					</td>
				</tr>
		    </c:forEach>
		</table>
	</div>
	
	
</body>
</html>