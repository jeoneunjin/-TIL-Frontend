<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>로그인 페이지</title>
</head>
<body>
	<%@ include file="/header.jsp"%>
	<div class="container">
		<h1>로그인 페이지</h1>
		<form action="#" method="">
			
			<fieldset>

			<div>
				<label for="email">이메일 </label> 
				<input type="email" id="email" name="email" placeholder="email을 입력하세요">
				</div>
				<div>
				<label for="pass">비밀번호 </label>
				<input type="password" id="pass" name="pass"/> <br>
				</div>
				<div>
					<label> <input type="checkbox" name="remember-me"> 아이디 기억하기
					</label>
				</div>
				<input type="submit" value="로그인"> <br>
			</fieldset>

		</form>
	</div>
</body>
</html>
