<%@ page language="java" contentType="text/html; charset=UTF-8"
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
    		padding-top: 5%;
    		padding-left: 5%; 
		}
	</style>
</head>
<body>
<!-- 	<ul class="list-group"> -->
<%-- 		<c:forEach var="query" items="${ambiguousQueries}"> --%>
<%-- 			<li class="list-group-item">${query.name}</li> --%>
<%-- 	    </c:forEach> --%>
<!-- 	</ul> -->

	<div class="main-div">
<%-- 		<form action="selectQuery" method="get"> --%> <!-- This form or this both wroks-->
		<form:form servletRelativeAction="/labeling/selectQuery" method="post">
			<table border="0" class="form-group">
				 <tr>
				 	<td><label>Your name:</label></td>
				 	<td><input class="form-control" type="text" name="personName" placeholder="e.g., Ahmad"/></td>
				 </tr>
				 <tr>
				 	<td><label>Your email:</label></td>
				 	<td><input class="form-control" type="text" name="email" placeholder="(Optional)"/></td>
				 </tr>
				<tr>
				 	<td><label>Search engine:</label></td>
				 	<td>
				 		<select name="searchEngine" class="form-control">
					        <option value="Google">Google</option>
					        <option value="Bing">Bing</option>
					     </select>
				 	</td>
				 </tr>
			</table>
			<button type="submit" class="btn btn-default">Enter!</button>
		</form:form>
<%-- 		</form> --%>
	</div>
</body>
</html>