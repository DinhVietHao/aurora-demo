<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <fmt:setLocale value="vi_VN" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />
                <c:set var="pageTitle" value="${empty shop.name ? 'Aurora Shop' : shop.name}" />

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/view_shop.css?v=1.0.1" />
                </head>

                <body>
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div class="shop container my-3">
                        <!-- Shop Header -->
                        <div class="shop-header">
                            <div class="shop-header__left">
                                <div class="shop-header__avatar">
                                    <img src="${ctx}/assets/images/shops/${empty shop.avatarUrl ? 'default-shop.png' : shop.avatarUrl}"
                                        class="shop-header__avatar-img" alt="${shop.name}"
                                        onerror="this.src='${ctx}/assets/images/shops/default-shop.png';">
                                </div>
                                <div class="shop-header__info">
                                    <h2 class="shop-header__name">
                                        <c:out value="${shop.name}" />
                                    </h2>
                                    <ul class="shop-header__stats">
                                        <li>
                                            <i class="bi bi-box"></i>
                                            <b>
                                                <fmt:formatNumber value="${shop.productCount}" />
                                            </b> Sản phẩm
                                        </li>
                                        <li>
                                            <i class="bi bi-star-fill"></i>
                                            <b>
                                                <fmt:formatNumber value="${shop.avgRating}" maxFractionDigits="1" />
                                            </b>
                                            (
                                            <fmt:formatNumber value="${shop.reviewCount}" /> đánh giá)
                                        </li>
                                        <li>
                                            <i class="bi bi-calendar-event"></i>
                                            <b>
                                                <fmt:formatDate value="${shop.createdAt}" pattern="dd.MM.yyyy" />
                                            </b> Tham gia
                                        </li>
                                    </ul>
                                    <c:if test="${not empty shop.pickupAddress}">
                                        <span class="shop-header__address">
                                            <c:out value="${shop.pickupAddress.ward}" />,
                                            <c:out value="${shop.pickupAddress.district}" />,
                                            <c:out value="${shop.pickupAddress.city}" />
                                        </span>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <!-- Sách bán chạy -->
                        <c:if test="${not empty bestsellerProducts}">
                            <div class="shop-introduction">
                                <h5 class="shop-introduction-title">🔥 Sách bán chạy</h5>

                                <div id="shopBestseller" class="carousel slide" data-bs-ride="carousel">
                                    <div class="carousel-inner">
                                        <c:set var="chunkSize" value="6" />
                                        <c:set var="totalChunks"
                                            value="${(fn:length(bestsellerProducts) + chunkSize - 1) / chunkSize}" />

                                        <c:forEach var="chunkIndex" begin="0" end="${totalChunks - 1}">
                                            <div class="carousel-item ${chunkIndex == 0 ? 'active' : ''}">
                                                <div class="row g-3 product">
                                                    <c:forEach var="product" items="${bestsellerProducts}"
                                                        begin="${chunkIndex * chunkSize}"
                                                        end="${(chunkIndex + 1) * chunkSize - 1}">
                                                        <div class="col-6 col-md-4 col-lg-2">
                                                            <div class="product-card">
                                                                <a
                                                                    href="${ctx}/home?action=detail&id=${product.productId}">
                                                                    <div class="product-img">
                                                                        <c:if test="${product.discountPercent > 0}">
                                                                            <span
                                                                                class="discount">-${product.discountPercent}%</span>
                                                                        </c:if>
                                                                        <img src="${ctx}/assets/images/catalog/products/${product.primaryImageUrl}"
                                                                            alt="${product.title}"
                                                                            onerror="this.src='${ctx}/assets/images/catalog/products/default-product.png';">
                                                                    </div>
                                                                    <div class="product-body">
                                                                        <h6 class="price">
                                                                            <fmt:formatNumber
                                                                                value="${product.salePrice}"
                                                                                type="currency" currencySymbol="₫"
                                                                                groupingUsed="true" />
                                                                        </h6>
                                                                        <small class="author">
                                                                            <c:forEach var="author"
                                                                                items="${product.authors}"
                                                                                varStatus="status">
                                                                                <c:out value="${author.authorName}" />
                                                                                <c:if test="${!status.last}">, </c:if>
                                                                            </c:forEach>
                                                                        </small>
                                                                        <p class="title">
                                                                            <c:out value="${product.title}" />
                                                                        </p>
                                                                        <div class="rating">
                                                                            <c:forEach begin="1" end="5" var="k">
                                                                                <c:choose>
                                                                                    <c:when
                                                                                        test="${k <= product.avgRating}">
                                                                                        <i
                                                                                            class="bi bi-star-fill text-warning small"></i>
                                                                                    </c:when>
                                                                                    <c:when
                                                                                        test="${k - product.avgRating <= 0.5}">
                                                                                        <i
                                                                                            class="bi bi-star-half text-warning small"></i>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <i
                                                                                            class="bi bi-star text-warning small"></i>
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </c:forEach>
                                                                            <span>Đã bán ${product.soldCount}</span>
                                                                        </div>
                                                                    </div>
                                                                </a>
                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>

                                    <c:if test="${fn:length(bestsellerProducts) > chunkSize}">
                                        <button class="carousel-control-prev" type="button"
                                            data-bs-target="#shopBestseller" data-bs-slide="prev">
                                            <span class="carousel-control-prev-icon"></span>
                                        </button>
                                        <button class="carousel-control-next" type="button"
                                            data-bs-target="#shopBestseller" data-bs-slide="next">
                                            <span class="carousel-control-next-icon"></span>
                                        </button>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>

                        <!-- Tủ sách cửa hàng -->
                        <c:if test="${not empty allProducts}">
                            <div class="featured-bookcase container">
                                <h5 class="featured-bookcase-title"><i class="bi bi-bookshelf"></i> Tủ sách cửa hàng
                                </h5>

                                <div class="row g-3 product" id="allProductsContainer">
                                    <c:set var="productsPerRow" value="6" />
                                    <c:set var="totalProducts" value="${fn:length(allProducts)}" />
                                    <c:set var="currentRow" value="0" />

                                    <c:forEach var="i" begin="0" end="${totalProducts - 1}" step="${productsPerRow}">
                                        <c:set var="currentRow" value="${currentRow + 1}" />
                                        <div class="row-item" data-row="${currentRow}"
                                            style="${currentRow > 1 ? 'display: none;' : ''}">
                                            <div class="row g-3">
                                                <c:forEach var="j" begin="${i}" end="${i + productsPerRow - 1}">
                                                    <c:if test="${j < totalProducts}">
                                                        <c:set var="p" value="${allProducts[j]}" />
                                                        <div class="col-6 col-md-4 col-lg-2">
                                                            <a href="${ctx}/home?action=detail&id=${p.productId}">
                                                                <div class="product-card">
                                                                    <div class="product-img">
                                                                        <c:if test="${p.discountPercent > 0}">
                                                                            <span class="discount">-
                                                                                <fmt:formatNumber
                                                                                    value="${p.discountPercent}"
                                                                                    maxFractionDigits="0" />%
                                                                            </span>
                                                                        </c:if>
                                                                        <img src="${ctx}/assets/images/catalog/products/${p.primaryImageUrl}"
                                                                            alt="${p.title}"
                                                                            onerror="this.src='${ctx}/assets/images/catalog/products/default-product.png';">
                                                                    </div>
                                                                    <div class="product-body">
                                                                        <h6 class="price">
                                                                            <fmt:formatNumber value="${p.salePrice}"
                                                                                type="currency" currencySymbol="đ"
                                                                                maxFractionDigits="0" />
                                                                        </h6>
                                                                        <small class="author">
                                                                            <c:forEach var="author" items="${p.authors}"
                                                                                varStatus="status">
                                                                                <c:out value="${author.authorName}" />
                                                                                <c:if test="${!status.last}">, </c:if>
                                                                            </c:forEach>
                                                                        </small>
                                                                        <p class="title">
                                                                            <c:out value="${p.title}" />
                                                                        </p>
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
                                </div>

                                <div class="text-center mt-4">
                                    <button id="loadMoreBtn" class="button-two"
                                        style="${totalProducts <= productsPerRow ? 'display: none;' : ''}">
                                        Xem thêm
                                    </button>
                                    <button id="collapseBtn" class="button-two" style="display: none;">
                                        Thu gọn
                                    </button>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${empty allProducts && empty bestsellerProducts}">
                            <div class="text-center py-5">
                                <i class="bi bi-shop" style="font-size: 4rem; color: #ccc;"></i>
                                <h4 class="mt-3 text-muted">Cửa hàng chưa có sản phẩm nào</h4>
                                <p class="text-muted">Vui lòng quay lại sau</p>
                                <a href="${ctx}/home?action=bookstore" class="btn btn-primary mt-3">
                                    <i class="bi bi-arrow-left"></i> Quay lại Nhà sách
                                </a>
                            </div>
                        </c:if>
                    </div>

                    <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

                    <script>
                        document.addEventListener("DOMContentLoaded", function () {
                            const rows = document.querySelectorAll(".row-item");
                            const loadMoreBtn = document.getElementById("loadMoreBtn");
                            const collapseBtn = document.getElementById("collapseBtn");

                            let visibleRows = 1;

                            if (loadMoreBtn) {
                                loadMoreBtn.addEventListener("click", function () {
                                    visibleRows++;

                                    rows.forEach((row, index) => {
                                        if (index < visibleRows) {
                                            row.style.display = "block";
                                        }
                                    });

                                    if (visibleRows >= rows.length) {
                                        loadMoreBtn.style.display = "none";
                                        collapseBtn.style.display = "inline-block";
                                    }
                                });
                            }

                            if (collapseBtn) {
                                collapseBtn.addEventListener("click", function () {
                                    visibleRows = 1;

                                    rows.forEach((row, index) => {
                                        row.style.display = index === 0 ? "block" : "none";
                                    });

                                    loadMoreBtn.style.display = "inline-block";
                                    collapseBtn.style.display = "none";

                                    document.getElementById("allProductsContainer").scrollIntoView({
                                        behavior: "smooth",
                                        block: "start"
                                    });
                                });
                            }

                            if (rows.length <= 1) {
                                if (loadMoreBtn) loadMoreBtn.style.display = "none";
                                if (collapseBtn) collapseBtn.style.display = "none";
                            }
                        });
                    </script>
                </body>

                </html>