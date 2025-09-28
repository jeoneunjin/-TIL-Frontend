<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
    <%@include file="/fragments/header.jsp"%>
    <div class="container">
        <h1>회원가입</h1>
        <form action="${root }/member" method="post" class="m-3">
            <input type="hidden" name="action" value="member-regist" />
			
			<!-- 이름 -->
            <div class="mb-3 row">
                <label for="name" class="col-sm-2 col-form-label">이름</label>
                <div class="col-sm-10">
                    <input type="text" name="name" id="name" class="form-control" required />
                </div>
            </div>

			<!-- 이메일 -->
            <div class="mb-3 row">
                <label for="email" class="col-sm-2 col-form-label">이메일</label>
                <div class="col-sm-10">
                    <input type="email" name="email" id="email" class="form-control" required />
                    <div class="invalid-feedback">이미 사용중인 email입니다.</div>
                </div>
            </div>
			
			<!-- 비밀번호 -->
            <div class="mb-3 row">
                <label for="password" class="col-sm-2 col-form-label">비밀번호</label>
                <div class="col-sm-10">
                    <input type="password" name="pass" id="pass" class="form-control" required />
                </div>
            </div>
            
            <button type="submit" class="btn btn-primary">등록</button>
        </form>
        <%
        Object error = request.getAttribute("error");
        if (error != null) {
            out.println("<div class='alert alert-danger' role='alert'>" + error + "</div>");
        }
        %>

        <%@include file="/fragments/footer.jsp"%>
    </div>
</body>

 <script>
     document.querySelector("#email").addEventListener("input", async (e)=> {
         const target = e.target;
         const response = await fetch(`${root}/member?action=email-check&email=\${target.value}`);
         const json = await response.json();
         
         console.log(json);
         if(!json.canUse){
             target.classList.add("is-invalid");
             target.classList.remove("is-valid");
         } else {
             target.classList.add("is-valid");
             target.classList.remove("is-invalid");
         }
     })
 
 </script>

<!-- END -->
</html>
