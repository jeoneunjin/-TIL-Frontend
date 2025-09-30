<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상세 정보 페이지</title>
<style type="text/css">
table, th, td {
	border: 1px solid black;
	border-collapse: collapse;
}
</style>
</head>
<body>
<%@ include file="/header.jsp" %>
	<h1>상세 정보 페이지</h1>

	<a href="#">목록으로</a>
	<table>
		<tr>
			<th>제품 코드</th>
			<td></td>
		</tr>
		<tr>
			<th>제품명</th>
			<td></td>

		</tr>
		<tr>
			<th>가격</th>
			<td></td>
		</tr>
		<tr>
			<th>제조사</th>
			<td></td>
		</tr>
		<tr>
			<th>제품 색</th>
			<td></td>
		</tr>
	</table>
	<a
		href="#">수정</a>
	<a
		href="#">삭제</a>
</body>
</html>