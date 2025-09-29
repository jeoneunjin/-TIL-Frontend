<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
    <%@include file="/fragments/header.jsp"%>
    <div class="container">
        <h2>Cookie</h2>
        <a href="${root }/main?action=make-cookie" class="mx-3">쿠키만들기</a>
        <a href="${root }/main?action=check-cookie" class="mx-3">쿠키확인하기</a>
        <a href="${root }/main?action=gugu-form" class="mx-3">구구단</a>
        <hr />
        <h2>Session</h2>
        <a href="${root }/main?action=cart-form" class="mx-3">쇼핑</a>
        <hr />
        <h2>Exception Handling</h2>
        <ul>
            <li>
                <a href="${root }/not_exists">404</a>
            </li>
            <li>
                <a href="${root }/main?action=exception">Exception - 500</a>
            </li>
        </ul>
    </div>
    <%@include file="/fragments/footer.jsp"%>
</body>
</html>
