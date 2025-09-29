<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>사용자 정보</title>
	<link rel="stylesheet" href="${root}/css/member-detail.css">
</head>
<body>
    <%@ include file="/fragments/header.jsp"%>
    <div class="container">
        <div class="container mt-5">
            <div class="row">
                <div class="col-md-5">
                    <div class="card mb-4 profile-card">
					    <div class="card-header">
					        <h4>회원 정보</h4>
					    </div>
					    <div class="card-body">
					        <div class="profile-info">
					            <p><strong>회원번호:</strong> ${member.mno}</p>
					            <p><strong>이름:</strong> ${member.name}</p>
					            <p><strong>이메일:</strong> ${member.email}</p>
					            <p><strong>권한:</strong> ${member.role}</p>
					                <c:choose>
					                    <c:when test="${!empty member.diseases}">
					                        <c:forEach var="d" items="${member.diseases}" varStatus="status">
					                            ${d}<c:if test="${!status.last}">, </c:if>
					                        </c:forEach>
					                    </c:when>
					                    <c:otherwise>없음</c:otherwise>
					                </c:choose>
					            </p>
					        </div>
					
					        <c:if test="${loginUser.email.equals(member.email) || loginUser.role.equalsIgnoreCase('admin')}">
					            <div class="d-flex mt-4 justify-content-center">
					                <form method="post" action="${root}/auth" id="form-delete">
					                    <input type="hidden" name="action" value="member-delete">
					                    <input type="hidden" name="mno" value="${member.mno}">
					                    <input type="hidden" name="email" value="${member.email}">
					                    <button type="button" class="btn btn-primary mx-3" onclick="memberModify()">수정</button>
					                    <button type="button" class="btn btn-danger mx-3" onclick="memberDelete()">삭제</button>
					                </form>
					            </div>
					        </c:if>
					    </div>
					</div>

                </div>
            </div>
        </div>

        <c:if test="${!empty error }">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>
    </div>
    <%@ include file="/fragments/footer.jsp"%>
</body>

<script>
const memberModify = ()=>{
    location.href="${root}/auth?action=member-modify-form&email=${member.email}"
};
</script>

<script>
const memberDelete = ()=>{
	if(confirm("정말 삭제하시겠습니까?")){
		document.getElementById("form-delete").submit();
	}	
};
</script>

<!-- END -->
</html>
