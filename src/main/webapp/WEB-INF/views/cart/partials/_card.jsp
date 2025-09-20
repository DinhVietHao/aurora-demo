<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<%-- <div class="col-6 col-md-4 col-lg-2">
  <div class="product-card">
    <a href="${ctx}/book/${param.id}">
      <div class="product-img">
        <c:if test="${not empty param.discount}">
          <span class="discount">-${param.discount}%</span>
        </c:if>
        <img
          src="${empty param.img ? (ctx+'/assets/images/catalog/product/product-1.png') : param.img}"
          alt="Sách"
        />
      </div>
    </a>
    <div class="product-body">
      <h6 class="price"><c:out value="${param.price}" /></h6>
      <small class="author"><c:out value="${param.author}" /></small>
      <p class="title"><c:out value="${param.title}" /></p>
      <div class="rating">
        <i class="bi bi-star-fill text-warning small"></i>
        <i class="bi bi-star-fill text-warning small"></i>
        <i class="bi bi-star-fill text-warning small"></i>
        <i class="bi bi-star-fill text-warning small"></i>
        <i class="bi bi-star-half text-warning small"></i>
        <span>Đã bán <c:out value="${empty param.sold ? 0 : param.sold}" /></span>
      </div>
    </div>
  </div>
</div> --%>
