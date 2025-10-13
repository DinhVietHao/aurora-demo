<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết Voucher - Aurora Bookstore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <jsp:include page="/WEB-INF/views/layouts/_head_admin.jsp" />
    <style>
        .details-title {
            font-weight: bold;
            color: #333;
            margin-bottom: 1.5rem;
        }
        .voucher-header-card {
            background-color: #f8f9fa;
            border-left: 5px solid #007bff;
            margin-bottom: 20px;
        }
        .voucher-code-display {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
        }
        .code-text {
            font-size: 1.8rem;
            font-weight: bold;
            color: #007bff;
            letter-spacing: 1px;
        }
        .voucher-name {
            margin-bottom: 10px;
            font-weight: 600;
        }
        .voucher-description {
            color: #6c757d;
        }
        .voucher-actions {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
        }
        .status-badge {
            padding: 8px 12px;
            font-size: 0.9rem;
        }
        .action-buttons {
            display: flex;
            gap: 10px;
            margin-top: 10px;
        }
        .info-item {
            margin-bottom: 15px;
        }
        .info-item label {
            display: block;
            font-weight: 500;
            color: #6c757d;
            margin-bottom: 5px;
        }
        .discount-value {
            font-weight: 600;
            color: #28a745;
            font-size: 1.1rem;
        }
        .voucher-preview {
            margin-top: 1rem;
        }
        .voucher-preview-card {
            border: 2px solid #ddd;
            border-radius: 10px;
            padding: 20px;
            background-color: #f8f9fa;
            position: relative;
        }
        .voucher-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        .voucher-code-preview {
            font-size: 1.3rem;
            font-weight: bold;
            color: #007bff;
        }
        .voucher-type-preview {
            background-color: #17a2b8;
            color: white;
            width: 30px;
            height: 30px;
            display: flex;
            justify-content: center;
            align-items: center;
            border-radius: 50%;
            font-weight: bold;
        }
        .voucher-body {
            margin-bottom: 15px;
        }
        .voucher-name-preview {
            font-weight: bold;
            margin-bottom: 10px;
        }
        .voucher-discount-preview {
            font-size: 1.5rem;
            font-weight: bold;
            color: #28a745;
            margin-bottom: 5px;
        }
        .voucher-condition-preview {
            font-size: 0.9rem;
            color: #6c757d;
        }
        .voucher-footer {
            display: flex;
            justify-content: space-between;
            font-size: 0.85rem;
            color: #6c757d;
            border-top: 1px dashed #ddd;
            padding-top: 10px;
        }
        .stats-list {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }
        .stat-item {
            display: flex;
            justify-content: space-between;
            padding-bottom: 10px;
            border-bottom: 1px solid #eee;
        }
        .stat-label {
            font-weight: 500;
            color: #6c757d;
        }
        .stat-value {
            font-weight: 600;
        }
    </style>
</head>
<body class="sb-nav-fixed">
    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />
    
    <div id="layoutSidenav">
        <jsp:include page="/WEB-INF/views/layouts/_sidebar_admin.jsp" />
        
        <div id="layoutSidenav_content">
            <main>
                <div class="container-fluid px-4">
                    <!-- Page Header -->
                    <div class="d-flex justify-content-between align-items-center">
                        <h1 class="mt-4 details-title">Chi tiết Voucher</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/vouchers">Voucher</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Chi tiết</li>
                            </ol>
                        </nav>
                    </div>
                    
                    <!-- Success message -->
                    <c:if test="${param.success != null}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <c:choose>
                                <c:when test="${param.success == 'voucher_updated'}">Voucher đã được cập nhật thành công!</c:when>
                                <c:otherwise>Thao tác thành công!</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
                    <!-- Voucher Header -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card voucher-header-card">
                                <div class="card-body">
                                    <div class="row align-items-center">
                                        <div class="col-md-8">
                                            <div class="voucher-info">
                                                <div class="voucher-code-display">
                                                    <span class="code-text" id="voucherCode">${voucher.code}</span>
                                                    <button class="btn btn-sm btn-outline-primary ms-2" onclick="copyVoucherCode()" title="Sao chép mã">
                                                        <i class="bi bi-clipboard"></i>
                                                    </button>
                                                </div>
                                                <h4 class="voucher-name" id="voucherName">${voucher.description}</h4>
                                                <p class="voucher-description" id="voucherDescription">
                                                    <c:choose>
                                                        <c:when test="${voucher.discountType == 'percentage'}">
                                                            Giảm ${voucher.value}% cho đơn hàng từ ${voucher.formattedMinOrderAmount} 
                                                            <c:if test="${voucher.maxAmount != null}">
                                                                (tối đa ${voucher.formattedMaxAmount})
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>
                                                            Giảm ${voucher.formattedValue} cho đơn hàng từ ${voucher.formattedMinOrderAmount}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>
                                            </div>
                                        </div>
                                        <div class="col-md-4 text-md-end">
                                            <div class="voucher-actions">
                                                <span class="badge ${voucher.displayStatus == 'Hoạt động' ? 'bg-success' : voucher.displayStatus == 'Hết hạn' ? 'bg-danger' : voucher.displayStatus == 'Chờ' ? 'bg-warning' : 'bg-secondary'} status-badge mb-2" id="voucherStatus">${voucher.displayStatus}</span>
                                                <div class="action-buttons">
                                                    <a href="${pageContext.request.contextPath}/admin/vouchers/edit?id=${voucher.voucherId}" class="btn btn-warning">
                                                        <i class="bi bi-pencil me-2"></i>Chỉnh sửa
                                                    </a>
                                                    <button class="btn btn-danger" onclick="confirmDelete(${voucher.voucherId}, '${voucher.code}')">
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
                                                <span class="badge ${voucher.discountType == 'percentage' ? 'bg-info' : 'bg-warning'}" id="discountType">
                                                    ${voucher.discountType == 'percentage' ? 'Phần trăm (%)' : 'Số tiền cố định (VNĐ)'}
                                                </span>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="info-item">
                                                <label>Giá trị giảm:</label>
                                                <span class="discount-value" id="discountValue">${voucher.formattedValue}</span>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="info-item">
                                                <label>Giảm tối đa:</label>
                                                <span id="maxDiscount">${voucher.formattedMaxAmount}</span>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="info-item">
                                                <label>Đơn hàng tối thiểu:</label>
                                                <span id="minOrderValue">${voucher.formattedMinOrderAmount}</span>
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
                                                <span id="startDate"><fmt:formatDate value="${voucher.startAtDate}" pattern="dd/MM/yyyy HH:mm" /></span>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="info-item">
                                                <label>Ngày kết thúc:</label>
                                                <span id="endDate"><fmt:formatDate value="${voucher.endAtDate}" pattern="dd/MM/yyyy HH:mm" /></span>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="info-item">
                                                <label>Giới hạn sử dụng:</label>
                                                <span id="usageLimit">${voucher.usageLimit != null ? voucher.usageLimit : 'Không giới hạn'}</span>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="info-item">
                                                <label>Đã sử dụng:</label>
                                                <span id="usedCount" class="text-success">${voucher.usageCount} lượt</span>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <c:if test="${voucher.usageLimit != null && voucher.usageLimit > 0}">
                                    <!-- Usage Progress -->
                                    <div class="mt-3">
                                        <label class="form-label">Tiến độ sử dụng:</label>
                                        <div class="progress">
                                                <div class="progress-bar bg-success" role="progressbar" 
                                                     style="width: ${voucher.usageCount * 100 / voucher.usageLimit}%" 
                                                     id="usageProgress">
                                                    ${Math.round(voucher.usageCount * 100 / voucher.usageLimit)}%
                                                </div>
                                            </div>
                                            <small class="text-muted">${voucher.usageCount} / ${voucher.usageLimit} lượt sử dụng</small>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                            
                            <!-- Usage History - Placeholder for now -->
                            <div class="card">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-list-ul me-2"></i>Lịch sử sử dụng gần đây
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <!-- This would be populated with actual order history using the voucher -->
                                    <div class="alert alert-info">
                                        <i class="bi bi-info-circle me-2"></i>
                                        Chức năng hiển thị lịch sử đơn hàng sử dụng voucher đang được phát triển.
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
                                                <div class="voucher-type-preview">${voucher.discountType == 'percentage' ? '%' : '₫'}</div>
                                            </div>
                                            <div class="voucher-body">
                                                <div class="voucher-name-preview">${voucher.description}</div>
                                                <div class="voucher-discount-preview">${voucher.formattedValue}</div>
                                                <div class="voucher-condition-preview">Đơn tối thiểu: ${voucher.formattedMinOrderAmount}</div>
                                            </div>
                                            <div class="voucher-footer">
                                                <div class="voucher-date-preview">
                                                    <fmt:formatDate value="${voucher.startAtDate}" pattern="dd/MM/yyyy" /> - 
                                                    <fmt:formatDate value="${voucher.endAtDate}" pattern="dd/MM/yyyy" />
                                                </div>
                                                <div class="voucher-usage-preview">
                                                    <c:choose>
                                                        <c:when test="${voucher.usageLimit != null}">
                                                            Còn lại: ${voucher.usageLimit - voucher.usageCount} lượt
                                                        </c:when>
                                                        <c:otherwise>
                                                            Không giới hạn
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Status Actions -->
                            <div class="card mb-4">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-toggles me-2"></i>Thay đổi trạng thái
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <form action="${pageContext.request.contextPath}/admin/vouchers/edit" method="post" id="statusForm">
                                        <input type="hidden" name="id" value="${voucher.voucherId}">
                                        <input type="hidden" name="action" value="update_status">
                                        
                                        <div class="d-grid gap-2">
                                            <c:if test="${voucher.status != 'active' && !voucher.expired}">
                                                <button type="button" class="btn btn-success" onclick="updateStatus('active')">
                                                    <i class="bi bi-check-circle me-2"></i>Kích hoạt
                                                </button>
                                            </c:if>
                                            
                                            <c:if test="${voucher.status != 'inactive'}">
                                                <button type="button" class="btn btn-secondary" onclick="updateStatus('inactive')">
                                                    <i class="bi bi-pause-circle me-2"></i>Tạm ngưng
                                                </button>
                                            </c:if>
                                            
                                            <c:if test="${voucher.status != 'expired' && !voucher.expired}">
                                                <button type="button" class="btn btn-danger" onclick="updateStatus('expired')">
                                                    <i class="bi bi-x-circle me-2"></i>Đánh dấu hết hạn
                                                </button>
                                            </c:if>
                                        </div>
                                        
                                        <input type="hidden" name="status" id="statusValue">
                                    </form>
                                </div>
                            </div>
                            
                            <!-- Statistics - Placeholder for now -->
                            <div class="card">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-graph-up me-2"></i>Thống kê
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <!-- This would be populated with actual statistics -->
                                    <div class="stats-list">
                                        <div class="stat-item">
                                            <div class="stat-label">Lượt sử dụng:</div>
                                            <div class="stat-value">${voucher.usageCount}</div>
                                        </div>
                                        <div class="stat-item">
                                            <div class="stat-label">Trạng thái:</div>
                                            <div class="stat-value">${voucher.displayStatus}</div>
                                        </div>
                                        <div class="stat-item">
                                            <div class="stat-label">Thời gian còn lại:</div>
                                            <div class="stat-value" id="remainingTime">Đang tính...</div>
                                        </div>
                                        <div class="stat-item">
                                            <div class="stat-label">ID Voucher:</div>
                                            <div class="stat-value">#${voucher.voucherId}</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                            </div>
                        </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Xác nhận xóa</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Bạn có chắc chắn muốn xóa voucher <strong id="deleteVoucherCode"></strong>?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <form id="deleteForm" action="${pageContext.request.contextPath}/admin/vouchers/delete" method="post">
                        <input type="hidden" id="deleteVoucherId" name="id" value="">
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                        </div>
                        </div>
                        </div>
                    </div>

    <!-- Status Confirmation Modal -->
    <div class="modal fade" id="statusModal" tabindex="-1" aria-labelledby="statusModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="statusModalLabel">Xác nhận thay đổi trạng thái</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                <div class="modal-body" id="statusModalBody">
                    Bạn có chắc chắn muốn thay đổi trạng thái voucher?
                        </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-primary" id="confirmStatusBtn">Xác nhận</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Copy voucher code to clipboard
        function copyVoucherCode() {
            const code = document.getElementById('voucherCode').textContent;
            navigator.clipboard.writeText(code).then(() => {
                alert('Đã sao chép mã voucher: ' + code);
            });
        }
        
        // Delete confirmation
        function confirmDelete(id, code) {
            document.getElementById('deleteVoucherId').value = id;
            document.getElementById('deleteVoucherCode').textContent = code;
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            deleteModal.show();
        }
        
        // Update status
        function updateStatus(status) {
            const statusModal = new bootstrap.Modal(document.getElementById('statusModal'));
            const statusModalBody = document.getElementById('statusModalBody');
            const statusValue = document.getElementById('statusValue');
            const confirmStatusBtn = document.getElementById('confirmStatusBtn');
            
            statusValue.value = status;
            
            let message = '';
            if (status === 'active') {
                message = 'Bạn có chắc chắn muốn kích hoạt voucher này?';
            } else if (status === 'inactive') {
                message = 'Bạn có chắc chắn muốn tạm ngưng voucher này?';
            } else if (status === 'expired') {
                message = 'Bạn có chắc chắn muốn đánh dấu voucher này đã hết hạn?';
            }
            
            statusModalBody.textContent = message;
            
            confirmStatusBtn.onclick = function() {
                document.getElementById('statusForm').submit();
            };
            
            statusModal.show();
        }
        
        // Calculate and update remaining time
        function updateRemainingTime() {
            const endDate = new Date('${voucher.endAt}');
            const now = new Date();
            
            if (now > endDate) {
                document.getElementById('remainingTime').textContent = 'Đã hết hạn';
                return;
            }
            
            const diffTime = endDate - now;
            const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
            const diffHours = Math.floor((diffTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const diffMinutes = Math.floor((diffTime % (1000 * 60 * 60)) / (1000 * 60));
            
            let remainingText = '';
            if (diffDays > 0) {
                remainingText += diffDays + ' ngày ';
            }
            if (diffHours > 0 || diffDays > 0) {
                remainingText += diffHours + ' giờ ';
            }
            remainingText += diffMinutes + ' phút';
            
            document.getElementById('remainingTime').textContent = remainingText;
        }
        
        // Update remaining time on page load and every minute
        updateRemainingTime();
        setInterval(updateRemainingTime, 60000);
    </script>
</body>
</html>