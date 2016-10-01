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

#cnt #center_col, #cnt #foot {
	width: 528px;
}

#searchform {
	position: absolute;
	top: 15px;
	width: 100%;
	min-width: 980px;
	z-index: 103;
}

}
div.sfbg, div.sfbgg {
	height: 59px;
}

.sfbg {
	background: #f1f1f1;
	height: 69px;
	left: 0;
	position: absolute;
	width: 100%;
}

div.sfbg, div.sfbgg {
	height: 50px;
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

.srp #tsf {
	position: relative;
	top: -2px;
}

form {
	display: block;
	padding-top: 20px;
	margin-left: 120px;
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
	padding-top: 0;
	padding-bottom: 0;
	padding-right: 8px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	width: 1000px;
	margin-left: 120px;
}

#wrapper {
	margin-right: 200px;
}

#content {
	float: left;
	width: 100%;
	background-color: #CCF;
}

#sidebar {
	/* float: right; */
	width: 450px;
	margin-right: -200px;
	/* background-color: #FFA; */
	display: inline-block;
	-webkit-box-shadow: 0px 1px 4px 0px rgba(0, 0, 0, 0.2);
	box-shadow: 0px 1px 4px 0px rgba(0, 0, 0, 0.2);
	margin-left: 50px;
	height: 200px;
	padding: 20px;
	display: inline-block;
	-webkit-box-shadow: 0px 1px 4px 0px rgba(0, 0, 0, 0.2);
	box-shadow: 0px 1px 4px 0px rgba(0, 0, 0, 0.2);
	margin-left: 100px;
	height: 600px;
}

#cleared {
	clear: both;
}

.ev-head {
	color: #000;
	font-family: arial, sans-serif-light, sans-serif;
	font-size: 30px;
	font-weight: normal;
	position: relative;
	overflow: hidden;
	-webkit-transform-origin: left;
	transform-origin: left;
}

.ev-item {
	margin-top: 10px;
}

.head-span {
	font-weight: bolder;
	line-height: 1.24;
}

.ev-items {
	margin-top: 40px;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>IR Course Project</title>
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
	
	<img src="${logoUrl}" />
	<div class="big" id="searchform">
		<div class="sfbg nojsv"
			style="margin-top: -15px; visibility: visible;">
			
			<div class="sfbgg">
				<form class="tsf" style="overflow: visible"
					action="/spread/google/search" method="POST">
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
					About ${results.size()} results<span> (${time})</span>
				</div>
				<div id="center_col" style="visibility: visible; padding-top: 0px;">

					<ol id="menu">
						<c:forEach var="result" items="${results}">
							<li class="item">
								<div class="rc">
									<h3>
										<font color="blue"><b>${result.name}</b></font>
									</h3>
									<span><b>Actual class: </b>${result.actualClass}</span><span
										class="span"><b>Predicted class: </b>${result.predictedClass}</span>
									<c:if test="${result.newlyClassified}">
										<span class="span"><mark>
												<font color="blue"><b><i>new</i></b></font>
											</mark></span>
									</c:if>
									<br /> <span> ${result.snippet}</span>

								</div>
							</li>

						</c:forEach>
					</ol>

				</div>
				<div id="sidebar">
					<div class="ev-head">Evaluation Summary</div>
					<div class="ev-items">
						<div class="ev-item">
							<span class="head-span"> Correctly Classified Instances: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="0" value="${evaluation.correct() }" /> - <fmt:formatNumber
									type="number" maxFractionDigits="2"
									value="${evaluation.pctCorrect() }" />%</span>

						</div>
						<div class="ev-item">
							<span class="head-span"> Incorrectly Classified Instances:
							</span><span class="span"><fmt:formatNumber type="number"
									maxFractionDigits="0" value="${evaluation.incorrect() }" /> -
								<fmt:formatNumber type="number" maxFractionDigits="2"
									value="${evaluation.pctIncorrect() }" />%</span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Kappa statistic: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="4" value="${evaluation.kappa() }" /> </span>
						</div>

						<div class="ev-item">
							<span class="head-span"> Mean absolute error: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="4"
									value="${evaluation.meanAbsoluteError() }" /> </span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Root mean squared error: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="4"
									value="${evaluation.rootMeanSquaredError() }" /> </span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Relative absolute error: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="2"
									value="${evaluation.relativeAbsoluteError() }" /> %</span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Root relative squared error: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="2"
									value="${evaluation.rootRelativeSquaredError() }" /> % </span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Coverage of cases (0.95 level): </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="2"
									value="${evaluation.coverageOfTestCasesByPredictedRegions() }" />
								% </span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Mean rel. region size (0.95
								level): </span><span class="span"><fmt:formatNumber
									type="number" maxFractionDigits="2"
									value="${evaluation.sizeOfPredictedRegions() }" /> % </span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Total Number of Instances: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="2" value="${evaluation.numInstances() }" />
							</span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Weighted precision: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="4"
									value="${evaluation.weightedPrecision() }" /> </span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Weighted recall: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="4" value="${evaluation.weightedRecall() }" />
							</span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Weighted Macro F measure: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="4" value="${evaluation.weightedFMeasure() }" />
							</span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Averaged Macro F measure: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="4"
									value="${evaluation.unweightedMacroFmeasure() }" /> </span>
						</div>
						<div class="ev-item">
							<span class="head-span"> Averaged Micro F measure: </span><span
								class="span"><fmt:formatNumber type="number"
									maxFractionDigits="4"
									value="${evaluation.unweightedMicroFmeasure() }" /> </span>
						</div>
					</div>
				</div>
				<div id="cleared"></div>
			</div>

		</div>
	</c:if>

</body>
</html>
