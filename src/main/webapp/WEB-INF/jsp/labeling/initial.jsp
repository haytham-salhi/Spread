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
	<title>Spread: Human Relevance Assessment</title>
	<style type="text/css">
		.main-div {
/*     		padding-top: 5%; */
/*     		padding-left: 5%; */
    		margin: auto;
            width: 30%;
            border: 2px solid;
            border-color: #6495ED;	
            padding: 10px; 
		}
	</style>
</head>
<body>
<!-- 	<ul class="list-group"> -->
<%-- 		<c:forEach var="query" items="${ambiguousQueries}"> --%>
<%-- 			<li class="list-group-item">${query.name}</li> --%>
<%-- 	    </c:forEach> --%>
<!-- 	</ul> -->

	<div class="container" style="margin: auto; width:70%; background-color: #6495ED; margin-top: 1%;">
		<div class="row" style="border: 1px solid;">
			<div class="col-md-6">
				<p style="color: white; text-align: justify;">Hello, this interface is a part of <code>spread</code> framework, an experimental framework being developed as a part of master Thesis at Birzeit University for search results disambiguation, and represents the human relevance assessmenet interface. Its purpose is to let researchers assess the relevance of search results returned by search engines. 

				<br /><br />
				For any further information or if you find any bug/issue, please drop me an email at hsalhi89@gmail.com. I am happy to help in anyway that I can.

				</p>
			</div>
			
			<div class="col-md-6" style="text-align: justify; direction: rtl;">
				<p style="color: white;">  مرحبا, هذه الواجهة هي جزء من نظام <code>سبريد</code>، والذي يُطور كجزء من رسالة ماجيستير في جامعة بيرزيت  بهدف توضيح نتائج البحث. تُمثل هذه الواجهة الواجهة الرئيسية لتقييم نتائج البحث ذات العلاقة, والتي تساعد الباحثين على تقييم نتائج البحث المسترجعة من محركات البحث, لأغراض تقييم طرق توضيح نتائج البحث.

			<br /><br /> <br /><br />
 للمزيد من المعلومات أو اذا تم ايجاد اي خلل فني, الرجاء التواصل على الايميل التالي: hsalhi89@gmail.com. سأكون سعيد للرد على اية استفسارات.
				</p>
			</div>

		</div>

	</div>

	<br />

	<div class="main-div">
<%-- 		<form action="selectQuery" method="get"> --%> <!-- This form or this both wroks-->
		<form:form servletRelativeAction="/assessment/selectQuery" method="post">
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