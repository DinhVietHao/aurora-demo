<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <c:set var="pageTitle" value="Aurora" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/shop_products.css?v=1.0.1" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/voucherManagement.css?1.0.1">
                </head>

                <body class="sb-nav-fixed" data-page="shop-vouchers">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />

                        <div id="layoutSidenav_content">
                            <main>
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
                                <div class="container-fluid px-4">
                                    <!-- Page Header -->
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h1 class="mt-4 promotion-title">Quản lý Khuyến mãi</h1>
                                    </div>

                                    <!-- Statistics Cards -->
                                    <div class="row mt-4 row-cols-5">
                                        <div class="col">
                                            <div class="card stats-card stats-card-blue">
                                                <div class="card-body">
                                                    <div class="stats-content">
                                                        <div class="stats-number">${stats.activeCount}</div>
                                                        <div class="stats-label">Voucher hoạt động</div>
                                                    </div>
                                                    <div class="stats-icon">
                                                        <i class="bi bi-ticket-perforated"></i>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col">
                                            <div class="card stats-card stats-card-orange">
                                                <div class="card-body">
                                                    <div class="stats-content">
                                                        <div class="stats-number">${stats.upcomingCount}</div>
                                                        <div class="stats-label">Voucher sắp diễn ra</div>
                                                    </div>
                                                    <div class="stats-icon">
                                                        <i class="bi bi-clock"></i>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col">
                                            <div class="card stats-card stats-card-red">
                                                <div class="card-body">
                                                    <div class="stats-content">
                                                        <div class="stats-number">${stats.expiredCount}</div>
                                                        <div class="stats-label">Voucher hết hạn</div>
                                                    </div>
                                                    <div class="stats-icon">
                                                        <i class="bi bi-x-circle"></i>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col">
                                            <div class="card stats-card stats-card-purpel">
                                                <div class="card-body">
                                                    <div class="stats-content">
                                                        <div class="stats-number">${stats.outofstockCount}</div>
                                                        <div class="stats-label">Voucher đã hết lượt</div>
                                                    </div>
                                                    <div class="stats-icon"><i class="bi bi-slot"></i></div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col">
                                            <div class="card stats-card stats-card-green">
                                                <div class="card-body">
                                                    <div class="stats-content">
                                                        <div class="stats-number">${stats.totalUsage}</div>
                                                        <div class="stats-label">Lượt sử dụng</div>
                                                    </div>
                                                    <div class="stats-icon">
                                                        <i class="bi bi-graph-up"></i>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Action Bar -->
                                    <div class="row mt-4">
                                        <div class="col-12">
                                            <div class="card">
                                                <div class="card-body">
                                                    <div class="d-flex justify-content-between align-items-center">
                                                        <div class="d-flex align-items-center gap-3">
                                                            <button class="btn btn-success" id="createVoucherBtn">
                                                                <i class="bi bi-plus-circle me-2"></i>Tạo voucher mới
                                                            </button>
                                                            <div class="input-group" style="width: 300px;">
                                                                <span class="input-group-text">
                                                                    <i class="bi bi-search"></i>
                                                                </span>
                                                                <input type="text" class="form-control"
                                                                    placeholder="Tìm kiếm mã voucher..."
                                                                    id="searchVoucher">
                                                            </div>
                                                        </div>
                                                        <div class="d-flex align-items-center gap-2">
                                                            <select class="form-select" id="statusFilter"
                                                                style="width: 150px;">
                                                                <option value="">Tất cả</option>
                                                                <option value="active">Hoạt động</option>
                                                                <option value="pending">Sắp diễn ra</option>
                                                                <option value="out_of_stock">Hết voucher</option>
                                                                <option value="expired">Hết hạn</option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Voucher List -->
                                    <div class="row mt-4">
                                        <div class="col-12">
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-list-ul me-2"></i>Danh sách voucher
                                                        ${stats.totalVouchers}
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="table-responsive">
                                                        <table class="table table-hover voucher-table">
                                                            <thead>
                                                                <tr>
                                                                    <th>Mã Code</th>
                                                                    <th>Loại</th>
                                                                    <th>Giá trị</th>
                                                                    <th>Đơn tối thiểu</th>
                                                                    <th>Thời gian</th>
                                                                    <th>Số dùng</th>
                                                                    <th>Trạng thái</th>
                                                                    <th>Thao tác</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <c:if test="${empty listVoucher}">
                                                                    <div class="alert alert-warning">Chưa có voucher để
                                                                        hiển thị.</div>
                                                                </c:if>
                                                                <c:forEach var="v" items="${listVoucher}">
                                                                    <tr>
                                                                        <!-- Mã code + mô tả -->
                                                                        <td>
                                                                            <div class="voucher-code">
                                                                                <strong>${v.code}</strong>
                                                                                <small
                                                                                    class="text-muted d-block"></small>
                                                                                ${v.description}</small>
                                                                            </div>
                                                                        </td>

                                                                        <!-- Loại giảm giá -->
                                                                        <td>
                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${v.discountType == 'PERCENT'}">
                                                                                    <span class="badge bg-info">%</span>
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${v.discountType == 'AMOUNT'}">
                                                                                    <span
                                                                                        class="badge bg-warning">VNĐ</span>
                                                                                </c:when>
                                                                            </c:choose>
                                                                        </td>

                                                                        <!-- Giá trị giảm -->
                                                                        <td>
                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${v.discountType == 'PERCENT'}">
                                                                                    <span
                                                                                        class="discount-value">${v.value}%</span>
                                                                                    <small class="text-muted d-block">
                                                                                        Tối đa
                                                                                        <fmt:formatNumber
                                                                                            value="${v.maxAmount}"
                                                                                            type="number"
                                                                                            groupingUsed="true" /> VNĐ
                                                                                    </small>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <span class="discount-value">
                                                                                        <fmt:formatNumber
                                                                                            value="${v.value}"
                                                                                            type="number"
                                                                                            groupingUsed="true" /> VNĐ
                                                                                    </span>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </td>

                                                                        <!-- Đơn tối thiểu -->
                                                                        <td>
                                                                            <span class="min-order">
                                                                                <fmt:formatNumber
                                                                                    value="${v.minOrderAmount}"
                                                                                    type="number" groupingUsed="true" />
                                                                                VNĐ
                                                                            </span>
                                                                        </td>

                                                                        <!-- Thời gian -->
                                                                        <td>
                                                                            <div class="date-range">
                                                                                <small>
                                                                                    <fmt:formatDate value="${v.startAt}"
                                                                                        pattern="yyyy-MM-dd HH:mm" />
                                                                                </small>
                                                                                <small>
                                                                                    <fmt:formatDate value="${v.endAt}"
                                                                                        pattern="yyyy-MM-dd HH:mm" />
                                                                                </small>
                                                                            </div>
                                                                        </td>

                                                                        <!-- Số lần sử dụng -->
                                                                        <td>
                                                                            <div class="usage-info">
                                                                                <span
                                                                                    class="used">${v.usageCount}</span>/
                                                                                <span
                                                                                    class="total">${v.usageLimit}</span>
                                                                            </div>
                                                                        </td>

                                                                        <!-- Trạng thái -->
                                                                        <td>
                                                                            <c:choose>
                                                                                <c:when test="${v.status == 'ACTIVE'}">
                                                                                    <span
                                                                                        class="badge bg-success status-badge">Hoạt
                                                                                        động</span>
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${v.status == 'UPCOMING'}">
                                                                                    <span
                                                                                        class="badge bg-warning status-badge">Sắp
                                                                                        diễn ra</span>
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${v.status == 'OUT_OF_STOCK'}">
                                                                                    <span
                                                                                        class="badge bg-secondary status-badge">Hết
                                                                                        Voucher</span>
                                                                                </c:when>
                                                                                <c:when test="${v.status == 'EXPIRED'}">
                                                                                    <span
                                                                                        class="badge bg-danger status-badge">Hết
                                                                                        hạn</span>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <span
                                                                                        class="badge bg-secondary status-badge">Khác</span>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </td>

                                                                        <!-- Thao tác -->
                                                                        <td>
                                                                            <div class="action-buttons">
                                                                                <a href="${ctx}/shop/voucher?action=detail&voucherID=${v.voucherID}"
                                                                                    class="btn btn-sm btn-outline-primary"
                                                                                    title="Xem chi tiết">
                                                                                    <i class="bi bi-eye"></i>
                                                                                </a>
                                                                                <c:if
                                                                                    test="${v.status == 'UPCOMING' || (v.status == 'EXPIRED' && v.usageCount == 0) || (v.status == 'ACTIVE' && v.usageCount == 0)}">
                                                                                    <a href="${ctx}/shop/voucher?action=update&voucherID=${v.voucherID}"
                                                                                        class="btn btn-sm btn-outline-warning"
                                                                                        title="Chỉnh sửa">
                                                                                        <i class="bi bi-pencil"></i>
                                                                                    </a>
                                                                                </c:if>
                                                                                <c:if
                                                                                    test="${v.status == 'UPCOMING' || (v.status == 'EXPIRED' && v.usageCount == 0) || (v.status == 'ACTIVE' && v.usageCount == 0)}">
                                                                                    <button
                                                                                        class="btn btn-sm btn-outline-danger"
                                                                                        onclick="deleteVoucher('${v.code}')"
                                                                                        title="Xóa voucher">
                                                                                        <i class="bi bi-trash"></i>
                                                                                    </button>
                                                                                </c:if>
                                                                            </div>
                                                                        </td>
                                                                    </tr>
                                                                </c:forEach>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </main>
                            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp?v=1.0.1" />
                        </div>
                    </div>

                    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
                    <script src="${ctx}/assets/js/shop/voucherManagement.js"></script>
                </body>

                </html>