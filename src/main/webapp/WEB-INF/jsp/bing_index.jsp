<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<style>
ol {
	display: block;
	list-style-type: decimal;
	-webkit-margin-before: 1em;
	-webkit-margin-after: 1em;
	-webkit-margin-start: 0px;
	-webkit-margin-end: 0px;
	-webkit-padding-start: 40px;
}

h3, .med {
	font-size: medium;
	font-weight: normal;
	margin: 0;
	padding: 0;
}

div {
	display: block;
}

.item {
	margin-top: 0;
	margin-bottom: 23px;
	margin-top: 10px;
}

ol li {
	list-style: none;
}

ol, ul, li {
	border: 0;
	margin: 0;
	padding: 0;
}

li {
	line-height: 1.2;
	display: list-item;
	text-align: -webkit-match-parent;
}

.rc {
	position: relative;
}

#rcnt {
	margin-top: 60px;
}

.col {
	float: left;
}

#searchform {
	position: absolute;
	top: 15px;
	width: 100%;
	min-width: 980px;
	z-index: 103;
}

.sfbg {
	background: #f1f1f1;
	height: 69px;
	left: 0;
	position: absolute;
	width: 100%;
}

div.sfbg, div.sfbgg {
	height: 90px;
}

.sfbgg {
	background-color: #f1f1f1;
	border-bottom: 1px solid #666;
	border-color: #e5e5e5;
	height: 69px;
}

#searchform.big>#tsf {
	max-width: 784px;
}

.big form#tsf, form#tsf {
	width: auto;
	max-width: 784px;
	overflow: hidden;
}

form {
	display: block;
	padding-top: 20px;
	margin-left: 250px;
}

.tsf {
	background: none;
}

body {
	color: #000;
	margin: 0;
	overflow-y: scroll;
}

#cnt #center_col, .mw #center_col {
	margin-left: 120px;
}

#center_col {
	float: left;
	width: 528px;
	margin-left: 120px;
}

.big #center_col {
	margin-left: 138px;
	padding: 0 8px;
}

h3 {
	font-size: 18px;
}

.span {
	padding-left: 15px;
}

input[type="text"] {
	width: 300px;
	height: 17px;
}

#resultStats {
	line-height: 43px;
	/* position: absolute; */
	top: 0;
	color: #808080;
	/* padding-left: 16px; */
	padding-top: 30px;
	padding-bottom: 0;
	padding-right: 8px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	width: 1000px;
	margin-left: 120px;
}

#cleared {
	clear: both;
}

</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spread Bing</title>
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.11.3/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.3/jquery-ui.js"></script>
<!-- <script>
  $(function() {
    $("#menu").menu();
  });
</script> -->
</head>
<body>
	<c:url value="/static/logo.png" var="logoUrl" />
	<c:url value="/bing/search" var="searchPath" />
	
	<div class="big" id="searchform">
		<div class="sfbg nojsv"
			style="margin-top: -15px; visibility: visible;">
			
			<div class="sfbgg">
				<img src="${logoUrl}" align="left"/>
			
				<form class="tsf" style="overflow: visible"
					action="${searchPath}" method="GET">
					<label>What are you looking for?</label> <input type="text"
						name="query" size="20" /> <input type="submit" value="Search!" />
					<input type="reset" value="Reset!" />
				</form>
			</div>
		</div>
	</div>

	<c:if test="${not empty results}">
		<div id="rcnt" style="clear: both; position: relative; zoom: 1">
			<div class="col" style="width: 0">
				<div id="resultStats">
					${results.size()} items returned<span> (${time})</span>
				</div>
				<div id="center_col" style="visibility: visible; padding-top: 0px;">

					<ol id="menu">
						<c:forEach var="result" items="${results}">
							<li class="item">
								<div class="rc">
									<h3>
										<font color="blue"><b>${result.title}</b></font>
									</h3>
									<a href="${result.url}"><span>${result.url}</span></a>
									<br>
									<span>${result.shortSummary}</span>

								</div>
							</li>
						</c:forEach>
					</ol>

				</div>
				<div id="cleared"></div>
			</div>

		</div>
	</c:if>

</body>
</html>
