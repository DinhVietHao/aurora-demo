<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Cài đặt - Aurora Bookstore</title>
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
        .stats-card-purple {
            background: linear-gradient(45deg, #6f42c1, #9b59b6);
            color: white;
        }
        .setting-table {
            vertical-align: middle;
        }
        .action-buttons {
            display: flex;
            gap: 5px;
        }
        .setting-value {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
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
                        <h1 class="mt-4">Quản lý Cài đặt</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Cài đặt</li>
                            </ol>
                        </nav>
                    </div>

                    <!-- Notification messages -->
                    <c:if test="${param.success != null}">
                        <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                            <c:choose>
                                <c:when test="${param.success == 'added'}">Cài đặt đã được thêm thành công!</c:when>
                                <c:when test="${param.success == 'updated'}">Cài đặt đã được cập nhật thành công!</c:when>
                                <c:when test="${param.success == 'deleted'}">Cài đặt đã được xóa thành công!</c:when>
                                <c:otherwise>Thao tác thành công!</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                            <c:choose>
                                <c:when test="${param.error == 'notfound'}">Không tìm thấy cài đặt!</c:when>
                                <c:when test="${param.error == 'add'}">Không thể thêm cài đặt. Key có thể đã tồn tại!</c:when>
                                <c:when test="${param.error == 'update'}">Không thể cập nhật cài đặt!</c:when>
                                <c:when test="${param.error == 'delete'}">Không thể xóa cài đặt!</c:when>
                                <c:when test="${param.error == 'invalid'}">Dữ liệu không hợp lệ!</c:when>
                                <c:otherwise>${param.error}</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Statistics Card -->
                    <div class="row mt-4">
                        <div class="col-md-4">
                            <div class="card stats-card stats-card-purple">
                                <div class="card-body">
                                    <div class="stats-content">
                                        <div class="stats-number">${settingList != null ? settingList.size() : 0}</div>
                                        <div class="stats-label">Tổng số cài đặt</div>
                                    </div>
                                    <div class="stats-icon">
                                        <i class="bi bi-gear"></i>
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
                                    <form method="get" action="${pageContext.request.contextPath}/admin/setting-management" id="filterForm">
                                        <div class="row g-3 align-items-end">
                                            <div class="col-md-4">
                                                <label for="search" class="form-label">Tìm kiếm</label>
                                                <input type="text" class="form-control" id="search" name="search"
                                                       value="${searchTerm}" placeholder="Tìm theo key, value, mô tả...">
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
                                                    <i class="bi bi-search me-1"></i>Tìm
                                                </button>
                                                <a href="${pageContext.request.contextPath}/admin/setting-management" class="btn btn-secondary">
                                                    <i class="bi bi-arrow-clockwise me-1"></i>
                                                </a>
                                            </div>
                                            <div class="col-md-3 text-end">
                                                <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addSettingModal">
                                                    <i class="bi bi-plus-circle me-2"></i>Thêm Cài đặt
                                                </button>
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Setting List -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-list-ul me-2"></i>Danh sách Cài đặt
                                    </h5>
                                    <c:if test="${pagination != null}">
                                        <span class="badge bg-info">
                                            Hiển thị ${pagination.startRecord}-${pagination.endRecord} / ${pagination.totalRecords} bản ghi
                                        </span>
                                    </c:if>
                                </div>
                                <div class="card-body">
                                    <c:choose>
                                        <c:when test="${empty settingList}">
                                            <div class="alert alert-info text-center" role="alert">
                                                <i class="bi bi-info-circle me-2"></i>Không tìm thấy cài đặt nào phù hợp với điều kiện tìm kiếm.
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table table-hover setting-table">
                                            <thead>
                                                <tr>
                                                    <th>ID</th>
                                                    <th>Key</th>
                                                    <th>Value</th>
                                                    <th>Mô tả</th>
                                                    <th>Ngày tạo</th>
                                                    <th>Thao tác</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="setting" items="${settingList}">
                                                <tr>
                                                    <td><strong>${setting.settingId}</strong></td>
                                                    <td><code>${setting.settingKey}</code></td>
                                                    <td>
                                                        <div class="setting-value" title="${setting.settingValue}">
                                                            ${setting.settingValue}
                                                        </div>
                                                    </td>
                                                    <td>${setting.description}</td>
                                                    <td>
                                                        <c:if test="${setting.createdAt != null}">
                                                            <c:set var="dateStr" value="${setting.createdAt.toString()}" />
                                                            <c:set var="datePart" value="${dateStr.substring(0, 10)}" />
                                                            <c:set var="timePart" value="${dateStr.substring(11, 16)}" />
                                                            ${datePart.substring(8, 10)}/${datePart.substring(5, 7)}/${datePart.substring(0, 4)} ${timePart}
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <div class="action-buttons">
                                                            <button class="btn btn-sm btn-outline-warning"
                                                                    onclick="editSetting(${setting.settingId}, '${setting.settingKey}', `${setting.settingValue}`, `${setting.description}`)"
                                                                    title="Chỉnh sửa">
                                                                <i class="bi bi-pencil"></i>
                                                            </button>
                                                            <button class="btn btn-sm btn-outline-danger"
                                                                    onclick="confirmDelete(${setting.settingId}, '${setting.settingKey}')"
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

    <!-- Add Setting Modal -->
    <div class="modal fade" id="addSettingModal" tabindex="-1" aria-labelledby="addSettingModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addSettingModalLabel">Thêm Cài đặt mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/admin/setting-management" method="post">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="settingKey" class="form-label">Setting Key <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="settingKey" name="settingKey" required
                                   placeholder="Ví dụ: site_name, max_upload_size">
                            <small class="text-muted">Key phải là duy nhất</small>
                        </div>
                        <div class="mb-3">
                            <label for="settingValue" class="form-label">Setting Value</label>
                            <textarea class="form-control" id="settingValue" name="settingValue" rows="3"
                                      placeholder="Giá trị của cài đặt"></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="description" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="description" name="description" rows="2"
                                      placeholder="Mô tả về cài đặt này"></textarea>
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

    <!-- Edit Setting Modal -->
    <div class="modal fade" id="editSettingModal" tabindex="-1" aria-labelledby="editSettingModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editSettingModalLabel">Chỉnh sửa Cài đặt</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/admin/setting-management" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" id="editSettingId" name="settingId">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="editSettingKey" class="form-label">Setting Key <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="editSettingKey" name="settingKey" required>
                        </div>
                        <div class="mb-3">
                            <label for="editSettingValue" class="form-label">Setting Value</label>
                            <textarea class="form-control" id="editSettingValue" name="settingValue" rows="3"></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="editDescription" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="editDescription" name="description" rows="2"></textarea>
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
                    Bạn có chắc chắn muốn xóa cài đặt <strong><code id="deleteSettingKey"></code></strong>?
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
        // Edit Setting
        function editSetting(settingId, settingKey, settingValue, description) {
            document.getElementById('editSettingId').value = settingId;
            document.getElementById('editSettingKey').value = settingKey;
            document.getElementById('editSettingValue').value = settingValue || '';
            document.getElementById('editDescription').value = description || '';
            const editModal = new bootstrap.Modal(document.getElementById('editSettingModal'));
            editModal.show();
        }

        // Delete confirmation
        function confirmDelete(settingId, settingKey) {
            document.getElementById('deleteSettingKey').textContent = settingKey;
            document.getElementById('deleteConfirmBtn').href =
                '${pageContext.request.contextPath}/admin/setting-management?action=delete&settingId=' + settingId;
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            deleteModal.show();
        }
    </script>
</body>
</html>

