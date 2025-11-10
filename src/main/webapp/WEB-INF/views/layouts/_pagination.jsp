<%@page contentType="text/html" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

      <c:set var="page" value="${empty param.page ? 1 : param.page}" />
      <c:set var="totalPages" value="${empty param.totalPages ? 1 : param.totalPages}" />

      <!-- Build URL động với tất cả tham số hiện tại -->
      <c:set var="currentAction" value="${not empty param.currentAction ? param.currentAction : param.action}" />
      <c:set var="currentSort" value="${param.currentSort}" />
      <c:set var="keyword" value="${param.keyword}" />
      <c:set var="category" value="${param.category}" />
      <c:set var="author" value="${param.author}" />
      <c:set var="publisher" value="${param.publisher}" />
      <c:set var="language" value="${param.language}" />
      <c:set var="minPrice" value="${param.minPrice}" />
      <c:set var="maxPrice" value="${param.maxPrice}" />

      <!-- Build base URL với logic điều kiện -->
      <c:url var="baseUrl" value="${ctx}/home">
        <c:if test="${not empty currentAction}">
          <c:param name="action" value="${currentAction}" />
        </c:if>
        <c:if test="${not empty currentSort}">
          <c:param name="sort" value="${currentSort}" />
        </c:if>
        <c:if test="${not empty keyword}">
          <c:param name="keyword" value="${keyword}" />
        </c:if>
        <c:if test="${not empty category}">
          <c:param name="category" value="${category}" />
        </c:if>
        <c:if test="${not empty author}">
          <c:param name="author" value="${author}" />
        </c:if>
        <c:if test="${not empty publisher}">
          <c:param name="publisher" value="${publisher}" />
        </c:if>
        <c:if test="${not empty language}">
          <c:param name="language" value="${language}" />
        </c:if>
        <c:if test="${not empty minPrice}">
          <c:param name="minPrice" value="${minPrice}" />
        </c:if>
        <c:if test="${not empty maxPrice}">
          <c:param name="maxPrice" value="${maxPrice}" />
        </c:if>
      </c:url>

      <c:set var="prev" value="${page > 1 ? page - 1 : 1}" />
      <c:set var="next" value="${page < totalPages ? page + 1 : totalPages}" />

      <nav class="mt-4" aria-label="Phân trang">
        <ul class="pagination justify-content-center">
          <!-- Previous button -->
          <li class="page-item ${page == 1 ? 'disabled' : ''}">
            <a class="page-link" href="${baseUrl}&page=${prev}" aria-label="Trang trước">‹</a>
          </li>

          <!-- Page numbers -->
          <c:forEach var="i" begin="1" end="${totalPages}">
            <li class="page-item ${i == page ? 'active' : ''}">
              <a class="page-link" href="${baseUrl}&page=${i}">${i}</a>
            </li>
          </c:forEach>

          <!-- Next button -->
          <li class="page-item ${page == totalPages ? 'disabled' : ''}">
            <a class="page-link" href="${baseUrl}&page=${next}" aria-label="Trang sau">›</a>
          </li>
        </ul>
      </nav>