<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý người dùng - Aurora Admin</title>
    <jsp:include page="/WEB-INF/views/layouts/_head_admin.jsp" />
</head>
<body class="sb-nav-fixed">
<jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

<div id="layoutSidenav">
    <jsp:include page="/WEB-INF/views/layouts/_sidebar_admin.jsp" />

    <div id="layoutSidenav_content">
        <main>
            <div class="container-fluid px-4">
                <div class="d-flex justify-content-between align-items-center">
                    <button id="sidebarToggle" class="btn btn-outline-secondary btn-sm me-3" type="button">
                        <i class="bi bi-list"></i>
                    </button>
                    <h1 class="mt-4 dashboard-title">Quản lý người dùng</h1>
                </div>

                <div class="card mt-4">
                    <div class="card-header">
                        <div class="row">
                            <div class="col-md-8">
                                <form method="get" action="<c:url value='/admin/users'/>" class="d-flex gap-2">
                                    <div class="input-group">
                                        <input type="text" name="q" value="${q}" class="form-control" placeholder="Tìm kiếm theo tên, email...">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-search"></i> Tìm kiếm
                                        </button>
                                    </div>
                                    
                                    <select name="status" class="form-select" style="width: auto;">
                                        <option value="" ${status == '' ? 'selected' : ''}>Tất cả trạng thái</option>
                                        <option value="active" ${status == 'active' ? 'selected' : ''}>Hoạt động</option>
                                        <option value="locked" ${status == 'locked' ? 'selected' : ''}>Đã khóa</option>
                                    </select>
                                    
                                    <select name="role" class="form-select" style="width: auto;">
                                        <option value="" ${role == '' ? 'selected' : ''}>Tất cả vai trò</option>
                                        <option value="CUSTOMER" ${role == 'CUSTOMER' ? 'selected' : ''}>Khách hàng</option>
                                        <option value="SHOP_OWNER" ${role == 'SHOP_OWNER' ? 'selected' : ''}>Chủ shop</option>
                                        <option value="ADMIN" ${role == 'ADMIN' ? 'selected' : ''}>Quản trị viên</option>
                                    </select>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="card-body table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Họ tên</th>
                                <th>Email</th>
                                <th>Điện thoại</th>
                                <th>Điểm</th>
                                <th>CMND/CCCD</th>
                                <th>Vai trò</th>
                                <th>Trạng thái</th>
                                <th>Nhà cung cấp</th>
                                <th>Tạo lúc</th>
                                <th>Hành động</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${users}" var="u">
                                <tr>
                                    <td>${u.userID}</td>
                                    <td>${u.fullName}</td>
                                    <td>${u.email}</td>
                                    <td>${u.phone}</td>
                                    <td>${u.points}</td>
                                    <td>${u.nationalID}</td>
                                    <td>${u.roles}</td>
                                    <td>
                                        <span class="badge ${u.status.trim() == 'active' ? 'bg-success' : 'bg-danger'}">
                                            ${u.status.trim() == 'active' ? 'Hoạt động' : 'Đã khóa'}
                                        </span>
                                    </td>
                                    <td>${u.authProvider}</td>
                                    <td>${u.createdAt}</td>
                                    <td>
                                        <form method="post" action="<c:url value='/admin/users'/>">
                                            <input type="hidden" name="action" value="toggle-status">
                                            <input type="hidden" name="id" value="${u.userID}">
                                            <button type="submit" class="btn btn-sm ${u.status.trim() == 'active' ? 'btn-danger' : 'btn-success'}">
                                                ${u.status.trim() == 'active' ? '<i class="bi bi-lock"></i> Khóa' : '<i class="bi bi-unlock"></i> Mở khóa'}
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    
                    <c:if test="${total > pageSize}">
                        <div class="card-footer">
                            <nav>
                                <ul class="pagination justify-content-center">
                                    <c:forEach begin="1" end="${(total + pageSize - 1) / pageSize}" var="i">
                                        <li class="page-item ${page == i ? 'active' : ''}">
                                            <a class="page-link" href="<c:url value='/admin/users?page=${i}&q=${q}&status=${status}&role=${role}'/>">
                                                ${i}
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </nav>
                        </div>
                    </c:if>
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

