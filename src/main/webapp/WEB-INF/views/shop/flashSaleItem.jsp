<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

                <c:set var="pageTitle" value="Quản lý Flash Sale" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/flashSale.css" />
                </head>

                <body class="sb-nav-fixed" data-ctx="${ctx}" data-page="shop-flashsale">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />

                        <div id="layoutSidenav_content">
                            <main>
                                <!-- Thông báo -->
                                <c:if test="${not empty successMessage}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        ${fn:escapeXml(successMessage)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>

                                <c:if test="${not empty errorMessage}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        ${fn:escapeXml(errorMessage)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>

                                <!-- Tiêu đề -->
                                <div class="container-fluid px-4">
                                    <div class="d-flex justify-content-between align-items-center mt-4 mb-3">
                                        <h1 class="fw-bold" style="color: #154C3D;">Sản phẩm tham gia Flash Sale</h1>
                                        <a href="/shop/flashSale" class="btn btn-outline-secondary">
                                            <i class="bi bi-arrow-left me-1"></i> Quay lại
                                        </a>
                                    </div>

                                    <!-- Cảnh báo -->
                                    <div class="alert alert-info fade show shadow-sm" role="alert">
                                        <i class="bi bi-lightning-charge-fill text-warning me-2"></i>
                                        <strong>Lưu ý:</strong> Chỉ hiển thị các sản phẩm thuộc Flash Sale hiện tại.
                                    </div>

                                    <!-- Danh sách sản phẩm -->
                                    <div class="card mt-3 shadow-sm">
                                        <div class="card-body">
                                            <c:choose>
                                                <c:when test="${empty items}">
                                                    <div class="text-center text-muted py-5">
                                                        <i class="bi bi-inbox fs-1 mb-3"></i>
                                                        <p>Chưa có sản phẩm nào trong Flash Sale này.</p>
                                                    </div>
                                                </c:when>

                                                <c:otherwise>
                                                    <!-- Tiêu đề cột -->
                                                    <div
                                                        class="row fw-bold border-bottom pb-2 mb-3 text-center text-md-start">
                                                        <div class="col-md-5">Sản phẩm</div>
                                                        <div class="col-md-2">Giá Flash Sale</div>
                                                        <div class="col-md-1">Số lượng</div>
                                                        <div class="col-md-1">Giới hạn/người</div>
                                                        <div class="col-md-2">Trạng thái</div>
                                                        <div class="col-md-1">Thao tác</div>
                                                    </div>

                                                    <!-- Dữ liệu -->
                                                    <c:forEach var="item" items="${items}">
                                                        <div
                                                            class="row align-items-center border-bottom py-3 flashsale-item-row">
                                                            <!-- Sản phẩm -->
                                                            <div class="col-md-5 d-flex align-items-center">
                                                                <img src="${ctx}/assets/images/catalog/products/${item.imageUrl}"
                                                                    alt="${item.title}" class="rounded border me-3"
                                                                    style="width: 80px; height: 80px; object-fit: cover;">
                                                                <div>
                                                                    <h6 class="fw-semibold mb-1 text-truncate"
                                                                        style="max-width: 350px;">${item.title}</h6>
                                                                    <small class="text-muted">ID:
                                                                        ${item.productID}</small>
                                                                </div>
                                                            </div>

                                                            <!-- Giá Flash Sale -->
                                                            <div class="col-md-2 text-danger fw-semibold">
                                                                <fmt:formatNumber value="${item.flashPrice}"
                                                                    type="number" pattern="#,##0" /> VND
                                                            </div>

                                                            <!-- Số lượng -->
                                                            <div class="col-md-1">${item.fsStock}</div>

                                                            <!-- Giới hạn/người -->
                                                            <div class="col-md-1">
                                                                <c:choose>
                                                                    <c:when
                                                                        test="${item.perUserLimit != null && item.perUserLimit > 0}">
                                                                        ${item.perUserLimit}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <small class="text-muted">∞</small>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>

                                                            <!-- Trạng thái -->
                                                            <div class="col-md-2">
                                                                <c:choose>
                                                                    <c:when test="${item.approvalStatus == 'PENDING'}">
                                                                        <span
                                                                            class="badge bg-warning text-dark px-3 py-2">Chờ
                                                                            duyệt</span>
                                                                    </c:when>
                                                                    <c:when test="${item.approvalStatus == 'APPROVED'}">
                                                                        <span class="badge bg-success px-3 py-2">Đã
                                                                            duyệt</span>
                                                                    </c:when>
                                                                    <c:when test="${item.approvalStatus == 'REJECTED'}">
                                                                        <span class="badge bg-danger px-3 py-2">Từ
                                                                            chối</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span
                                                                            class="badge bg-secondary px-3 py-2">${item.approvalStatus}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>

                                                            <!-- Thao tác -->
                                                            <div class="col-md-1">
                                                                <a href="${ctx}/shop/flashSale?action=getFlashsaleItemDetail&itemId=${item.flashSaleItemID}"
                                                                    class="btn btn-outline-primary btn-sm rounded-pill px-3">
                                                                    Xem
                                                                </a>
                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </main>
                        </div>
                    </div>

                    <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
                </body>

                </html>