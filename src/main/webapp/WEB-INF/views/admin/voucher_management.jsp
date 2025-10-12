<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Voucher - Aurora Bookstore</title>
    <jsp:include page="/WEB-INF/views/layouts/_head_admin.jsp" />
    <style>
        .stats-card {
            border: none;
            border-radius: 10px;
            box-shadow: 0 0 15px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }
        .stats-card:hover {
            transform: translateY(-5px);
        }
        .stats-card-blue {
            background: linear-gradient(45deg, #4099ff, #73b4ff);
            color: white;
        }
        .stats-card-orange {
            background: linear-gradient(45deg, #FFB64D, #ffcb80);
            color: white;
        }
        .stats-card-red {
            background: linear-gradient(45deg, #FF5370, #ff869a);
            color: white;
        }
        .stats-card-green {
            background: linear-gradient(45deg, #2ed8b6, #59e0c5);
            color: white;
        }
        .stats-card .card-body {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 25px;
        }
        .stats-content .stats-number {
            font-size: 2.5rem;
            font-weight: bold;
            margin-bottom: 5px;
        }
        .stats-content .stats-label {
            font-size: 1rem;
            opacity: 0.8;
        }
        .stats-icon {
            font-size: 3rem;
            opacity: 0.7;
        }
        .voucher-table {
            vertical-align: middle;
        }
        .voucher-code strong {
            font-size: 1.1rem;
        }
        .action-buttons {
            display: flex;
            gap: 5px;
        }
        .date-range {
            display: flex;
            flex-direction: column;
            font-size: 0.8rem;
        }
        .status-badge {
            padding: 7px 10px;
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
                        <h1 class="mt-4 promotion-title">Quản lý Voucher</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Voucher</li>
                            </ol>
                        </nav>
                    </div>
                    
                    <!-- Notification messages -->
                    <c:if test="${param.success != null}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <c:choose>
                                <c:when test="${param.success == 'voucher_created'}">Voucher đã được tạo thành công!</c:when>
                                <c:when test="${param.success == 'voucher_updated'}">Voucher đã được cập nhật thành công!</c:when>
                                <c:when test="${param.success == 'voucher_deleted'}">Voucher đã được xóa thành công!</c:when>
                                <c:otherwise>Thao tác thành công!</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <c:choose>
                                <c:when test="${param.error == 'voucher_not_found'}">Không tìm thấy voucher!</c:when>
                                <c:when test="${param.error == 'invalid_voucher_id'}">ID voucher không hợp lệ!</c:when>
                                <c:when test="${param.error == 'delete_failed'}">Không thể xóa voucher!</c:when>
                                <c:otherwise>${param.error}</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
                    <!-- Statistics Cards -->
                    <div class="row mt-4">
                        <div class="col-md-3">
                            <div class="card stats-card stats-card-blue">
                                <div class="card-body">
                                    <div class="stats-content">
                                        <div class="stats-number">${activeCount}</div>
                                        <div class="stats-label">Voucher hoạt động</div>
                                    </div>
                                    <div class="stats-icon">
                                        <i class="bi bi-ticket-perforated"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stats-card stats-card-orange">
                                <div class="card-body">
                                    <div class="stats-content">
                                        <div class="stats-number">${pendingCount}</div>
                                        <div class="stats-label">Voucher chờ</div>
                                    </div>
                                    <div class="stats-icon">
                                        <i class="bi bi-clock"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stats-card stats-card-red">
                                <div class="card-body">
                                    <div class="stats-content">
                                        <div class="stats-number">${expiredCount}</div>
                                        <div class="stats-label">Voucher hết hạn</div>
                                    </div>
                                    <div class="stats-icon">
                                        <i class="bi bi-x-circle"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card stats-card stats-card-green">
                                <div class="card-body">
                                    <div class="stats-content">
                                        <div class="stats-number">${totalUsageCount}</div>
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
                                            <a href="${pageContext.request.contextPath}/admin/vouchers/create" class="btn btn-success" id="createVoucherBtn">
                                                <i class="bi bi-plus-circle me-2"></i>Tạo voucher mới
                                            </a>
                                            <div class="input-group" style="width: 300px;">
                                                <span class="input-group-text">
                                                    <i class="bi bi-search"></i>
                                                </span>
                                                <input type="text" class="form-control" placeholder="Tìm kiếm mã voucher..." id="searchVoucher">
                                            </div>
                                        </div>
                                        <div class="d-flex align-items-center gap-2">
                                            <form id="statusFilterForm" action="${pageContext.request.contextPath}/admin/vouchers" method="get">
                                                <select class="form-select" id="statusFilter" name="status" style="width: 150px;" onchange="document.getElementById('statusFilterForm').submit()">
                                                    <option value="" ${empty statusFilter ? 'selected' : ''}>Tất cả</option>
                                                    <option value="active" ${statusFilter == 'active' ? 'selected' : ''}>Hoạt động</option>
                                                    <option value="pending" ${statusFilter == 'pending' ? 'selected' : ''}>Chờ kích hoạt</option>
                                                    <option value="expired" ${statusFilter == 'expired' ? 'selected' : ''}>Hết hạn</option>
                                                    <option value="inactive" ${statusFilter == 'inactive' ? 'selected' : ''}>Ngừng hoạt động</option>
                                            </select>
                                            </form>
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
                                        <i class="bi bi-list-ul me-2"></i>Danh sách voucher (${vouchers.size()})
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
                                                <c:forEach var="voucher" items="${vouchers}">
                                                <tr>
                                                    <td>
                                                        <div class="voucher-code">
                                                                <strong>${voucher.code}</strong>
                                                                <small class="text-muted d-block">${voucher.description}</small>
                                                        </div>
                                                    </td>
                                                    <td>
                                                            <c:choose>
                                                                <c:when test="${voucher.discountType == 'percentage'}">
                                                        <span class="badge bg-info">%</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                        <span class="badge bg-warning">VNĐ</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                    </td>
                                                    <td>
                                                            <span class="discount-value">${voucher.formattedValue}</span>
                                                            <small class="text-muted d-block">${voucher.formattedMaxAmount}</small>
                                                    </td>
                                                    <td>
                                                            <span class="min-order">${voucher.formattedMinOrderAmount}</span>
                                                    </td>
                                                    <td>
                                                        <div class="date-range">
                                                <small><fmt:formatDate value="${voucher.startAtDate}" pattern="yyyy-MM-dd HH:mm" /></small>
                                                <small><fmt:formatDate value="${voucher.endAtDate}" pattern="yyyy-MM-dd HH:mm" /></small>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <div class="usage-info">
                                                                <span class="used">${voucher.usageCount}</span>/
                                                                <span class="total">${voucher.usageLimit != null ? voucher.usageLimit : '∞'}</span>
                                                        </div>
                                                    </td>
                                                    <td>
                                                            <c:choose>
                                                                <c:when test="${voucher.displayStatus == 'Hoạt động'}">
                                                                    <span class="badge bg-success status-badge">${voucher.displayStatus}</span>
                                                                </c:when>
                                                                <c:when test="${voucher.displayStatus == 'Hết hạn'}">
                                                                    <span class="badge bg-danger status-badge">${voucher.displayStatus}</span>
                                                                </c:when>
                                                                <c:when test="${voucher.displayStatus == 'Chờ'}">
                                                                    <span class="badge bg-warning status-badge">${voucher.displayStatus}</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-secondary status-badge">${voucher.displayStatus}</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="action-buttons">
                                                                <a href="${pageContext.request.contextPath}/admin/vouchers/view?id=${voucher.voucherId}" class="btn btn-sm btn-outline-primary" title="Xem chi tiết">
                                                                <i class="bi bi-eye"></i>
                                                                </a>
                                                                <a href="${pageContext.request.contextPath}/admin/vouchers/edit?id=${voucher.voucherId}" class="btn btn-sm btn-outline-warning" title="Chỉnh sửa">
                                                                <i class="bi bi-pencil"></i>
                                                                </a>
                                                                <button class="btn btn-sm btn-outline-danger" onclick="confirmDelete(${voucher.voucherId}, '${voucher.code}')" title="Xóa">
                                                                <i class="bi bi-trash"></i>
                                                            </button>
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Filter table based on search input
        document.getElementById('searchVoucher').addEventListener('keyup', function() {
            const searchText = this.value.toLowerCase();
            const table = document.querySelector('.voucher-table');
            const rows = table.getElementsByTagName('tr');
            
            for (let i = 1; i < rows.length; i++) {
                const codeCell = rows[i].getElementsByTagName('td')[0];
                if (codeCell) {
                    const codeText = codeCell.textContent || codeCell.innerText;
                    if (codeText.toLowerCase().indexOf(searchText) > -1) {
                        rows[i].style.display = "";
                    } else {
                        rows[i].style.display = "none";
                    }
                }
            }
        });
        
        // Delete confirmation
        function confirmDelete(id, code) {
            document.getElementById('deleteVoucherId').value = id;
            document.getElementById('deleteVoucherCode').textContent = code;
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            deleteModal.show();
        }
    </script>
</body>
</html>