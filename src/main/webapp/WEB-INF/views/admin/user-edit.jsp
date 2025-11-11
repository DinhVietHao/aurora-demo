<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa người dùng - Aurora Admin</title>
    <jsp:include page="/WEB-INF/views/layouts/_head_admin.jsp" />
</head>
<body class="sb-nav-fixed">
<jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

<div id="layoutSidenav">
    <jsp:include page="/WEB-INF/views/layouts/_sidebar_admin.jsp" />

    <div id="layoutSidenav_content">
        <main>
            <div class="container-fluid px-4">
                <h1 class="mt-4 dashboard-title">Chỉnh sửa người dùng</h1>
                <div class="d-flex align-items-center">
                    <button id="sidebarToggle" class="btn btn-outline-secondary btn-sm me-3" type="button">
                        <i class="bi bi-list"></i>
                    </button>
                    <a href="<c:url value='/admin/users'/>" class="btn btn-secondary btn-sm">
                        <i class="bi bi-arrow-left"></i> Quay lại
                    </a>
                </div>

                <div class="card mt-4">
                    <div class="card-header">
                        <h5 class="mb-0">Thông tin người dùng #${user.userID}</h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty success}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                ${success}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <form method="post" action="<c:url value='/admin/users'/>">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="${user.userID}">

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="fullName" class="form-label">Họ và tên <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="fullName" name="fullName" 
                                               value="${user.fullName}" required>
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                                        <input type="email" class="form-control" id="email" name="email" 
                                               value="${user.email}" required>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="status" class="form-label">Trạng thái <span class="text-danger">*</span></label>
                                        <select class="form-select" id="status" name="status" required>
                                            <option value="ACTIVE" ${user.status == 'ACTIVE' || user.status == 'active' ? 'selected' : ''}>Hoạt động</option>
                                            <option value="LOCKED" ${user.status == 'LOCKED' || user.status == 'locked' ? 'selected' : ''}>Đã khóa</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="authProvider" class="form-label">Nhà cung cấp</label>
                                        <input type="text" class="form-control" id="authProvider" 
                                               value="${user.authProvider}" readonly disabled>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Vai trò <span class="text-danger">*</span></label>
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="roles" value="CUSTOMER" 
                                           id="roleCustomer" ${user.roles.contains('Khách hàng') ? 'checked' : ''}>
                                    <label class="form-check-label" for="roleCustomer">
                                        Khách hàng
                                    </label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="roles" value="SELLER" 
                                           id="roleSeller" ${user.roles.contains('Người bán') ? 'checked' : ''}>
                                    <label class="form-check-label" for="roleSeller">
                                        Người bán
                                    </label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="roles" value="ADMIN" 
                                           id="roleAdmin" ${user.roles.contains('Quản trị') ? 'checked' : ''}>
                                    <label class="form-check-label" for="roleAdmin">
                                        Quản trị viên
                                    </label>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="avatarUrl" class="form-label">URL Avatar</label>
                                <input type="url" class="form-control" id="avatarUrl" name="avatarUrl" 
                                       value="${user.avatarUrl}" placeholder="https://example.com/avatar.jpg">
                                <c:if test="${not empty user.avatarUrl}">
                                    <div class="mt-2">
                                        <img src="${user.avatarUrl}" alt="Avatar" class="img-thumbnail" style="max-width: 150px;">
                                    </div>
                                </c:if>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Ngày tạo</label>
                                <input type="text" class="form-control" value="${user.createdAt}" readonly disabled>
                            </div>

                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-save"></i> Lưu thay đổi
                                </button>
                                <a href="<c:url value='/admin/users'/>" class="btn btn-secondary">
                                    <i class="bi bi-x-circle"></i> Hủy
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
        
        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
    </div>
</div>

<jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
<script src="<c:url value='/assets/js/adminDashboard.js'/>"></script>
</body>
</html>

