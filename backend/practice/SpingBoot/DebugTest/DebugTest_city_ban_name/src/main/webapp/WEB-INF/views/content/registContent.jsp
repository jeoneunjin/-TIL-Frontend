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

		<h2>음원 정보 등록</h2>
		<form id="registForm" action="/content/regist" method="post">

			<div class="form-group">
				<label for="code">고유번호</label> <input type="text"
					class="form-control" id="code" placeholder="고유번호 입력">
			</div>

			<div class="form-group">
				<label for="title">제목</label> <input type="text"
					class="form-control" id="title" placeholder="제목 입력">
			</div>

			<div class="form-group">
				<label for="genre">장르</label> <select class="form-control"
					id="genre" >
					<c:forEach var="g" items="${genreList}">
						<option value="${g.genreId}">${g.name}</option>
					</c:forEach>
				</select>
			</div>

			<div class="form-group">
				<label for="album">앨범명</label> <input type="text"
					class="form-control" id="album" placeholder="앨범명 입력">
			</div>

			<div class="form-group">
				<label for="artist">아티스트</label> <input type="text"
					class="form-control" id="artist" placeholder="아티스트 입력">
			</div>

			<button type="submit" class="btn btn-primary" id="regist">등록</button>
			<a class="btn btn-secondary" href="/content/list">취소</a>
		</form>



	</div>
	<%@ include file="/WEB-INF/views/include/footer.jsp"%>
	</body>
</html>