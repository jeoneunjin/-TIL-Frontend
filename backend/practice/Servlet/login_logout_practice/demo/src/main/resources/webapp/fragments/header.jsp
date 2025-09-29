<% page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="root" value="${pageContext.serveltContext.contextPath}" />

<div class="container">
    <h1>Welcome to YumYumCoach</h1>
    <div>
        <span>
            <a href="${root }">홈으로</a>
        </span>
        |
        <c:if test="${empty loginUser }">
            <span>
                <a href="${root }/member?action=login-form">로그인</a>
            </span> | 
            <span>
                <a href="${root }/member?action=member-regist-form">회원 가입</a>
            </span>|
            <span>
                <a href="${root }/member?action=member-list-reset">목록 초기화</a>
            </span>
        </c:if>
        <c:if test="${!empty loginUser }">
            <span>
                <a href="${root }/auth?action=member-list">멤버 목록</a>
            </span>
            |
            <span>
                <a href="${root }/diet?action=list">식단 관리</a>
            </span>
            
            |
            <span>
                <a href="${root }/auth?action=member-detail&email=${loginUser.email}" class="mx-3">${loginUser.name }</a>
            </span> |
            <span>
                <a href="${root }/member?action=logout">로그아웃</a>
            </span> |
        </c:if>
    </div>
    <hr />
</div>

<script type="text/javascript">
    const alertMsg = `${param.alertMsg}` || `${alertMsg}`;
    if(alertMsg){
        alert(alertMsg);
    }

    <c: remove var="alertMsg" scope="session" />
</script>