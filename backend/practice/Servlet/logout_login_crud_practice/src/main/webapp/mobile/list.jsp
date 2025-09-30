<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>목록 조회 페이지</title>
<style type="text/css">
table, th, td {
	border: 1px solid black;
	border-collapse: collapse;
}
</style>
</head>
<body>
<%@ include file="/header.jsp" %>
	<h1>목록 조회 페이지</h1>

	<a href="${root}/main">메인 화면으로</a>
	<a href="${root}/mobile?action=mobile-regist-form">등록하기</a>
	<table>
		<thead>
			<tr>
				<th>제품 코드</th>
				<th>제품명</th>
				<th>제품 가격</th>
			</tr>
		</thead>
		 <tbody>
         <c:forEach var="item" items="${mobiles}">
         	<tr>
         		<td>${item.getCode()}</td>
         		<td>${item.getName()}</td>
         		<td>${item.getPrice()}</td>
         	</tr>
         </c:forEach>
      </tbody>
	</table>
</body>
</html>