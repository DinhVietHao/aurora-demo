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
                    <title>Update Voucher - Aurora Bookstore</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                        rel="stylesheet">
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
                    <link rel="stylesheet"
                        href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/common/globals.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/catalog/home.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/admin/adminPage.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/createVoucher.css">
                </head>

                <body class="sb-nav-fixed">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />

                        <div id="layoutSidenav_content">
                            <main>
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
                                        <h1 class="mt-4 create-title">Update Voucher</h1>
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item"><a href="/home">Trang chủ</a></li>
                                                <li class="breadcrumb-item"><a href="/shop/dashboard">Dashboard</a>
                                                </li>
                                                <li class="breadcrumb-item"><a href="/shop/voucher?action=view">Khuyến
                                                        mãi</a></li>
                                                <li class="breadcrumb-item active" aria-current="page">update</li>
                                            </ol>
                                        </nav>
                                    </div>

                                    <!-- Create Form -->
                                    <div class="row mt-4">
                                        <div class="col-lg-8">
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-plus-circle me-2"></i>Thông tin voucher
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <form id="createVoucherForm" action="/shop/voucher?action=update"
                                                        method="POST">
                                                        <input type="hidden" name="voucherID"
                                                            value="${voucher.voucherID}" />
                                                        <div class="row">
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="voucherCode" class="form-label">Mã
                                                                        voucher <span
                                                                            class="text-danger">*</span></label>
                                                                    <input type="text" class="form-control"
                                                                        id="voucherCode" placeholder="VD: NEWUSER50"
                                                                        required name="voucherCode"
                                                                        value="${voucher.code}" <c:if
                                                                        test="${restrictToDescription or disableAll}">readonly
                                                                    </c:if>>
                                                                    <div class="form-text">Mã voucher phải là duy nhất
                                                                        và không chứa khoảng trắng</div>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="mb-3">
                                                            <label for="voucherDescription" class="form-label">Mô
                                                                tả</label>
                                                            <textarea class="form-control" id="voucherDescription"
                                                                rows="2" name="voucherDescription"
                                                                placeholder="Mô tả chi tiết về voucher..." required
                                                                <c:if
                                                                test="${disableAll}">readonly</c:if>>${voucher.description}</textarea>
                                                        </div>

                                                        <div class="row">
                                                            <div class="col-md-4">
                                                                <div class="mb-3">
                                                                    <label for="discountType" class="form-label">Loại
                                                                        giảm giá <span
                                                                            class="text-danger">*</span></label>
                                                                    <select class="form-select" name="discountType"
                                                                        id="discountType" <c:if
                                                                        test="${restrictToDescription || disableAll}">disabled
                                                                        </c:if> required>
                                                                        <option value="">Chọn loại giảm giá</option>
                                                                        <option value="PERCENT"
                                                                            ${voucher.discountType=='PERCENT'
                                                                            ? 'selected' : '' }>Phần trăm (%)
                                                                        </option>
                                                                        <option value="AMOUNT"
                                                                            ${voucher.discountType=='AMOUNT'
                                                                            ? 'selected' : '' }>Số tiền cố định (VNĐ)
                                                                        </option>
                                                                    </select>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-4">
                                                                <div class="mb-3">
                                                                    <label for="discountValue" class="form-label">Giá
                                                                        trị giảm <span
                                                                            class="text-danger">*</span></label>
                                                                    <input type="number" class="form-control"
                                                                        id="discountValue" value="${voucher.value}"
                                                                        <c:if
                                                                        test="${restrictToDescription or disableAll}">readonly
                                                                    </c:if>
                                                                    name="discountValue" placeholder="0" min="0"
                                                                    required>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-4">
                                                                <div class="mb-3">
                                                                    <label for="maxDiscount" class="form-label">Giảm tối
                                                                        đa (VNĐ)</label>
                                                                    <input type="number" class="form-control"
                                                                        id="maxDiscount" name="maxDiscount"
                                                                        placeholder="0"
                                                                        value="${voucher.maxAmount != null ? voucher.maxAmount : ''}"
                                                                        min="0" <c:if
                                                                        test="${restrictToDescription || disableAll}">readonly
                                                                    </c:if>>
                                                                    <div class="form-text">Chỉ áp dụng cho giảm theo %
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="row">
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="minOrderValue" class="form-label">Đơn
                                                                        hàng tối thiểu (VNĐ)</label>
                                                                    <input type="number" min="0" name="minOrderValue"
                                                                        class="form-control" id="minOrderValue"
                                                                        placeholder="0"
                                                                        value="${voucher.minOrderAmount != null ? voucher.minOrderAmount : ''}"
                                                                        min="0" <c:if
                                                                        test="${restrictToDescription || disableAll}">readonly
                                                                    </c:if> required>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="usageLimit" class="form-label">Giới hạn
                                                                        sử dụng</label>
                                                                    <input type="number" class="form-control"
                                                                        id="usageLimit" name="usageLimit"
                                                                        placeholder="Không giới hạn"
                                                                        value="${voucher.usageLimit != null ? voucher.usageLimit : ''}"
                                                                        min="1" <c:if
                                                                        test="${restrictToDescription || disableAll}">readonly
                                                                    </c:if> required>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="row">
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="startDate" class="form-label">Ngày bắt
                                                                        đầu <span class="text-danger">*</span></label>
                                                                    <input type="datetime-local" class="form-control"
                                                                        id="startDate"
                                                                        value="${fn:substring(voucher.startAt, 0, 16)}"
                                                                        name="startDate" <c:if
                                                                        test="${restrictToDescription || disableAll}">readonly
                                                                    </c:if> required>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="endDate" class="form-label">Ngày kết
                                                                        thúc <span class="text-danger">*</span></label>
                                                                    <input type="datetime-local" class="form-control"
                                                                        id="endDate"
                                                                        value="${fn:substring(voucher.endAt, 0, 16)}"
                                                                        name="endDate" <c:if
                                                                        test="${disableAll}">readonly</c:if> required>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="d-flex gap-3">
                                                            <button type="submit" class="btn btn-success" <c:if
                                                                test="${disableAll}">disabled title="Voucher không thể
                                                                cập nhật"</c:if>>
                                                                <i class="bi bi-check-circle me-2"></i>Cập nhật voucher
                                                            </button>
                                                            <button type="button" class="btn btn-secondary"
                                                                onclick="window.history.back()">
                                                                <i class="bi bi-arrow-left me-2"></i>Quay lại
                                                            </button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-lg-4">
                                            <!-- Preview Card -->
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-eye me-2"></i>Xem trước voucher
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="voucher-preview">
                                                        <div class="voucher-preview-card">
                                                            <div class="voucher-header">
                                                                <div class="voucher-code-preview" id="previewCode">
                                                                    VOUCHER_CODE</div>
                                                                <div class="voucher-type-preview" id="previewType">%
                                                                </div>
                                                            </div>
                                                            <div class="voucher-body">
                                                                <div class="voucher-name-preview" id="previewName">Tên
                                                                    voucher</div>
                                                                <div class="voucher-discount-preview"
                                                                    id="previewDiscount">0%</div>
                                                                <div class="voucher-condition-preview"
                                                                    id="previewCondition">Đơn tối thiểu: 0 VNĐ</div>
                                                            </div>
                                                            <div class="voucher-footer">
                                                                <div class="voucher-date-preview" id="previewDate">Chưa
                                                                    có thời hạn</div>
                                                                <div class="voucher-usage-preview" id="previewUsage">
                                                                    Không giới hạn</div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Tips Card -->
                                            <div class="card mt-4">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-lightbulb me-2"></i>Gợi ý
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="tips-list">
                                                        <div class="tip-item">
                                                            <i class="bi bi-check-circle text-success me-2"></i>
                                                            <span>Mã voucher nên ngắn gọn và dễ nhớ</span>
                                                        </div>
                                                        <div class="tip-item">
                                                            <i class="bi bi-check-circle text-success me-2"></i>
                                                            <span>Đặt giới hạn sử dụng để tránh lạm dụng</span>
                                                        </div>
                                                        <div class="tip-item">
                                                            <i class="bi bi-check-circle text-success me-2"></i>
                                                            <span>Kiểm tra kỹ thời gian hiệu lực</span>
                                                        </div>
                                                        <div class="tip-item">
                                                            <i class="bi bi-check-circle text-success me-2"></i>
                                                            <span>Mô tả rõ ràng điều kiện áp dụng</span>
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
                    <script src="${ctx}/assets/js/shop/updateVoucher.js"></script>
                    <script>
                        window.originalStartAt = "${fn:substring(voucher.startAt, 0, 16)}"; 
                    </script>
                </body>

                </html>