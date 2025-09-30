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
	<span> <a href="#">홈으로</a></span> | 
	<span> <a href="#">로그인</a></span> | 
	<span> <a href="#">목록 조회</a></span> | 
	<span>안녕하세요.  님. </span> | <span> <a
		href="#">로그아웃</a>
	</span> |
</div>
<hr />
<script type="text/javascript">
	const alertMsg = `${param.alertMsg}` || `${alertMsg}`; 
	if (alertMsg) {
		alert(alertMsg);
	}
</script>
