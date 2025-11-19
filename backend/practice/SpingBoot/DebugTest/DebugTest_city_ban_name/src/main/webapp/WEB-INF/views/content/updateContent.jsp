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

		<h2>음원 정보 수정</h2>
		<form id="registForm" action="/content/update" method="post">

			<div class="form-group">
				<label for="code">고유번호</label> <input type="text"
					class="form-control" id="code" name="code" value="${content.code}"
					readonly="readonly">
			</div>

			<div class="form-group">
				<label for="title">제목</label> <input type="text"
					class="form-control" id="title" name="title" placeholder="제목 입력"
					value="${content.title}">
			</div>

			<div class="form-group">
				<label for="genre">장르</label> <select class="form-control"
					id="genre" name="genre">
					<c:forEach var="g" items="${genreList}">
						<option value="${g.genreId}"
							<c:if test="${g.name eq content.genreName}">selected</c:if>>
							${g.name}</option>
					</c:forEach>
				</select>
			</div>

			<div class="form-group">
				<label for="album">앨범명</label> <input type="text"
					class="form-control" id="album" name="album" placeholder="앨범명 입력"
					value="${content.album}">
			</div>

			<div class="form-group">
				<label for="artist">아티스트</label> <input type="text"
					class="form-control" id="artist" name="artist"
					placeholder="아티스트 입력" value="${content.artist}">
			</div>

			<button type="submit" class="btn btn-primary" id="regist">수정</button>
			<a class="btn btn-secondary"
				href="/content/detail?code=${content.code}">취소</a>
		</form>

	</div>
	<%@ include file="/WEB-INF/views/include/footer.jsp"%>
	</body>
</html>