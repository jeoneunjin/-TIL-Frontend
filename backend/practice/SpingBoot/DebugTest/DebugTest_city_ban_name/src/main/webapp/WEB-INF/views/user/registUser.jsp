<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

    <%@ include file="/WEB-INF/views/include/head.jsp"%>

    <script>
        function validateForm() {
            var pw = document.getElementById("pw");
            var pwConfirm = document.getElementById("pwConfirm");
            if (pw.value !== pwConfirm.value) {
                alert("비밀번호가 일치하지 않습니다. 다시 확인해주세요.");
                pw.value = "";
                pwConfirm.value = "";
                pw.focus();  
                return false;  
            }else{
            	return true;
            }
        }
    </script>
</head>
<body>
    <%@ include file="/WEB-INF/views/include/nav.jsp"%>
    <div class="container p-4">
        <h2>회원가입</h2>
        <form action="/user/regist" method="post" onsubmit="return validateForm();">

            <div class="form-group">
                <label for="id">ID:</label>
                <input type="text" class="form-control" id="id" name="id" placeholder="아이디 입력" required>
            </div>

            <div class="form-group">
                <label for="pw">비밀번호:</label>
                <input type="password" class="form-control" id="pw" name="pw" placeholder="비밀번호 입력" required>
            </div>

            <div class="form-group">
                <label for="pwConfirm">비밀번호 확인:</label>
                <input type="password" class="form-control" id="pwConfirm" name="pwConfirm" placeholder="비밀번호 입력" required>
            </div>

            <div class="form-group">
                <label for="name">이름:</label>
                <input type="text" class="form-control" id="name" name="name" placeholder="이름 입력" required>
            </div>

            <div class="form-group">
                <label for="birthdate">생년월일:</label>
                <input type="date" class="form-control" id="birthdate" name="birthdate" placeholder="YYYY-MM-DD 형식으로 입력" required>
            </div>

            <div class="form-group">
                <label for="phone">전화번호:</label>
                    <input type="text" class="form-control" id="phone" name="phone" 
				        placeholder="010-0000-0000 형식으로 입력" required
				        pattern="010-\d{4}-\d{4}" 
				        title="전화번호는 010-0000-0000 형식으로 입력해야 합니다.">
            </div>

            <div class="form-group">
                <label for="email">이메일:</label>
                <input type="email" class="form-control" id="email" name="email" placeholder="이메일 입력" required>
            </div>
            <br>
            <button type="submit" class="btn btn-primary">회원가입</button>
            <a class="btn btn-secondary" href="/">메인 페이지로</a>
        </form>
    </div>
    <%@ include file="/WEB-INF/views/include/footer.jsp"%>
</body>
</html>
