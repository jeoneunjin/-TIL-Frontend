<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<nav class="navbar navbar-expand-sm bg-dark navbar-dark">
	<ul class="navbar-nav me-auto">
		<li class="nav-item"><a class="nav-link"
			href="/">메인</a></li>
		<li class="nav-item"><a class="nav-link"
			href="/content/list">음원 목록</a></li>
		<li class="nav-item"><a class="nav-link"
			href="/content/regist">음원 정보 등록</a></li>
	</ul>
	<c:choose>
		<c:when test="${empty userInfo}">
			<ul class="navbar-nav">
				<li class="nav-item"><a class="nav-link"
					href="/user/login">로그인</a></li>
			</ul>
		</c:when>
		<c:otherwise>
			<ul class="navbar-nav">
				<li class="nav-item"><a class="nav-link" href="/user/detail">${userInfo.name}님 반갑습니다.</a></li>
				<li class="nav-item"><a class="nav-link"
					href="/user/logout">로그아웃</a></li>
			</ul>
		</c:otherwise>
	</c:choose>
</nav>