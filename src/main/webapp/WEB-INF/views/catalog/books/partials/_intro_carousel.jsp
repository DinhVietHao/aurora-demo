<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="id"  value="${empty param.carouselId ? 'bookIntroduction' : param.carouselId}" />

<div id="${id}" class="carousel slide" data-bs-ride="carousel">
    <div class="carousel-inner">
        <c:choose>
            <c:when test="${not empty suggestions}">
                <c:forEach var="p" items="${suggestions}" varStatus="s">
                    <c:if test="${s.index % 6 == 0}">
                        <div class="carousel-item ${s.index == 0 ? 'active' : ''}">
                            <div class="row g-3 product">
                            </c:if>

                                <jsp:include page="/WEB-INF/views/cart/partials/_card.jsp">
                                    <jsp:param name="id"       value="${p.id}"/>
                                    <jsp:param name="title"    value="${p.title}"/>
                                    <jsp:param name="author"   value="${p.author}"/>
                                    <jsp:param name="price"    value="${p.priceFormatted}"/>
                                    <jsp:param name="img"      value="${p.imageUrl}"/>
                                    <jsp:param name="discount" value="${p.discountPercent}"/>
                                    <jsp:param name="sold"     value="${p.sold}"/>
                                </jsp:include>

                            <c:if test="${s.index % 6 == 5 || s.last}">
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <!-- Demo 2 slide x 6 sản phẩm khi chưa có dữ liệu -->
                <div class="carousel-item active">
                    <div class="row g-3 product">
                        <c:forEach begin="1" end="6">
                                <jsp:include page="/WEB-INF/views/cart/partials/_card.jsp">
                                    <jsp:param name="id" value="1"/>
                                    <jsp:param name="title" value="Nơi Khu Rừng Chạm Tới Những Vì Sao"/>
                                    <jsp:param name="author" value="GLENDY VANDERAH"/>
                                    <jsp:param name="price" value="128.300 ₫"/>
                                    <jsp:param name="img" value="${ctx}/assets/images/product-1.png"/>
                                    <jsp:param name="discount" value="16"/>
                                    <jsp:param name="sold" value="99"/>
                                </jsp:include>
                        </c:forEach>
                    </div>
                </div>
                <div class="carousel-item">
                    <div class="row g-3 product">
                        <c:forEach begin="1" end="6">
                            <jsp:include page="/WEB-INF/views/cart/partials/_card.jsp">
                                <jsp:param name="id" value="2"/>
                                <jsp:param name="title" value="Tựa sách khác (demo)"/>
                                <jsp:param name="author" value="Tác giả"/>
                                <jsp:param name="price" value="128.300 ₫"/>
                                <jsp:param name="img" value="${ctx}/assets/images/product-2.jpg"/>
                                <jsp:param name="discount" value="12"/>
                                <jsp:param name="sold" value="292"/>
                            </jsp:include>
                        </c:forEach>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Nút điều hướng -->
    <button class="carousel-control-prev" type="button" data-bs-target="#${id}" data-bs-slide="prev">
        <span class="carousel-control-prev-icon"></span>
    </button>
    <button class="carousel-control-next" type="button" data-bs-target="#${id}" data-bs-slide="next">
        <span class="carousel-control-next-icon"></span>
    </button>
</div>