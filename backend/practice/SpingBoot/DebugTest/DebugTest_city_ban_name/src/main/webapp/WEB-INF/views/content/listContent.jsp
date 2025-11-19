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
	<h2>음원 목록</h2>
	<table class="table table-hover">
		<thead>
			<tr>
				<th>고유번호</th>
				<th>음원명</th>
				<th>아티스트</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="content" items="contentList">
				<tr>
					<td>${content.code}</td>
					<td>
						<a href="/content/detail?code=${content.code}">
							${content.title}
						</a>
					</td>
					<td>${content.artist}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

	<%@ include file="/WEB-INF/views/include/footer.jsp"%>
	</body>
</html>