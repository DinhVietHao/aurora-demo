<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${voucher != null ? 'Chỉnh sửa' : 'Tạo mới'} Voucher - Aurora Bookstore</title>
    <jsp:include page="/WEB-INF/views/layouts/_head_admin.jsp" />
    <style>
        .create-title {
            font-weight: bold;
            color: #333;
            margin-bottom: 1.5rem;
        }
        .voucher-preview {
            margin-top: 1rem;
        }
        .voucher-preview-card {
            border: 2px dashed #ddd;
            border-radius: 10px;
            padding: 20px;
            background-color: #f8f9fa;
            position: relative;
            overflow: hidden;
        }
        .voucher-preview-card::before {
            content: "Preview";
            position: absolute;
            top: 5px;
            right: 5px;
            font-size: 12px;
            background-color: #6c757d;
            color: white;
            padding: 2px 8px;
            border-radius: 4px;
            opacity: 0.7;
        }
        .voucher-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        .voucher-code-preview {
            font-size: 1.5rem;
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
            font-size: 1.8rem;
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
        .tips-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        .tip-item {
            display: flex;
            align-items: center;
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
                        <h1 class="mt-4 create-title">${voucher != null ? 'Chỉnh sửa' : 'Tạo mới'} Voucher</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/vouchers">Voucher</a></li>
                                <li class="breadcrumb-item active" aria-current="page">${voucher != null ? 'Chỉnh sửa' : 'Tạo mới'}</li>
                            </ol>
                        </nav>
                    </div>
                    
                    <!-- Error message if any -->
                    <c:if test="${error != null}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
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
                                    <form id="voucherForm" action="${pageContext.request.contextPath}${voucher != null ? '/admin/vouchers/edit' : '/admin/vouchers/create'}" method="post">
                                        <c:if test="${voucher != null}">
                                            <input type="hidden" name="id" value="${voucher.voucherId}">
                                        </c:if>
                                        
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="mb-3">
                                                    <label for="code" class="form-label">Mã voucher <span class="text-danger">*</span></label>
                                                    <input type="text" class="form-control" id="code" name="code" value="${voucher != null ? voucher.code : ''}" placeholder="VD: NEWUSER50" required>
                                                    <div class="form-text">Mã voucher phải là duy nhất và không chứa khoảng trắng</div>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="mb-3">
                                                    <label for="description" class="form-label">Mô tả</label>
                                                    <input type="text" class="form-control" id="description" name="description" value="${voucher != null ? voucher.description : ''}" placeholder="VD: Voucher cho người dùng mới">
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="row">
                                            <div class="col-md-4">
                                                <div class="mb-3">
                                                    <label for="discountType" class="form-label">Loại giảm giá <span class="text-danger">*</span></label>
                                                    <select class="form-select" id="discountType" name="discountType" required>
                                                        <option value="">Chọn loại giảm giá</option>
                                                        <option value="PERCENT" ${voucher != null && voucher.discountType == 'PERCENT' ? 'selected' : ''}>Phần trăm (%)</option>
                                                        <option value="AMOUNT" ${voucher != null && voucher.discountType == 'AMOUNT' ? 'selected' : ''}>Số tiền cố định (VNĐ)</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <div class="mb-3">
                                                    <label for="value" class="form-label">Giá trị giảm <span class="text-danger">*</span></label>
                                                    <input type="number" class="form-control" id="value" name="value" value="${voucher != null ? voucher.value : ''}" placeholder="0" min="0" step="0.01" required>
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <div class="mb-3">
                                                    <label for="maxAmount" class="form-label">Giảm tối đa (VNĐ)</label>
                                                    <input type="number" class="form-control" id="maxAmount" name="maxAmount" value="${voucher != null ? voucher.maxAmount : ''}" placeholder="0" min="0">
                                                    <div class="form-text">Chỉ áp dụng cho giảm theo %</div>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="row">
                                            <div class="col-md-4">
                                                <div class="mb-3">
                                                    <label for="minOrderAmount" class="form-label">Đơn hàng tối thiểu (VNĐ)</label>
                                                    <input type="number" class="form-control" id="minOrderAmount" name="minOrderAmount" value="${voucher != null ? voucher.minOrderAmount : '0'}" placeholder="0" min="0">
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <div class="mb-3">
                                                    <label for="usageLimit" class="form-label">Giới hạn sử dụng <span class="text-danger">*</span></label>
                                                    <input type="number" class="form-control" id="usageLimit" name="usageLimit" value="${voucher != null ? voucher.usageLimit : '100'}" placeholder="Nhập giới hạn sử dụng" min="1" required>
                                                    <div class="form-text">Số lần voucher có thể được sử dụng</div>
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <div class="mb-3">
                                                    <label for="perUserLimit" class="form-label">Giới hạn mỗi người</label>
                                                    <input type="number" class="form-control" id="perUserLimit" name="perUserLimit" value="${voucher != null ? voucher.perUserLimit : '1'}" placeholder="Giới hạn mỗi người dùng" min="1">
                                                    <div class="form-text">Số lần mỗi người dùng có thể sử dụng</div>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="mb-3">
                                                    <label for="startAt" class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                                                    <input type="datetime-local" class="form-control" id="startAt" name="startAt" value="${voucher != null ? startAtFormatted : ''}" required>
                                                    <div class="form-text">Không được chọn ngày trong quá khứ</div>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="mb-3">
                                                    <label for="endAt" class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
                                                    <input type="datetime-local" class="form-control" id="endAt" name="endAt" value="${voucher != null ? endAtFormatted : ''}" required>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" id="active" name="status" value="ACTIVE" ${voucher == null || voucher.status == 'ACTIVE' ? 'checked' : ''}>
                                                <label class="form-check-label" for="active">
                                                    Kích hoạt voucher ${voucher != null ? '' : 'ngay sau khi tạo'}
                                                </label>
                                            </div>
                                            <input type="hidden" name="defaultStatus" value="PENDING">
                                        </div>
                                        
                                        <div class="d-flex gap-3">
                                            <button type="submit" class="btn btn-success">
                                                <i class="bi bi-check-circle me-2"></i>${voucher != null ? 'Cập nhật' : 'Tạo'} voucher
                                            </button>
                                            <a href="${pageContext.request.contextPath}/admin/vouchers" class="btn btn-secondary">
                                                <i class="bi bi-arrow-left me-2"></i>Quay lại
                                            </a>
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
                                                <div class="voucher-code-preview" id="previewCode">${voucher != null ? voucher.code : 'VOUCHER_CODE'}</div>
                                                <div class="voucher-type-preview" id="previewType">${voucher != null && voucher.discountType == 'PERCENT' ? '%' : '₫'}</div>
                                            </div>
                                            <div class="voucher-body">
                                                <div class="voucher-name-preview" id="previewName">${voucher != null ? voucher.description : 'Mô tả voucher'}</div>
                                                <div class="voucher-discount-preview" id="previewDiscount">${voucher != null ? voucher.formattedValue : '0%'}</div>
                                                <div class="voucher-condition-preview" id="previewCondition">Đơn tối thiểu: ${voucher != null ? voucher.formattedMinOrderAmount : '0 VNĐ'}</div>
                                            </div>
                                            <div class="voucher-footer">
                                                <div class="voucher-date-preview" id="previewDate">
                                                    <c:choose>
                                                        <c:when test="${voucher != null}">
                                                            <fmt:formatDate value="${voucher.startAtDate}" pattern="dd/MM/yyyy" /> - <fmt:formatDate value="${voucher.endAtDate}" pattern="dd/MM/yyyy" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            Chưa có thời hạn
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="voucher-usage-preview" id="previewUsage">
                                                    <c:choose>
                                                        <c:when test="${voucher != null && voucher.usageLimit != null}">
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
            
            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Preview functionality
        const codeInput = document.getElementById('code');
        const descriptionInput = document.getElementById('description');
        const discountTypeSelect = document.getElementById('discountType');
        const valueInput = document.getElementById('value');
        const maxAmountInput = document.getElementById('maxAmount');
        const minOrderAmountInput = document.getElementById('minOrderAmount');
        const startAtInput = document.getElementById('startAt');
        const endAtInput = document.getElementById('endAt');
        const usageLimitInput = document.getElementById('usageLimit');
        
        const previewCode = document.getElementById('previewCode');
        const previewType = document.getElementById('previewType');
        const previewName = document.getElementById('previewName');
        const previewDiscount = document.getElementById('previewDiscount');
        const previewCondition = document.getElementById('previewCondition');
        const previewDate = document.getElementById('previewDate');
        const previewUsage = document.getElementById('previewUsage');
        
        function updatePreview() {
            // Update code
            previewCode.textContent = codeInput.value || 'VOUCHER_CODE';
            
            // Update description
            previewName.textContent = descriptionInput.value || 'Mô tả voucher';
            
            // Update discount type and value
            const type = discountTypeSelect.value;
            previewType.textContent = type === 'PERCENT' ? '%' : '₫';
            
            const value = valueInput.value || '0';
            if (type === 'PERCENT') {
                previewDiscount.textContent = value + '%';
            } else {
                previewDiscount.textContent = new Intl.NumberFormat('vi-VN').format(value) + ' VNĐ';
            }
            
            // Update min order amount
            const minOrderAmount = minOrderAmountInput.value || '0';
            previewCondition.textContent = 'Đơn tối thiểu: ' + new Intl.NumberFormat('vi-VN').format(minOrderAmount) + ' VNĐ';
            
            // Update dates if both are provided
            if (startAtInput.value && endAtInput.value) {
                const startDate = new Date(startAtInput.value);
                const endDate = new Date(endAtInput.value);
                const formatDate = (date) => {
                    return new Intl.DateTimeFormat('vi-VN', {
                        day: '2-digit',
                        month: '2-digit',
                        year: 'numeric'
                    }).format(date);
                };
                previewDate.textContent = formatDate(startDate) + ' - ' + formatDate(endDate);
            } else {
                previewDate.textContent = 'Chưa có thời hạn';
            }
            
            // Update usage limit
            const usageLimit = usageLimitInput.value;
            if (usageLimit) {
                previewUsage.textContent = `Còn lại: ${usageLimit} lượt`;
            } else {
                previewUsage.textContent = 'Không giới hạn';
            }
        }
        
        // Add event listeners
        [codeInput, descriptionInput, discountTypeSelect, valueInput, 
         maxAmountInput, minOrderAmountInput, startAtInput, 
         endAtInput, usageLimitInput].forEach(el => {
            el.addEventListener('input', updatePreview);
        });
        
        // Set min date for startAt field to current date/time
        function setMinStartDate() {
            const now = new Date();
            // Format date as yyyy-MM-ddThh:mm (required format for datetime-local input)
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const day = String(now.getDate()).padStart(2, '0');
            const hours = String(now.getHours()).padStart(2, '0');
            const minutes = String(now.getMinutes()).padStart(2, '0');
            
            const formattedDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
            startAtInput.setAttribute('min', formattedDateTime);
        }
        
        // Set min date for endAt field based on startAt value
        function updateMinEndDate() {
            if (startAtInput.value) {
                endAtInput.setAttribute('min', startAtInput.value);
            }
        }
        
        // Initialize min dates
        setMinStartDate();
        startAtInput.addEventListener('change', updateMinEndDate);
        updateMinEndDate();

        // Form validation
        document.getElementById('voucherForm').addEventListener('submit', function(e) {
            const startDate = new Date(startAtInput.value);
            const endDate = new Date(endAtInput.value);
            const now = new Date();
            
            // Check if start date is in the past
            if (startDate < now) {
                e.preventDefault();
                alert('Ngày bắt đầu không được nằm trong quá khứ!');
                return;
            }
            
            // Check if end date is before start date
            if (endDate <= startDate) {
                e.preventDefault();
                alert('Ngày kết thúc phải sau ngày bắt đầu!');
                return;
            }
            
            // Check percentage value
            if (discountTypeSelect.value === 'PERCENT' && valueInput.value > 100) {
                e.preventDefault();
                alert('Giá trị phần trăm không thể lớn hơn 100!');
                return;
            }
        });
        
        // Initialize preview on page load
        updatePreview();
    </script>
</body>
</html>