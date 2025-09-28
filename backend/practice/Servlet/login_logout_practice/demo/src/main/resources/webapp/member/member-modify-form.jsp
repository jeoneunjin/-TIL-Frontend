<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
    <%@ include file="/fragments/header.jsp"%>
    <div class="container">
        <h1>회원수정</h1>
        <form action="${root}/auth" method="post" class="m-3">
            <input type="hidden" name="action" value="member-modify" />
            <input type="hidden" name="mno" value="${member.mno }" />

            <div class="mb-3 row">
                <label for="name" class="col-sm-2 col-form-label">이름</label>
                <div class="col-sm-10">
                    <input type="text" name="name" id="name" class="form-control" required value="${member.name }" />
                </div>
            </div>

            <div class="mb-3 row">
                <label for="email" class="col-sm-2 col-form-label">이메일</label>
                <div class="col-sm-10">
                    <input type="email" name="email" id="email" class="form-control" readonly value="${member.email }" />
                </div>
            </div>

            <div class="mb-3 row">
                <label for="pass" class="col-sm-2 col-form-label">비밀번호</label>
                <div class="col-sm-10">
                    <input type="password" name="pass" id="pass" class="form-control" required
                        value="${member.password }" />
                </div>
            </div>

            <div class="mb-3 row">
                <label for="role" class="col-sm-2 col-form-label">권한</label>
                <div class="col-sm-10">
                    <input type="text" name="role" id="role" class="form-control" value="${member.role }" />
                </div>
            </div>
            <button type="submit" class="btn btn-primary">수정</button>
        </form>

        <c:if test="${!empty error }">
            <div class="alert alert-danger" role="alert">${error }</div>
        </c:if>
    </div>
    <%@ include file="/fragments/footer.jsp"%>
</body>
</html>
