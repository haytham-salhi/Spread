<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Demo</title>
	<style>
	    .error {
	        color: red; font-weight: bold;
	    }
	</style>
</head>
<body>
	<div align="center">
		<form:form action="intersect" method="post" modelAttribute="demoDataForm">
			<table border="0" class="form-group">
                <tr>
                    <td colspan="2" align="center"><h2>Query Results Intersection Explorer</h2></td>
                </tr>
                <tr>
                	<td><label>Query:</label></td>
                    <td><form:input path="query" cssClass="form-control"/></td>
                    <td><form:errors path="query" cssClass="error" /></td>
                </tr>
                <tr>
                    <td><label>Meanings:</label></td>
                    <td><form:textarea path="meanings" cssClass="form-control" placeholder="Type each meaning in a separate line!" rows="6" cols="50"/></td>
                    <td><form:errors path="meanings" cssClass="error" /></td>
                </tr>
                <tr>
                    <td><label>Query formulation:</label></td>
                    <td><form:select cssClass="form-control" path="queryFormulationStartegy" items="${queryFormulationStartegyList}" itemLabel="name" /></td>
                </tr>
                <tr>
                    <td><label>Search Engine:</label></td>
                    <td><form:select cssClass="form-control" path="searchEngine" items="${searchEngineList}" itemLabel="name" /></td>
                </tr>
                <tr>
                    <td colspan="2" align="right"><button type="submit" class="btn btn-default">Explore!</button></td>
                </tr>
            </table>
		</form:form>
	</div>
	
</body>
</html>
