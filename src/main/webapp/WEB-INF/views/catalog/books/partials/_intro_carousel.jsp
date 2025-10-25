<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

                <c:set var="ctx" value="${pageContext.request.contextPath}" />
                <c:set var="id" value="${empty param.carouselId ? 'bookIntroduction' : param.carouselId}" />
                <c:set var="chunkSize" value="6" />
                <c:set var="totalSlides"
                    value="${not empty suggestions ? ((fn:length(suggestions) + chunkSize - 1) / chunkSize) : 0}" />

                <div id="${id}" class="carousel slide" data-bs-ride="carousel" data-bs-interval="3000">
                    <div class="carousel-inner">
                        <c:choose>
                            <c:when test="${not empty suggestions}">
                                <c:forEach var="i" begin="0" end="${fn:length(suggestions) - 1}" step="${chunkSize}"
                                    varStatus="chunkStatus">
                                    <div class="carousel-item ${chunkStatus.first ? 'active' : ''}">
                                        <div class="row g-3 product">
                                            <c:forEach var="j" begin="${i}" end="${i + chunkSize - 1}">
                                                <c:if test="${j < fn:length(suggestions)}">
                                                    <c:set var="p" value="${suggestions[j]}" />
                                                    <div class="col-6 col-md-4 col-lg-2">
                                                        <a href="${ctx}/home?action=detail&id=${p.productId}">
                                                            <div class="product-card">
                                                                <div class="product-img">
                                                                    <!-- Discount badge -->
                                                                    <c:if
                                                                        test="${p.originalPrice != null && p.salePrice != null && p.originalPrice > p.salePrice}">
                                                                        <span class="discount">
                                                                            -
                                                                            <fmt:formatNumber
                                                                                value="${p.discountPercent}"
                                                                                maxFractionDigits="0" />%
                                                                        </span>
                                                                    </c:if>
                                                                    <img src="${ctx}/assets/images/catalog/products/${p.primaryImageUrl}"
                                                                        alt="${p.title}" />
                                                                </div>
                                                                <div class="product-body">
                                                                    <!-- Price -->
                                                                    <h6 class="price">
                                                                        <c:choose>
                                                                            <c:when
                                                                                test="${p.salePrice != null && p.originalPrice != null && p.salePrice < p.originalPrice}">
                                                                                <fmt:formatNumber value="${p.salePrice}"
                                                                                    type="currency" currencySymbol="đ"
                                                                                    maxFractionDigits="0" />
                                                                                <span
                                                                                    class="text-muted text-decoration-line-through ms-2">
                                                                                    <fmt:formatNumber
                                                                                        value="${p.originalPrice}"
                                                                                        type="currency"
                                                                                        currencySymbol="đ"
                                                                                        maxFractionDigits="0" />
                                                                                </span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <fmt:formatNumber
                                                                                    value="${p.originalPrice}"
                                                                                    type="currency" currencySymbol="đ"
                                                                                    maxFractionDigits="0" />
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </h6>

                                                                    <!-- Publisher -->
                                                                    <small class="author">
                                                                        <c:choose>
                                                                            <c:when test="${not empty p.publisher}">
                                                                                ${p.publisher.name}
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                &nbsp;
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </small>

                                                                    <!-- Title -->
                                                                    <p class="title">${p.title}</p>

                                                                    <!-- Rating & Sold -->
                                                                    <div class="rating">
                                                                        <c:forEach begin="1" end="5" var="k">
                                                                            <c:choose>
                                                                                <c:when test="${k <= p.avgRating}">
                                                                                    <i
                                                                                        class="bi bi-star-fill text-warning small"></i>
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${k - p.avgRating <= 0.5}">
                                                                                    <i
                                                                                        class="bi bi-star-half text-warning small"></i>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <i
                                                                                        class="bi bi-star text-warning small"></i>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </c:forEach>
                                                                        <span>Đã bán ${p.soldCount}</span>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </a>
                                                    </div>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <!-- Empty state -->
                                <div class="carousel-item active">
                                    <div class="text-center py-5">
                                        <p class="text-muted">Không có sản phẩm liên quan</p>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Navigation buttons - Chỉ hiển thị khi có >6 sản phẩm (>1 slide) -->
                    <c:if test="${fn:length(suggestions) > 6}">
                        <button class="carousel-control-prev" type="button" data-bs-target="#${id}"
                            data-bs-slide="prev">
                            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Previous</span>
                        </button>
                        <button class="carousel-control-next" type="button" data-bs-target="#${id}"
                            data-bs-slide="next">
                            <span class="carousel-control-next-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Next</span>
                        </button>
                    </c:if>
                </div>