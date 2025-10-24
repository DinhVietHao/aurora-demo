<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý VAT - Aurora Bookstore</title>
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
        .vat-table {
            vertical-align: middle;
        }
        .action-buttons {
            display: flex;
            gap: 5px;
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
                        <h1 class="mt-4">Quản lý VAT</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                                <li class="breadcrumb-item active" aria-current="page">VAT</li>
                            </ol>
                        </nav>
                    </div>

                    <!-- Notification messages -->
                    <c:if test="${param.success != null}">
                        <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                            <c:choose>
                                <c:when test="${param.success == 'added'}">VAT đã được thêm thành công!</c:when>
                                <c:when test="${param.success == 'updated'}">VAT đã được cập nhật thành công!</c:when>
                                <c:when test="${param.success == 'deleted'}">VAT đã được xóa thành công!</c:when>
                                <c:otherwise>Thao tác thành công!</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                            <c:choose>
                                <c:when test="${param.error == 'notfound'}">Không tìm thấy VAT!</c:when>
                                <c:when test="${param.error == 'add'}">Không thể thêm VAT. Mã VAT có thể đã tồn tại!</c:when>
                                <c:when test="${param.error == 'update'}">Không thể cập nhật VAT!</c:when>
                                <c:when test="${param.error == 'delete'}">Không thể xóa VAT. VAT này đang được sử dụng bởi danh mục!</c:when>
                                <c:when test="${param.error == 'invalid'}">Dữ liệu không hợp lệ!</c:when>
                                <c:otherwise>${param.error}</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Statistics Card -->
                    <div class="row mt-4">
                        <div class="col-md-4">
                            <div class="card stats-card stats-card-blue">
                                <div class="card-body">
                                    <div class="stats-content">
                                        <div class="stats-number">${vatList != null ? vatList.size() : 0}</div>
                                        <div class="stats-label">Tổng số mức VAT</div>
                                    </div>
                                    <div class="stats-icon">
                                        <i class="bi bi-percent"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Filter and Search Bar -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-body">
                                    <form method="get" action="${pageContext.request.contextPath}/admin/vat-management" id="filterForm">
                                        <div class="row g-3 align-items-end">
                                            <div class="col-md-4">
                                                <label for="search" class="form-label">Tìm kiếm</label>
                                                <input type="text" class="form-control" id="search" name="search"
                                                       value="${searchTerm}" placeholder="Tìm theo mã VAT hoặc mô tả...">
                                            </div>
                                            <div class="col-md-2">
                                                <label for="pageSize" class="form-label">Số dòng</label>
                                                <select class="form-select" id="pageSize" name="pageSize" onchange="document.getElementById('filterForm').submit();">
                                                    <option value="5" ${currentPageSize == 5 ? 'selected' : ''}>5</option>
                                                    <option value="10" ${currentPageSize == 10 ? 'selected' : ''}>10</option>
                                                    <option value="20" ${currentPageSize == 20 ? 'selected' : ''}>20</option>
                                                    <option value="50" ${currentPageSize == 50 ? 'selected' : ''}>50</option>
                                                    <option value="100" ${currentPageSize == 100 ? 'selected' : ''}>100</option>
                                                </select>
                                            </div>
                                            <div class="col-md-3">
                                                <button type="submit" class="btn btn-primary me-2">
                                                    <i class="bi bi-search me-1"></i>Tìm kiếm
                                                </button>
                                                <a href="${pageContext.request.contextPath}/admin/vat-management" class="btn btn-secondary">
                                                    <i class="bi bi-arrow-clockwise me-1"></i>Làm mới
                                                </a>
                                            </div>
                                            <div class="col-md-3 text-end">
                                                <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addVATModal">
                                                    <i class="bi bi-plus-circle me-2"></i>Thêm VAT mới
                                                </button>
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- VAT List -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-list-ul me-2"></i>Danh sách VAT
                                    </h5>
                                    <c:if test="${pagination != null}">
                                        <span class="badge bg-info">
                                            Hiển thị ${pagination.startRecord}-${pagination.endRecord} / ${pagination.totalRecords} bản ghi
                                        </span>
                                    </c:if>
                                </div>
                                <div class="card-body">
                                    <c:choose>
                                        <c:when test="${empty vatList}">
                                            <div class="alert alert-info text-center" role="alert">
                                                <i class="bi bi-info-circle me-2"></i>Không tìm thấy VAT nào phù hợp với điều kiện tìm kiếm.
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table table-hover vat-table">
                                            <thead>
                                                <tr>
                                                    <th>Mã VAT</th>
                                                    <th>Thuế suất (%)</th>
                                                    <th>Mô tả</th>
                                                    <th>Trạng thái</th>
                                                    <th>Thao tác</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="vat" items="${vatList}">
                                                <tr>
                                                    <td><strong>${vat.vatCode}</strong></td>
                                                    <td><span class="badge bg-primary">${vat.vatRate}%</span></td>
                                                    <td>${vat.description}</td>
                                                    <td>
                                                        <span class="badge bg-success">Đang sử dụng</span>
                                                    </td>
                                                    <td>
                                                        <div class="action-buttons">
                                                            <button class="btn btn-sm btn-outline-warning"
                                                                    onclick="editVAT('${vat.vatCode}', ${vat.vatRate}, '${vat.description}')"
                                                                    title="Chỉnh sửa">
                                                                <i class="bi bi-pencil"></i>
                                                            </button>
                                                            <button class="btn btn-sm btn-outline-danger"
                                                                    onclick="confirmDelete('${vat.vatCode}')"
                                                                    title="Xóa">
                                                                <i class="bi bi-trash"></i>
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>

                                    <!-- Pagination -->
                                    <c:if test="${pagination != null && pagination.totalPages > 1}">
                                        <nav aria-label="Page navigation" class="mt-4">
                                            <ul class="pagination justify-content-center">
                                                <!-- First Page -->
                                                <li class="page-item ${pagination.currentPage == 1 ? 'disabled' : ''}">
                                                    <a class="page-link" href="?page=1&pageSize=${currentPageSize}&search=${searchTerm}">
                                                        <i class="bi bi-chevron-double-left"></i>
                                                    </a>
                                                </li>

                                                <!-- Previous Page -->
                                                <li class="page-item ${!pagination.hasPrevious() ? 'disabled' : ''}">
                                                    <a class="page-link" href="?page=${pagination.previousPage}&pageSize=${currentPageSize}&search=${searchTerm}">
                                                        <i class="bi bi-chevron-left"></i>
                                                    </a>
                                                </li>

                                                <!-- Page Numbers -->
                                                <c:forEach begin="${pagination.currentPage > 2 ? pagination.currentPage - 2 : 1}"
                                                           end="${pagination.currentPage + 2 < pagination.totalPages ? pagination.currentPage + 2 : pagination.totalPages}"
                                                           var="i">
                                                    <li class="page-item ${pagination.currentPage == i ? 'active' : ''}">
                                                        <a class="page-link" href="?page=${i}&pageSize=${currentPageSize}&search=${searchTerm}">${i}</a>
                                                    </li>
                                                </c:forEach>

                                                <!-- Next Page -->
                                                <li class="page-item ${!pagination.hasNext() ? 'disabled' : ''}">
                                                    <a class="page-link" href="?page=${pagination.nextPage}&pageSize=${currentPageSize}&search=${searchTerm}">
                                                        <i class="bi bi-chevron-right"></i>
                                                    </a>
                                                </li>

                                                <!-- Last Page -->
                                                <li class="page-item ${pagination.currentPage == pagination.totalPages ? 'disabled' : ''}">
                                                    <a class="page-link" href="?page=${pagination.totalPages}&pageSize=${currentPageSize}&search=${searchTerm}">
                                                        <i class="bi bi-chevron-double-right"></i>
                                                    </a>
                                                </li>
                                            </ul>
                                            <div class="text-center text-muted">
                                                <small>Trang ${pagination.currentPage} / ${pagination.totalPages}</small>
                                            </div>
                                        </nav>
                                    </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>

            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
        </div>
    </div>

    <!-- Add VAT Modal -->
    <div class="modal fade" id="addVATModal" tabindex="-1" aria-labelledby="addVATModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addVATModalLabel">Thêm VAT mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/admin/vat-management" method="post">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="vatCode" class="form-label">Mã VAT <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="vatCode" name="vatCode" required
                                   placeholder="Ví dụ: VAT0, VAT5, VAT10">
                        </div>
                        <div class="mb-3">
                            <label for="vatRate" class="form-label">Thuế suất (%) <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="vatRate" name="vatRate"
                                   min="0" max="100" step="0.01" required placeholder="Ví dụ: 5, 10">
                        </div>
                        <div class="mb-3">
                            <label for="description" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="description" name="description" rows="3"
                                      placeholder="Mô tả về mức thuế này"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-success">Thêm</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Edit VAT Modal -->
    <div class="modal fade" id="editVATModal" tabindex="-1" aria-labelledby="editVATModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editVATModalLabel">Chỉnh sửa VAT</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/admin/vat-management" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" id="editVatCode" name="vatCode">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="editVatCodeDisplay" class="form-label">Mã VAT</label>
                            <input type="text" class="form-control" id="editVatCodeDisplay" disabled>
                        </div>
                        <div class="mb-3">
                            <label for="editVatRate" class="form-label">Thuế suất (%) <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="editVatRate" name="vatRate"
                                   min="0" max="100" step="0.01" required>
                        </div>
                        <div class="mb-3">
                            <label for="editDescription" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="editDescription" name="description" rows="3"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">Cập nhật</button>
                    </div>
                </form>
            </div>
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
                    Bạn có chắc chắn muốn xóa VAT <strong id="deleteVATCode"></strong>?<br>
                    <small class="text-danger">Lưu ý: Không thể xóa VAT đang được sử dụng bởi danh mục sản phẩm.</small>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <a id="deleteConfirmBtn" href="#" class="btn btn-danger">Xóa</a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Edit VAT
        function editVAT(vatCode, vatRate, description) {
            document.getElementById('editVatCode').value = vatCode;
            document.getElementById('editVatCodeDisplay').value = vatCode;
            document.getElementById('editVatRate').value = vatRate;
            document.getElementById('editDescription').value = description;
            const editModal = new bootstrap.Modal(document.getElementById('editVATModal'));
            editModal.show();
        }

        // Delete confirmation
        function confirmDelete(vatCode) {
            document.getElementById('deleteVATCode').textContent = vatCode;
            document.getElementById('deleteConfirmBtn').href =
                '${pageContext.request.contextPath}/admin/vat-management?action=delete&vatCode=' + vatCode;
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            deleteModal.show();
        }
    </script>
</body>
</html>
