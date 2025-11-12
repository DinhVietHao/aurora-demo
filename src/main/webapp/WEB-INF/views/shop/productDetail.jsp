<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <fmt:setLocale value="vi_VN" />
            <c:set var="ctx" value="${pageContext.request.contextPath}" />

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
                <link rel="stylesheet" href="${ctx}/assets/css/shop/shop_products.css?v=1.0.1" />
                <link rel="stylesheet" href="${ctx}/assets/css/shop/bookDetail.css?v=1.0.3" />
            </head>

            <body>
                <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                <div class="container-fluid book-detail mt-3">
                    <div class="row justify-content-evenly">

                        <!-- =============== HÌNH ẢNH =============== -->
                        <div class="col-md-5">
                            <div class="book-detail-images">
                                <div class="product-image mb-3">
                                    <c:if test="${not empty product.images}">
                                        <img id="mainImage"
                                            src="http://localhost:8080/assets/images/catalog/products/${product.images[0].url}"
                                            alt="Sách" class="img-fluid border main-image" />
                                    </c:if>
                                </div>

                                <div class="row g-2 mb-3">
                                    <c:forEach var="img" items="${product.images}" varStatus="s">
                                        <div class="col-3">
                                            <img src="http://localhost:8080/assets/images/catalog/products/${img.url}"
                                                class="img-fluid border thumbnail" />
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>

                        <!-- =============== THÔNG TIN SÁCH =============== -->
                        <div class="col-md-7">
                            <!-- Nút quay lại -->
                            <div class="mb-3">
                                <a href="javascript:void(0)" onclick="history.back()"
                                    class="btn btn-outline-secondary btn-sm back-btn">
                                    <i class="bi bi-arrow-left"></i> Quay lại
                                </a>
                            </div>

                            <div class="book-detail-header">
                                <h6 class="author">
                                    Tác giả:
                                    <c:forEach var="author" items="${product.authors}" varStatus="status">
                                        <c:out value="${author.authorName}" />
                                        <c:if test="${!status.last}">, </c:if>
                                    </c:forEach>
                                </h6>

                                <h4 class="title">
                                    <c:out value="${product.title}" />
                                </h4>

                                <div class="mb-3">
                                    <span class="price">
                                        <fmt:formatNumber value="${product.salePrice}" type="currency"
                                            currencySymbol="đ" groupingUsed="true" />
                                    </span>
                                    <c:if test="${product.discountPercent != 0}">
                                        <span class="discount">-
                                            <c:out value="${product.discountPercent}" />%
                                        </span>
                                        <span class="text-muted text-decoration-line-through">
                                            <fmt:formatNumber value="${product.originalPrice}" type="currency"
                                                currencySymbol="đ" groupingUsed="true" />
                                        </span>
                                    </c:if>
                                </div>

                                <!-- =============== TRẠNG THÁI SẢN PHẨM =============== -->
                                <c:if test="${product.status == 'REJECTED'}">
                                    <div class="alert alert-danger d-flex align-items-center mb-3" role="alert">
                                        <i class="bi bi-x-circle-fill me-2 fs-5"></i>
                                        <div>
                                            <strong>Sản phẩm bị từ chối</strong>
                                            <br />
                                            <span class="text-muted">Lý do:
                                                <c:choose>
                                                    <c:when test="${not empty product.rejectReason}">
                                                        <c:out value="${product.rejectReason}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        Không có lý do cụ thể
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </div>
                                </c:if>

                                <c:if test="${product.status == 'PENDING'}">
                                    <div class="alert alert-warning d-flex align-items-center mb-3" role="alert">
                                        <i class="bi bi-clock-history me-2 fs-5"></i>
                                        <div>
                                            <strong>Sản phẩm đang chờ duyệt</strong>
                                            <br />
                                            <span class="text-muted">Sản phẩm của bạn đang được xem xét bởi admin</span>
                                        </div>
                                    </div>
                                </c:if>
                            </div>

                            <!-- =============== THÔNG TIN CHI TIẾT =============== -->
                            <div class="book-information my-3">
                                <div class="book-information-header">Thông tin chi tiết</div>
                                <div class="book-information-body">
                                    <div class="row mb-1 book-information-box">
                                        <div class="col-5 text-muted">Thể loại</div>
                                        <div class="col-7">
                                            <c:forEach var="cat" items="${product.categories}" varStatus="status">
                                                <c:out value="${cat.name}" />
                                                <c:if test="${!status.last}">, </c:if>
                                            </c:forEach>
                                        </div>
                                    </div>

                                    <div class="row mb-1 book-information-box">
                                        <div class="col-5 text-muted">Ngôn ngữ</div>
                                        <div class="col-7">
                                            <c:out value="${product.bookDetail.language.languageName}" />
                                        </div>
                                    </div>

                                    <div class="row mb-1 book-information-box">
                                        <div class="col-5 text-muted">Số trang</div>
                                        <div class="col-7">
                                            <c:out value="${product.bookDetail.pages}" />
                                        </div>
                                    </div>

                                    <div class="row mb-1 book-information-box">
                                        <div class="col-5 text-muted">Kích thước</div>
                                        <div class="col-7">
                                            <c:out value="${product.bookDetail.size}" />
                                        </div>
                                    </div>

                                    <div class="row mb-1 book-information-box">
                                        <div class="col-5 text-muted">Loại bìa</div>
                                        <div class="col-7">
                                            <c:out value="${product.bookDetail.coverType}" />
                                        </div>
                                    </div>

                                    <div class="row mb-1 book-information-box">
                                        <div class="col-5 text-muted">Nhà xuất bản</div>
                                        <div class="col-7">
                                            <c:out value="${product.publisher.name}" />
                                        </div>
                                    </div>

                                    <div class="row mb-1 book-information-box">
                                        <div class="col-5 text-muted">Ngày xuất bản</div>
                                        <div class="col-7">
                                            <fmt:formatDate value="${product.publishedDate}" pattern="dd/MM/yyyy" />
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- =============== MÔ TẢ SẢN PHẨM =============== -->
                            <div class="book-description my-4">
                                <div class="book-description-header">Mô tả sản phẩm</div>
                                <p id="bookDescription" class="book-description-body" style="white-space: pre-line;">
                                    <c:out value="${product.description}" />
                                </p>
                                <a href="#" id="toggleDescription" class="toggle-btn">Xem thêm</a>
                            </div>

                            <!-- =============== THANH TIẾN ĐỘ =============== -->
                            <!-- Chỉ hiển thị khi status không phải PENDING hoặc REJECTED -->
                            <c:if test="${product.status != 'PENDING' && product.status != 'REJECTED'}">
                                <div class="progress-box">
                                    <div class="progress">
                                        <div class="progress-bar"
                                            style="width: ${product.quantity != null && product.quantity > 0 ? (product.soldCount * 100 / (product.quantity + product.soldCount)) : 0}%;">
                                        </div>
                                    </div>
                                    <div class="progress-label">
                                        Đã bán <b>${product.soldCount}</b> / <b>${product.quantity +
                                            product.soldCount}</b>
                                        sản phẩm (
                                        <fmt:formatNumber
                                            value="${product.quantity > 0 ? (product.soldCount * 100 / (product.quantity + product.soldCount)) : 0}"
                                            maxFractionDigits="0" />%)
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
                <script src="${ctx}/assets/js/shop/bookDetail.js?v=1.0.1"></script>
            </body>

            </html>