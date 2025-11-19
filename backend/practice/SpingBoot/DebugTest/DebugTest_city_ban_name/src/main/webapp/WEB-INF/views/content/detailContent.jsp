<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/views/include/head.jsp"%>
</head>
<body>
	<%@ include file="/WEB-INF/views/include/nav.jsp"%>

	<div class="container p-4">

		<h2>음원 상세 정보</h2>
		<a class="btn btn-warning"
			href="/content/update?code=${content.code}">수정</a>
		<a class="btn btn-danger"
			href="/content/delete?code=${content.code}">삭제</a>
		<table class="table table-striped">
			<tr>
				<td>고유번호</td>
				<td>${content.code}</td>
			</tr>
			<tr>
				<td>음원명</td>
				<td>${content.title}</td>
			</tr>
			<tr>
				<td>장르</td>
				<td>${content.genreName}</td>
			</tr>
			<tr>
				<td>앨범명</td>
				<td>${content.album}</td>
			</tr>
			<tr>
				<td>아티스트</td>
				<td>${content.artist}</td>
			</tr>
			<tr>
				<td>등록자</td>
				<td>${content.userId}</td>
			</tr>
		</table>

	</div>
	<%@ include file="/WEB-INF/views/include/footer.jsp"%>
	</body>
</html>