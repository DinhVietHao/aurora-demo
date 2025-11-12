<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <c:set var="pageTitle" value="Lịch sử doanh thu - Aurora" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/revenueHistory.css?v=1.0.2" />
                </head>

                <body class="sb-nav-fixed">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />
                        <div id="layoutSidenav_content">
                            <main>
                                <div class="container-fluid px-4">
                                    <!-- Page Header -->
                                    <div class="d-flex justify-content-between align-items-center mt-4 mb-4">
                                        <div>
                                            <h1 class="dashboard-title mb-2">
                                                <i class="bi bi-cash-stack me-2"></i>Lịch sử doanh thu
                                            </h1>
                                            <nav aria-label="breadcrumb">
                                                <ol class="breadcrumb">
                                                    <li class="breadcrumb-item">
                                                        <a href="${ctx}/shop?action=dashboard">Dashboard</a>
                                                    </li>
                                                    <li class="breadcrumb-item active">Lịch sử doanh thu</li>
                                                </ol>
                                            </nav>
                                        </div>
                                    </div>

                                    <!-- Summary Card -->
                                    <div class="row mb-4">
                                        <div class="col-12">
                                            <div class="card summary-card">
                                                <div class="card-body">
                                                    <h5 class="mb-3">
                                                        <i class="bi bi-calendar-range me-2"></i>
                                                        Khoảng thời gian:
                                                        <fmt:formatDate value="${startDate}" pattern="dd/MM/yyyy" /> -
                                                        <fmt:formatDate value="${endDate}" pattern="dd/MM/yyyy" />
                                                    </h5>
                                                    <div class="row">
                                                        <div class="col-md-4">
                                                            <div class="summary-item">
                                                                <span class="summary-label">Tổng đơn hàng:</span>
                                                                <span class="summary-value text-primary">
                                                                    ${fn:length(revenueDetails)} đơn
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-4">
                                                            <div class="summary-item">
                                                                <span class="summary-label">Tổng giá trị đơn:</span>
                                                                <span class="summary-value text-info">
                                                                    <fmt:formatNumber
                                                                        value="${totalRevenue + totalPlatformFee}"
                                                                        type="currency" currencySymbol="₫"
                                                                        maxFractionDigits="0" groupingUsed="true" />
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-4">
                                                            <div class="summary-item">
                                                                <span class="summary-label">Doanh thu thực nhận:</span>
                                                                <span class="summary-value text-success fw-bold fs-5">
                                                                    <fmt:formatNumber value="${totalRevenue}"
                                                                        type="currency" currencySymbol="₫"
                                                                        maxFractionDigits="0" groupingUsed="true" />
                                                                </span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Revenue Details Table -->
                                    <div class="card">
                                        <div class="card-header">
                                            <i class="bi bi-table me-1"></i>
                                            Chi tiết đơn hàng
                                        </div>
                                        <div class="card-body">
                                            <c:choose>
                                                <c:when test="${not empty revenueDetails}">
                                                    <div class="table-responsive">
                                                        <table class="table table-hover align-middle revenue-table">
                                                            <thead class="table-light">
                                                                <tr>
                                                                    <th>Mã đơn</th>
                                                                    <th>Khách hàng</th>
                                                                    <th>Ngày hoàn thành</th>
                                                                    <th class="text-end">Tổng đơn</th>
                                                                    <th class="text-end">Giảm giá</th>
                                                                    <th class="text-end">VAT</th>
                                                                    <th class="text-end">Phí nền tảng</th>
                                                                    <th class="text-end">Thực nhận</th>
                                                                    <th class="text-center">Chi tiết</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <c:forEach var="detail" items="${revenueDetails}"
                                                                    varStatus="status">
                                                                    <tr>
                                                                        <td>
                                                                            <span
                                                                                class="badge bg-secondary">${detail.orderCode}</span>
                                                                        </td>
                                                                        <td>
                                                                            <div class="customer-info">
                                                                                <i class="bi bi-person-circle me-1"></i>
                                                                                ${detail.customerName}
                                                                            </div>
                                                                            <small class="text-muted">
                                                                                ${detail.itemCount} sản phẩm
                                                                            </small>
                                                                        </td>
                                                                        <td>
                                                                            <fmt:formatDate
                                                                                value="${detail.completedAt}"
                                                                                pattern="dd/MM/yyyy HH:mm" />
                                                                        </td>
                                                                        <td class="text-end">
                                                                            <fmt:formatNumber value="${detail.subtotal}"
                                                                                type="currency" currencySymbol="₫"
                                                                                maxFractionDigits="0"
                                                                                groupingUsed="true" />
                                                                        </td>
                                                                        <td class="text-end text-danger">
                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${detail.shopDiscount > 0}">
                                                                                    -
                                                                                    <fmt:formatNumber
                                                                                        value="${detail.shopDiscount}"
                                                                                        type="currency"
                                                                                        currencySymbol="₫"
                                                                                        maxFractionDigits="0"
                                                                                        groupingUsed="true" />
                                                                                </c:when>
                                                                                <c:otherwise>0₫</c:otherwise>
                                                                            </c:choose>
                                                                        </td>
                                                                        <td class="text-end text-warning">
                                                                            <c:choose>
                                                                                <c:when test="${detail.totalVAT > 0}">
                                                                                    -
                                                                                    <fmt:formatNumber
                                                                                        value="${detail.totalVAT}"
                                                                                        type="currency"
                                                                                        currencySymbol="₫"
                                                                                        maxFractionDigits="0"
                                                                                        groupingUsed="true" />
                                                                                </c:when>
                                                                                <c:otherwise>0₫</c:otherwise>
                                                                            </c:choose>
                                                                        </td>
                                                                        <td class="text-end text-info">
                                                                            -
                                                                            <fmt:formatNumber
                                                                                value="${detail.platformFee}"
                                                                                type="currency" currencySymbol="₫"
                                                                                maxFractionDigits="0"
                                                                                groupingUsed="true" />
                                                                        </td>
                                                                        <td class="text-end fw-bold text-success">
                                                                            <fmt:formatNumber
                                                                                value="${detail.shopRevenue}"
                                                                                type="currency" currencySymbol="₫"
                                                                                maxFractionDigits="0"
                                                                                groupingUsed="true" />
                                                                        </td>
                                                                        <td class="text-center">
                                                                            <button
                                                                                class="btn btn-sm btn-outline-primary"
                                                                                data-bs-toggle="modal"
                                                                                data-bs-target="#detailModal_${status.index}">
                                                                                <i class="bi bi-eye"></i>
                                                                            </button>
                                                                        </td>
                                                                    </tr>
                                                                </c:forEach>
                                                            </tbody>
                                                        </table>
                                                    </div>

                                                    <!-- Detail Modals (outside of table) -->
                                                    <c:forEach var="detail" items="${revenueDetails}"
                                                        varStatus="status">
                                                        <div class="modal fade" id="detailModal_${status.index}"
                                                            tabindex="-1"
                                                            aria-labelledby="detailModalLabel_${status.index}"
                                                            aria-hidden="true">
                                                            <div class="modal-dialog modal-lg">
                                                                <div class="modal-content">
                                                                    <div class="modal-header">
                                                                        <h5 class="modal-title"
                                                                            id="detailModalLabel_${status.index}">
                                                                            Chi tiết đơn hàng ${detail.orderCode}
                                                                        </h5>
                                                                        <button type="button" class="btn-close"
                                                                            data-bs-dismiss="modal"
                                                                            aria-label="Close"></button>
                                                                    </div>
                                                                    <div class="modal-body">
                                                                        <table class="table table-sm">
                                                                            <tbody>
                                                                                <tr>
                                                                                    <td class="fw-bold">Tổng tiền hàng:
                                                                                    </td>
                                                                                    <td class="text-end">
                                                                                        <fmt:formatNumber
                                                                                            value="${detail.subtotal}"
                                                                                            type="currency"
                                                                                            currencySymbol="₫"
                                                                                            maxFractionDigits="0" />
                                                                                    </td>
                                                                                </tr>
                                                                                <tr>
                                                                                    <td class="fw-bold">Giảm giá shop:
                                                                                    </td>
                                                                                    <td class="text-end text-danger">
                                                                                        -
                                                                                        <fmt:formatNumber
                                                                                            value="${detail.shopDiscount}"
                                                                                            type="currency"
                                                                                            currencySymbol="₫"
                                                                                            maxFractionDigits="0" />
                                                                                    </td>
                                                                                </tr>
                                                                                <tr>
                                                                                    <td class="fw-bold">Phí vận chuyển:
                                                                                    </td>
                                                                                    <td class="text-end">
                                                                                        <fmt:formatNumber
                                                                                            value="${detail.shippingFee}"
                                                                                            type="currency"
                                                                                            currencySymbol="₫"
                                                                                            maxFractionDigits="0" />
                                                                                    </td>
                                                                                </tr>
                                                                                <tr class="table-light">
                                                                                    <td class="fw-bold">Khách thanh
                                                                                        toán:</td>
                                                                                    <td class="text-end fw-bold">
                                                                                        <fmt:formatNumber
                                                                                            value="${detail.finalAmount}"
                                                                                            type="currency"
                                                                                            currencySymbol="₫"
                                                                                            maxFractionDigits="0" />
                                                                                    </td>
                                                                                </tr>
                                                                                <tr>
                                                                                    <td class="fw-bold">Thuế VAT:</td>
                                                                                    <td class="text-end text-warning">
                                                                                        -
                                                                                        <fmt:formatNumber
                                                                                            value="${detail.totalVAT}"
                                                                                            type="currency"
                                                                                            currencySymbol="₫"
                                                                                            maxFractionDigits="0" />
                                                                                    </td>
                                                                                </tr>
                                                                                <tr>
                                                                                    <td class="fw-bold">Phí nền tảng
                                                                                        (
                                                                                        <fmt:formatNumber
                                                                                            value="${detail.platformFee / detail.subtotal * 100}"
                                                                                            maxFractionDigits="2" />%):
                                                                                    </td>
                                                                                    <td class="text-end text-info">
                                                                                        -
                                                                                        <fmt:formatNumber
                                                                                            value="${detail.platformFee}"
                                                                                            type="currency"
                                                                                            currencySymbol="₫"
                                                                                            maxFractionDigits="0" />
                                                                                    </td>
                                                                                </tr>
                                                                                <tr class="table-success">
                                                                                    <td class="fw-bold fs-5">Shop nhận
                                                                                        được:</td>
                                                                                    <td
                                                                                        class="text-end fw-bold text-success fs-5">
                                                                                        <fmt:formatNumber
                                                                                            value="${detail.shopRevenue}"
                                                                                            type="currency"
                                                                                            currencySymbol="₫"
                                                                                            maxFractionDigits="0" />
                                                                                    </td>
                                                                                </tr>
                                                                            </tbody>
                                                                        </table>
                                                                        <div class="alert alert-info mt-3">
                                                                            <i class="bi bi-info-circle me-2"></i>
                                                                            <strong>Công thức tính:</strong><br>
                                                                            Doanh thu shop = Tổng tiền hàng - Giảm giá
                                                                            shop - VAT - Phí nền tảng
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:forEach>

                                                </c:when>
                                                <c:otherwise>
                                                    <div class="text-center py-5">
                                                        <i class="bi bi-inbox display-1 text-muted"></i>
                                                        <p class="text-muted mt-3">Không có đơn hàng nào trong khoảng
                                                            thời gian này</p>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </main>
                            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                        </div>
                    </div>

                    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
                </body>

                </html>