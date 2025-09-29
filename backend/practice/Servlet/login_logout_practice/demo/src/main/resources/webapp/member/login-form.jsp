<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>Insert title here</title>
</head>
<body>
    <%@ include file="/fragments/header.jsp"%>
    <div class="container">
        <h1>login</h1>
        <form action="${root }/member" method="post" class="m-3">
            <input type="hidden" name="action" value="login" />

            <div class="mb-3 row">
                <label for="email" class="col-sm-2 col-form-label">이메일</label>
                <div class="col-sm-10">
                     <input type="email" id="email" name="email" placeholder="email 입력" class="form-control" required /> 
                </div>
            </div>

            <div class="mb-3 row">
                <label for="pass" class="col-sm-2 col-form-label">비밀번호</label>
                <div class="col-sm-10">
                    <input type="password" name="pass" id="pass" class="form-control" required />
                </div>
            </div>

            <div class="d-flex justify-content-end align-items-center">
                <div class="form-check">
                     <input type="checkbox" value="on" name="remember-me" id="remember-me" class="form-check-input"
                        ${cookie.rememberMe} != null?"checked":""/>
                    <label for="remember-me" class="form-check-label">id 기억하기</label>
                </div>
                <button type="submit" class="btn btn-primary ms-3">로그인</button>
            </div>
        </form>
    </div>
    <%@ include file="/fragments/footer.jsp"%>
</body>
</html>
