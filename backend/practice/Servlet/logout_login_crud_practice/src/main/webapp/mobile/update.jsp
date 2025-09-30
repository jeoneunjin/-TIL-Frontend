<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>정보 수정 페이지</title>
</head>
<body>
<%@ include file="/header.jsp" %>
	<h1>수정 페이지</h1>
	
	<form action="#" method="">
		<fieldset>

			<label> 제품 코드 <input type="text" name="code" value=""></label>
			<br>
			<label> 제품명 <input type="text" name="name" value=""></label>
			<br>
			<label> 제품 가격 <input type="number" name="price" value=""></label>
			<br>
			<label> 제조사 
			<select name="vendor">
					<option value="삼성">삼성</option>
					<option value="애플">애플</option>
					<option value="샤오미">샤오미</option>
			</select></label>
			<br>
			<label>제품 색 <input type = "text" name = "color" value = ""></label>
			<input type="submit" value="수정">
			<br>
			<a href="">상세 페이지</a>
		</fieldset>
	</form>
</body>
</html>