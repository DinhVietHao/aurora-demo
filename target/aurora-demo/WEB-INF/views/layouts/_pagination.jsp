<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="page"       value="${empty param.page ? 1 : param.page}"/>
<c:set var="totalPages" value="${empty param.totalPages ? 1 : param.totalPages}"/>
<c:set var="baseUrl"    value="${empty param.baseUrl ? '' : param.baseUrl}"/>

<c:set var="prev" value="${page>1 ? page-1 : 1}"/>
<c:set var="next" value="${page<totalPages ? page+1 : totalPages}"/>

<nav class="mt-4">
  <ul class="pagination justify-content-center">
    <li class="page-item ${page==1?'disabled':''}">
      <a class="page-link" href="${baseUrl}?page=${prev}">‹</a>
    </li>
    <c:forEach var="i" begin="1" end="${totalPages}">
      <li class="page-item ${i==page?'active':''}">
        <a class="page-link" href="${baseUrl}?page=${i}">${i}</a>
      </li>
    </c:forEach>
    <li class="page-item ${page==totalPages?'disabled':''}">
      <a class="page-link" href="${baseUrl}?page=${next}">›</a>
    </li>
  </ul>
</nav>