<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
    <%@ include file="/fragments/header.jsp"%>
    <div class="container">
        <h1>멤버 목록</h1>
        <form class="row mb-3" action="${root }/auth" method="get" id="search-form">
            <input type="hidden" name="action" value="member-list" />
            <input type="hidden" id="currentPage" name="currentPage" value="1" />
            <div class="d-flex justify-content-end">
                <select class="form-select w-25" name="key">
                    <optiondisabled ${emptyparam.key?'selected':'' }>검색항목 선택</option>
                    <option value="1" ${param.key=='1'?'selected':'' }>name</option>
                    <option value="2" ${param.key=='2'?'selected':'' }>email</option>
                </select>
                <input type="text" class="form-control w-25" name="word" value="${param.word}">
                <button type="submit" class="btn btn-primary">검색</button>
            </div>
        </form>
        <table class="table">
            <tbody>
                <tr>
                    <td>no</td>
                    <td>name</td>
                    <td>email</td>
                </tr>
                <c:forEach items="${page.list }" var="item">
                    <tr>
                        <td>${item.mno }</td>
                        <td>${item.name }</td>
                        <!-- TODO: 01-02. 상세정보를 위한 링크를 작성하세요.-->
                        <td>
                        <a href="${root }/auth?action=member-detail&email=${item.email}" class="mx-3">${item.email }</a>
                        </td>
						
                        <!--END-->
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <nav class="d-flex justify-content-center">
            <ul class="pagination">
                <!-- 이전 버튼 -->
                <c:if test="${page.hasPre}">
                    <li class="page-item">
                        <a class="page-link" href="#" data-page="${ page.startPage-1}">이전</a>
                    </li>
                </c:if>

                <!-- 페이지 번호 -->
                <c:forEach begin="${page.startPage}" end="${page.endPage}" var="item">
                    <li class="page-item ${page.condition.currentPage == item ? 'active' : ''}">
                        <a class="page-link" href="#" data-page="${ item}">${item}</a>
                    </li>
                </c:forEach>

                <!-- 다음 버튼 -->
                <c:if test="${page.hasNext}">
                    <li class="page-item">
                        <a class="page-link" href="#" data-page="${ page.endPage+1}">다음</a>
                    </li>
                </c:if>
            </ul>
        </nav>
    </div>
    <%@ include file="/fragments/footer.jsp"%>
</body>
<script>
const pageLinks = document.querySelectorAll(".pagination a");
pageLinks.forEach(link =>{
  link.addEventListener("click", (e)=>{
    e.preventDefault(); //a 링크의 기본 동작 중지
    document.querySelector("#currentPage").value=link.dataset.page; // 링크에 설정된 data 속성으로 form의 page 수정
    document.querySelector("#search-form").submit(); // form sumbit
  })
})
</script>
</html>
