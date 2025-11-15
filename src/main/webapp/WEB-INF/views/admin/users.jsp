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
                                <!-- Removed columns for non-existent fields -->
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
                                    <!-- Removed cells for non-existent fields -->
                                    <td>${u.roles}</td>
                                    <td>
                                        <span class="badge ${u.status == 'active' ? 'bg-success' : 'bg-danger'}">
                                            ${u.status == 'active' ? 'Hoạt động' : 'Đã khóa'}
                                        </span>
                                    </td>
                                    <td>${u.authProvider}</td>
                                    <td>${u.createdAt}</td>
                                    <td>
                                        <button type="button" 
                                                class="btn btn-sm toggle-user-status ${u.status == 'active' ? 'btn-danger' : 'btn-success'}" 
                                                data-user-id="${u.userID}"
                                                data-current-status="${u.status}">
                                            <span class="btn-text">
                                                ${u.status == 'active' ? '<i class="bi bi-lock"></i> Khóa' : '<i class="bi bi-unlock"></i> Mở khóa'}
                                            </span>
                                            <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                                        </button>
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
<script>
document.addEventListener('DOMContentLoaded', function() {
    // Toggle user status with AJAX
    const toggleButtons = document.querySelectorAll('.toggle-user-status');
    
    toggleButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            const userId = btn.getAttribute('data-user-id');
            const currentStatus = btn.getAttribute('data-current-status');
            const row = btn.closest('tr');
            
            // Disable button and show loading
            btn.disabled = true;
            const btnText = btn.querySelector('.btn-text');
            const spinner = btn.querySelector('.spinner-border');
            btnText.classList.add('d-none');
            spinner.classList.remove('d-none');
            
            // Make AJAX request
            fetch('<c:url value="/admin/users"/>', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: 'action=toggle-status&id=' + userId
            })
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(data => {
                // Toggle status
                const newStatus = currentStatus === 'active' ? 'locked' : 'active';
                btn.setAttribute('data-current-status', newStatus);
                
                // Update button appearance
                if (newStatus === 'active') {
                    btn.classList.remove('btn-success');
                    btn.classList.add('btn-danger');
                    btnText.innerHTML = '<i class="bi bi-lock"></i> Khóa';
                } else {
                    btn.classList.remove('btn-danger');
                    btn.classList.add('btn-success');
                    btnText.innerHTML = '<i class="bi bi-unlock"></i> Mở khóa';
                }
                
                // Update status badge in the row
                const statusBadge = row.querySelector('td:nth-child(5) span');
                if (newStatus === 'active') {
                    statusBadge.classList.remove('bg-danger');
                    statusBadge.classList.add('bg-success');
                    statusBadge.textContent = 'Hoạt động';
                } else {
                    statusBadge.classList.remove('bg-success');
                    statusBadge.classList.add('bg-danger');
                    statusBadge.textContent = 'Đã khóa';
                }
                
                // Show success message
                showToast('success', 'Cập nhật trạng thái thành công!');
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('error', 'Có lỗi xảy ra. Vui lòng thử lại!');
            })
            .finally(() => {
                // Re-enable button and hide loading
                btn.disabled = false;
                btnText.classList.remove('d-none');
                spinner.classList.add('d-none');
            });
        });
    });
    
    // Toast notification function
    function showToast(type, message) {
        const bgClass = type === 'success' ? 'bg-success' : 'bg-danger';
        const icon = type === 'success' ? 'check-circle' : 'exclamation-circle';
        
        const toastContainer = document.createElement('div');
        toastContainer.className = 'position-fixed bottom-0 end-0 p-3';
        toastContainer.style.zIndex = '11';
        
        const toastDiv = document.createElement('div');
        toastDiv.className = 'toast align-items-center text-white border-0 ' + bgClass;
        toastDiv.setAttribute('role', 'alert');
        toastDiv.setAttribute('aria-live', 'assertive');
        toastDiv.setAttribute('aria-atomic', 'true');
        
        const flexDiv = document.createElement('div');
        flexDiv.className = 'd-flex';
        
        const bodyDiv = document.createElement('div');
        bodyDiv.className = 'toast-body';
        bodyDiv.innerHTML = '<i class="bi bi-' + icon + ' me-2"></i>' + message;
        
        const closeBtn = document.createElement('button');
        closeBtn.type = 'button';
        closeBtn.className = 'btn-close btn-close-white me-2 m-auto';
        closeBtn.setAttribute('data-bs-dismiss', 'toast');
        closeBtn.setAttribute('aria-label', 'Close');
        
        flexDiv.appendChild(bodyDiv);
        flexDiv.appendChild(closeBtn);
        toastDiv.appendChild(flexDiv);
        toastContainer.appendChild(toastDiv);
        document.body.appendChild(toastContainer);
        
        const toast = new bootstrap.Toast(toastDiv, { delay: 3000 });
        toast.show();
        
        // Remove toast element after it's hidden
        toastDiv.addEventListener('hidden.bs.toast', function() {
            toastContainer.remove();
        });
    }
});
</script>
</body>
</html>

