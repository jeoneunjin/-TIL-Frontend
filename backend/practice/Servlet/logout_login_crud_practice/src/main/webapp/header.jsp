<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="root" value="${pageContext.servletContext.contextPath }" />
<style>
a {
	text-decoration: none;
}
</style>
<div>
	<span> <a href="${root }/main">홈으로</a></span> | 
	<c:if test="${empty loginUser}">
		<span> <a href="${root}/member?action=login-form">로그인</a></span> | 
	</c:if>
	<span> <a href="${root}/auth?action=mobile-list">목록 조회</a></span> 
	<c:if test="${!empty loginUser}">
	| <span>안녕하세요. ${loginUser.getName()}님. </span> | <span> <a
		href="${root }/member?action=logout">로그아웃</a>
	</span> |
	</c:if>
</div>
<hr />
<script type="text/javascript">
	const alertMsg = `${param.alertMsg}` || `${alertMsg}`; 
	if (alertMsg) {
		alert(alertMsg);
	}
	<c:remove var="alertMsg" scope="session"/> 
</script>
