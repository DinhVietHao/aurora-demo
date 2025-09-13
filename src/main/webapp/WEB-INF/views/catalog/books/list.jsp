<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Aurora"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <jsp:include page="/WEB-INF/views/layouts/_head.jsp"/>
        <!-- CSS riêng của trang -->
        <link rel="stylesheet" href="${ctx}/assets/css/book.css?v=1.0.1">
    </head>
    <body>
        <jsp:include page="/WEB-INF/views/layouts/_header.jsp"/>

        <div class="container my-4 book">
            <div class="row">
                <!-- FILTER -->
                <div class="col-md-3">
                    <jsp:include page="/WEB-INF/views/books/_filters.jsp"/>
                </div>

                <!-- CONTENT -->
                <div class="col-md-9 book-content">
                    <div class="book-content-header">
                        <h4 class="book-content-title">
                            <c:out value="${empty title ? 'Tất cả sản phẩm' : title}"/>
                        </h4>
                        <div class="book-content-arrange">
                            <label class="form-label m-0">Sắp xếp</label>
                            <form method="get" class="d-inline">
                                <select class="form-select w-auto" name="sort" onchange="this.form.submit()">
                                    <option value="pop"  ${param.sort=='pop'?'selected':''}>Phổ biến</option>
                                    <option value="best" ${param.sort=='best'?'selected':''}>Bán chạy</option>
                                    <option value="priceAsc"  ${param.sort=='priceAsc'?'selected':''}>Giá thấp đến cao</option>
                                    <option value="priceDesc" ${param.sort=='priceDesc'?'selected':''}>Giá cao đến thấp</option>
                                </select>
                            </form>
                        </div>
                    </div>

                    <!-- GRID SẢN PHẨM -->
                    <div class="row g-3 product">
                        <c:choose>
                            <c:when test="${empty products}">
                                <p>Chưa có bài đăng nào!</p>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="p" items="${products}">
                                    <div class="col-6 col-md-4 col-lg-3">
                                        <a href="${ctx}/book/${p.productId}">
                                            <div class="product-card">
                                                <div class="product-img">
                                                    <c:if test="${p.discount != null && p.discount > 0}">
                                                        <span class="discount">
                                                            -<fmt:formatNumber value="${(p.discount / p.price) * 100}" maxFractionDigits="0"/>%
                                                        </span>
                                                    </c:if>
                                                    <img src="http://localhost:8080/aurora/assets/images/${p.primaryImageUrl}" alt="${p.title}">
                                                </div>
                                                <div class="product-body">
                                                    <h6 class="price">
                                                        <c:choose>
                                                            <c:when test="${p.discount != null && p.discount > 0}">
                                                                <fmt:formatNumber value="${p.price - p.discount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <fmt:formatNumber value="${p.price}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </h6>
                                                    <small class="author">${p.publisher}</small>
                                                    <p class="title">${p.title}</p>
                                                    <div class="rating">
                                                        <i class="bi bi-star-fill text-warning small"></i>
                                                        <i class="bi bi-star-fill text-warning small"></i>
                                                        <i class="bi bi-star-fill text-warning small"></i>
                                                        <i class="bi bi-star-fill text-warning small"></i>
                                                        <i class="bi bi-star-half text-warning small"></i>
                                                        <span>Đã bán ${p.soldCount}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </a>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>


                    <!-- PHÂN TRANG DÙNG CHUNG -->
                    <jsp:include page="/WEB-INF/views/layouts/_pagination.jsp">
                        <jsp:param name="page"       value="${page}"/>
                        <jsp:param name="totalPages" value="${totalPages}"/>
                        <jsp:param name="baseUrl"    value="${ctx}/books"/>
                    </jsp:include>
                </div>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp"/>
        <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp"/>
    </body>
</html>