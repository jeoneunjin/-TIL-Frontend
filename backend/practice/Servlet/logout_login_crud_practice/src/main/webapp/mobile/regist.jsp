<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>정보 등록 페이지</title>
</head>
<body>
<%@ include file="/header.jsp" %>
		<h1>등록 페이지</h1>

	<form action="${root }/mobile?action=mobile-regist" method="post">
				<input type="hidden" name="action" value="regist"> <br>
		<fieldset>
			<label> 제품 코드 <input type="text" name="code" maxlength="15"></label> <br>
			<label> 제품명 <input type="text" name="name"></label> <br>
			<label> 제품 가격 <input type="number" name="price"></label> <br>
			<label> 제조사 <select name="vendor">
					<option value="삼성">삼성</option>
					<option value="애플">애플</option>
					<option value="샤오미">샤오미</option>
			</select>
			</label> <br> 
			<label> 제품 색 <input type="text" name="color"></label> <br>
			<input type="submit" value="등록"> <br> <a
				href="${root}/auth?action=mobile-list">목록으로</a>
		</fieldset>
	</form>
</body>
</html>