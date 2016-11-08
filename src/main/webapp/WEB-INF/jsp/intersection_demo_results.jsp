<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Demo</title>
	<style type="text/css">
		div {
    		width: 50%;
    		margin: 0 auto; 
		}
	</style>
</head>
<body>
	<div align="center">
		<table class="table" border="0">
            <tr>
                <td colspan="2" align="center">
                    <h3>Here's the results for ${demoDataForm.query}:</h3>
                </td>
            </tr>
            <tr>
                <td><label>Query:</label></td>
                <td>${demoDataForm.query}</td>
            </tr>
            <tr>
                <td><label>Search engine:</label></td>
                <td>${demoDataForm.searchEngine.name}</td>
            </tr>
            <tr>
                <td><label>Result set size of ${demoDataForm.query}:</label></td>
                <td>${size}</td>
            </tr>
            
            <c:forEach var="msr" items="${meaningSearchResults}">
            	<tr>
	                <td><label>Result set size of ${msr.key}:</label></td>
	                <td>${msr.value.searchItemsSize}</td>
           		 </tr>
            </c:forEach>
            
            <c:forEach var="mi" items="${meaningIntersections}">
            	<tr>
	                <td><label style="color:blue">Common items between ${demoDataForm.query} and ${mi.key}:</label></td>
	                <td style="color:blue">${mi.value}</td>
           		 </tr>
            </c:forEach>
        </table>
	</div>
</body>
</html>
