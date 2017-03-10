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
    		width: 60%;
    		margin: 0 auto;
		}
		
		.error {
	        color: red; font-weight: bold;
	    }
	</style>
</head>
<body>
	<div class="container" style="margin: auto; width:70%; background-color: #6495ED; margin-top: 1%;">
		<div class="row" >
			<h1 style="color: white">Hello, ${sessionScope.personName}.</h1>
		</div>

		<div class="row">
			<h4 style="color: white">For each search result below, please indicate whether it is <b>relevant</b> to the query or not. Finally, click on <b>Submit</b> at the end of this page.</h4>
		</div>
		
		<h4 style="color: white"><b>Please</b> do NOT press the button Submit twice; uploading your response may take time!</h4>
		
		<c:if test="${error}">
			<h4 style="color: maroon">Some results are not filled below.</h4>
		</c:if>
	</div>
<!-- 	<ul class="list-group"> -->
<%-- 		<c:forEach var="query" items="${ambiguousQueries}"> --%>
<%-- 			<li class="list-group-item">${query.name}</li> --%>
<%-- 	    </c:forEach> --%>
<!-- 	</ul> -->
	
	<div align="center" class="main-div">
		<form:form action="submit" method="post" modelAttribute="surveyItemsWrapper">
			<table border="0" class="form-group table-striped">
                <tr>
                	<form:hidden path="queryName"/>
                    <td colspan="2" align="center"><h2>Search Results of <code>${surveyItemsWrapper.queryName}</code></h2></td>
                </tr>
                
                <tr style="border-bottom:1pt solid black;">
                    <td><h2>Searh Item</h2></td>
                    <td align="right"><h2>Relevant?</h2></td>
                </tr>
				
				<%-- The dynamic infomative data should not be in the same form data model !! That's why I had to put in the form hidden to bind it again when the clients submits!!--%>
				<c:forEach items="${surveyItemsWrapper.surveyItems}" var="currItem" varStatus="status">
					<tr>
						<td>
							<h3>
								<form:hidden path="surveyItems[${status.index}].title"/>
								<font color="blue"><b>${currItem.title}</b></font>
							</h3>
							
							<form:hidden path="surveyItems[${status.index}].url"/>
							<a href="${currItem.url}"><span>${currItem.url}</span></a>
							
							<br>
							
							<form:hidden path="surveyItems[${status.index}].snippet"/>
							<span>${currItem.snippet}</span>
		                </td>
		                
	                    <%-- Here surveyItems is in surveyItemsWrapper--%>
	                    <td align="right">
	                    	<form:radiobuttons path="surveyItems[${status.index}].answer" items="${choices}" itemLabel="name"/>
	                    </td>
	                    
	                    <!-- 1. I put this here to make sure when submitting to include to bind all items, not only the filled ones -->
	                    <td><form:hidden path="surveyItems[${status.index}].id"/></td> 
	                    <td><form:errors path="surveyItems[${status.index}].answer" cssClass="error" /></td>
	                </tr>
				</c:forEach>
                
                <tr>
                    <td colspan="2" align="right"><button type="submit" class="btn btn-default">Submit!</button></td>
                </tr>
            </table>
		</form:form>
	</div>
	
	
</body>
</html>