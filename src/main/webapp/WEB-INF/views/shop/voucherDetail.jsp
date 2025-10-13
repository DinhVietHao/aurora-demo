<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <c:set var="pageTitle" value="Aurora" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Chi tiết Voucher - Aurora Bookstore</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                        rel="stylesheet">
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
                    <link rel="stylesheet"
                        href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/common/globals.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/catalog/home.css?v=1.0.1" />
                    <link rel="stylesheet" href="${ctx}/assets/css/admin/adminPage.css?v=1.0.1" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/voucherDetail.css">
                </head>

                <body class="sb-nav-fixed">
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
                                        <h1 class="mt-4 details-title">Chi tiết Voucher</h1>
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item"><a href="/home">Trang chủ</a></li>
                                                <li class="breadcrumb-item"><a href="/home/dashboard">Dashboard</a>
                                                </li>
                                                <li class="breadcrumb-item"><a href="/home/voucher">Khuyến
                                                        mãi</a></li>
                                                <li class="breadcrumb-item active" aria-current="page">Chi tiết</li>
                                            </ol>
                                        </nav>
                                    </div>

                                    <!-- Voucher Header -->
                                    <div class="row mt-4">
                                        <div class="col-12">
                                            <div class="card voucher-header-card">
                                                <div class="card-body">
                                                    <div class="row align-items-center">
                                                        <div class="col-md-8">
                                                            <div class="voucher-info">
                                                                <div class="voucher-code-display">
                                                                    <span class="code-text"
                                                                        id="voucherCode">${voucher.code}</span>
                                                                    <button class="btn btn-sm btn-outline-primary ms-2"
                                                                        onclick="copyVoucherCode()" title="Sao chép mã">
                                                                        <i class="bi bi-clipboard"></i>
                                                                    </button>
                                                                </div>
                                                                <p class="voucher-description" id="voucherDescription">
                                                                    ${voucher.description}</p>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-4 text-md-end">
                                                            <div class="voucher-actions">
                                                                <c:choose>
                                                                    <c:when test="${voucher.status == 'ACTIVE'}">
                                                                        <span class="badge bg-success status-badge">Hoạt
                                                                            động</span>
                                                                    </c:when>
                                                                    <c:when test="${voucher.status == 'UPCOMING'}">
                                                                        <span class="badge bg-warning status-badge">Sắp
                                                                            diễn ra</span>
                                                                    </c:when>
                                                                    <c:when test="${voucher.status == 'EXPIRED'}">
                                                                        <span class="badge bg-danger status-badge">Hết
                                                                            hạn</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span
                                                                            class="badge bg-secondary status-badge">Khác</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                <div class="action-buttons">
                                                                    <button class="btn btn-warning"
                                                                        onclick="editVoucher()">
                                                                        <i class="bi bi-pencil me-2"></i>Chỉnh sửa
                                                                    </button>
                                                                    <button class="btn btn-danger"
                                                                        onclick="deleteVoucher()">
                                                                        <i class="bi bi-trash me-2"></i>Xóa
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Voucher Details -->
                                    <div class="row mt-4">
                                        <div class="col-lg-8">
                                            <!-- Basic Information -->
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-info-circle me-2"></i>Thông tin cơ bản
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Loại giảm giá:</label>
                                                                <c:choose>
                                                                    <c:when test="${voucher.discountType == 'PERCENT'}">
                                                                        <span class="badge bg-info">Phần Trăm (%)</span>
                                                                    </c:when>
                                                                    <c:when test="${voucher.discountType == 'AMOUNT'}">
                                                                        <span class="badge bg-warning">Số tiền
                                                                            (VNĐ)</span>
                                                                    </c:when>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Giá trị giảm:</label>
                                                                <span class="discount-value"
                                                                    id="discountValue">${voucher.value}${voucher.discountType
                                                                    == 'PERCENT' ? '%' : ' VNĐ'}</span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <c:if test="${voucher.discountType == 'PERCENT'}">
                                                                <div class="info-item">
                                                                    <label>Giảm tối đa:</label>
                                                                    <span id="maxDiscount">
                                                                        <fmt:formatNumber value="${voucher.maxAmount}"
                                                                            type="number" groupingUsed="true" /> VNĐ
                                                                    </span>
                                                                </div>
                                                            </c:if>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Đơn hàng tối thiểu:</label>
                                                                <span
                                                                    id="minOrderValue">${voucher.minOrderAmount}</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Time & Usage -->
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-clock me-2"></i>Thời gian & Sử dụng
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Ngày bắt đầu:</label>
                                                                <span id="startDate">${voucher.startAt}</span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Ngày kết thúc:</label>
                                                                <span id="endDate">${voucher.endAt}</span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Giới hạn sử dụng:</label>
                                                                <span id="usageLimit">${voucher.usageLimit} lượt</span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Đã sử dụng:</label>
                                                                <span id="usedCount"
                                                                    class="text-success">${voucher.usageCount}
                                                                    lượt</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="mt-3">
                                                        <label class="form-label">Tiến độ sử dụng:</label>
                                                        <div class="progress">
                                                            <c:set var="progressPercent" value="0" />
                                                            <c:if test="${voucher.usageLimit > 0}">
                                                                <c:set var="progressPercent"
                                                                    value="${(voucher.usageCount * 100) / voucher.usageLimit}" />
                                                            </c:if>
                                                            <div class="progress-bar bg-success" role="progressbar"
                                                                style="width: ${progressPercent}%" id=" usageProgress">
                                                                ${progressPercent}%
                                                            </div>
                                                        </div>
                                                        <small class="text-muted">
                                                            ${voucher.usageCount} / ${voucher.usageLimit} lượt sử dụng
                                                        </small>
                                                    </div>

                                                </div>
                                            </div>

                                            <!-- Usage History -->
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-list-ul me-2"></i>Lịch sử sử dụng gần đây
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="table-responsive">
                                                        <table class="table table-hover">
                                                            <thead>
                                                                <tr>
                                                                    <th>Đơn hàng</th>
                                                                    <th>Khách hàng</th>
                                                                    <th>Giá trị đơn</th>
                                                                    <th>Giảm giá</th>
                                                                    <th>Thời gian</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <tr>
                                                                    <td><a
                                                                            href="orderDetails.html?id=ORD001">#ORD001</a>
                                                                    </td>
                                                                    <td>Nguyễn Văn A</td>
                                                                    <td>450.000 VNĐ</td>
                                                                    <td class="text-success">-200.000 VNĐ</td>
                                                                    <td>15/01/2024 14:30</td>
                                                                </tr>
                                                                <tr>
                                                                    <td><a
                                                                            href="orderDetails.html?id=ORD002">#ORD002</a>
                                                                    </td>
                                                                    <td>Trần Thị B</td>
                                                                    <td>320.000 VNĐ</td>
                                                                    <td class="text-success">-160.000 VNĐ</td>
                                                                    <td>14/01/2024 09:15</td>
                                                                </tr>
                                                                <tr>
                                                                    <td><a
                                                                            href="orderDetails.html?id=ORD003">#ORD003</a>
                                                                    </td>
                                                                    <td>Lê Văn C</td>
                                                                    <td>280.000 VNĐ</td>
                                                                    <td class="text-success">-140.000 VNĐ</td>
                                                                    <td>13/01/2024 16:45</td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                    <div class="text-center">
                                                        <button class="btn btn-outline-primary btn-sm">
                                                            <i class="bi bi-eye me-2"></i>Xem tất cả
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-lg-4">
                                            <!-- Voucher Preview -->
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-eye me-2"></i>Xem trước voucher
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="voucher-preview">
                                                        <div class="voucher-preview-card">
                                                            <div class="voucher-header">
                                                                <div class="voucher-code-preview">${voucher.code}</div>
                                                                <div class="voucher-type-preview">
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${voucher.discountType == 'PERCENT'}">
                                                                            %</c:when>
                                                                        <c:when
                                                                            test="${voucher.discountType == 'AMOUNT'}">
                                                                            VNĐ</c:when>
                                                                    </c:choose>
                                                                </div>
                                                            </div>
                                                            <div class="voucher-body">
                                                                <div class="voucher-name-preview">${voucher.description}
                                                                </div>
                                                                <div class="voucher-discount-preview">
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${voucher.discountType == 'PERCENT'}">
                                                                            ${voucher.value}%
                                                                        </c:when>
                                                                        <c:when
                                                                            test="${voucher.discountType == 'AMOUNT'}">
                                                                            <fmt:formatNumber value="${voucher.value}"
                                                                                type="number" groupingUsed="true" /> VNĐ
                                                                        </c:when>
                                                                    </c:choose>
                                                                </div>
                                                                <div class="voucher-condition-preview">
                                                                    Đơn tối thiểu:
                                                                    <fmt:formatNumber value="${voucher.minOrderAmount}"
                                                                        type="number" groupingUsed="true" /> VNĐ
                                                                </div>
                                                            </div>
                                                            <div class="voucher-footer">
                                                                <div class="voucher-date-preview">
                                                                    <fmt:formatDate value="${voucher.startAt}"
                                                                        pattern="dd/MM/yyyy" /> -
                                                                    <fmt:formatDate value="${voucher.endAt}"
                                                                        pattern="dd/MM/yyyy" />
                                                                </div>
                                                                <div class="voucher-usage-preview">
                                                                    <c:set var="remainingUsage"
                                                                        value="${voucher.usageLimit - voucher.usageCount}" />
                                                                    Còn lại: ${remainingUsage} lượt
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Statistics -->
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-graph-up me-2"></i>Thống kê
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="stats-list">
                                                        <div class="stat-item">
                                                            <div class="stat-label">Tổng tiết kiệm:</div>
                                                            <div class="stat-value text-success">12.500.000 VNĐ</div>
                                                        </div>
                                                        <div class="stat-item">
                                                            <div class="stat-label">Trung bình mỗi đơn:</div>
                                                            <div class="stat-value">52.083 VNĐ</div>
                                                        </div>
                                                        <div class="stat-item">
                                                            <div class="stat-label">Tỷ lệ sử dụng:</div>
                                                            <div class="stat-value">24%</div>
                                                        </div>
                                                        <div class="stat-item">
                                                            <div class="stat-label">Khách hàng duy nhất:</div>
                                                            <div class="stat-value">187 người</div>
                                                        </div>
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

                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                    <script src="${ctx}/assets/js/shop/voucherDetail.js?v=1.0.1"></script>
                </body>

                </html>