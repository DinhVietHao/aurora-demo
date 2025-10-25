<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Duyệt sản phẩm - Aurora Admin</title>
    <jsp:include page="/WEB-INF/views/layouts/_head_admin.jsp" />
    <style>
        .product-image {
            width: 80px;
            height: 80px;
            object-fit: contain;
        }
        .badge-pending {
            background-color: #ffc107;
            color: #212529;
        }
        .badge-rejected {
            background-color: #dc3545;
            color: #fff;
        }
        .badge-active {
            background-color: #28a745;
            color: #fff;
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
                <div class="d-flex justify-content-between align-items-center">
                    <h1 class="mt-4">Duyệt sản phẩm</h1>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="<c:url value='/'/>">Trang chủ</a></li>
                            <li class="breadcrumb-item"><a href="<c:url value='/admin/dashboard'/>">Dashboard</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Duyệt sản phẩm</li>
                        </ol>
                    </nav>
                </div>
                
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <div class="card mb-4">
                    <div class="card-header">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <i class="bi bi-list-check me-1"></i>
                                Danh sách sản phẩm chờ duyệt
                            </div>
                            <div>
                                <form method="get" action="<c:url value='/admin/products/approval'/>" class="d-flex">
                                    <select name="status" class="form-select form-select-sm me-2" style="width: auto">
                                        <c:forEach items="${statuses}" var="s">
                                            <option value="${s}" ${s eq status ? 'selected' : ''}>${s}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="submit" class="btn btn-sm btn-primary">Lọc</button>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Hình ảnh</th>
                                        <th>Sản phẩm</th>
                                        <th>Thông tin</th>
                                        <th>Shop</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty products}">
                                            <tr>
                                                <td colspan="7" class="text-center py-4">
                                                    <div class="text-muted mb-2">Không có sản phẩm nào ${status eq 'PENDING' ? 'chờ duyệt' : (status eq 'REJECTED' ? 'bị từ chối' : '')}</div>
                                                    <a href="<c:url value='/admin/products'/>" class="btn btn-sm btn-outline-primary">
                                                        <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách sản phẩm
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach items="${products}" var="product">
                                                <tr>
                                                    <td>${product.productId}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty product.primaryImageUrl}">
                                                                <img src="<c:url value='/assets/images/catalog/thumbnails/${product.primaryImageUrl}'/>" 
                                                                     alt="${product.title}" class="product-image border">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="product-image border d-flex align-items-center justify-content-center bg-light">
                                                                    <i class="bi bi-image text-muted" style="font-size: 1.5rem"></i>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="fw-bold">${product.title}</div>
                                                        <div class="text-muted small">ISBN: ${product.bookDetail.ISBN}</div>
                                                        <c:if test="${not empty product.authors}">
                                                            <div class="text-muted small">
                                                                <i class="bi bi-person-lines-fill me-1"></i>
                                                                <c:forEach items="${product.authors}" var="author" varStatus="status">
                                                                    ${author.authorName}${!status.last ? ', ' : ''}
                                                                </c:forEach>
                                                            </div>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <div class="text-danger fw-bold">
                                                            <fmt:formatNumber value="${product.salePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                        </div>
                                                        <c:if test="${product.originalPrice > product.salePrice}">
                                                            <div class="text-muted small text-decoration-line-through">
                                                                <fmt:formatNumber value="${product.originalPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                            </div>
                                                        </c:if>
                                                        <div class="text-muted small">
                                                            <span class="me-2"><i class="bi bi-boxes me-1"></i>${product.quantity}</span>
                                                            <span><i class="bi bi-bag-check me-1"></i>${product.soldCount}</span>
                                                        </div>
                                                    </td>
                                                    <td>${product.shopId}</td>
                                                    <td>
                                                        <span class="badge badge-${product.status.toLowerCase()}">${product.status}</span>
                                                        <c:if test="${not empty product.rejectReason}">
                                                            <div class="text-danger small mt-1">${product.rejectReason}</div>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <div class="d-flex flex-column gap-1">
                                                            <a href="<c:url value='/admin/products/detail?id=${product.productId}'/>" class="btn btn-sm btn-outline-primary">
                                                                <i class="bi bi-eye"></i> Chi tiết
                                                            </a>
                                                            
                                                            <c:if test="${product.status eq 'PENDING'}">
                                                                <form method="post" action="<c:url value='/admin/products/approval'/>">
                                                                    <input type="hidden" name="id" value="${product.productId}">
                                                                    <input type="hidden" name="action" value="approve">
                                                                    <input type="hidden" name="status" value="${status}">
                                                                    <input type="hidden" name="page" value="${page}">
                                                                    <input type="hidden" name="pageSize" value="${pageSize}">
                                                                    <button type="submit" class="btn btn-sm btn-success w-100">
                                                                        <i class="bi bi-check-circle"></i> Duyệt
                                                                    </button>
                                                                </form>
                                                                
                                                                <button type="button" class="btn btn-sm btn-outline-danger" data-bs-toggle="modal"
                                                                    data-bs-target="#rejectModal${product.productId}">
                                                                    <i class="bi bi-x-circle"></i> Từ chối
                                                                </button>
                                                                
                                                                <!-- Reject Modal -->
                                                                <div class="modal fade" id="rejectModal${product.productId}" tabindex="-1" aria-hidden="true">
                                                                    <div class="modal-dialog">
                                                                        <div class="modal-content">
                                                                            <form method="post" action="<c:url value='/admin/products/approval'/>">
                                                                                <div class="modal-header">
                                                                                    <h5 class="modal-title">Từ chối sản phẩm</h5>
                                                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                                                </div>
                                                                                <div class="modal-body">
                                                                                    <p>Vui lòng cung cấp lý do từ chối sản phẩm <strong>${product.title}</strong>:</p>
                                                                                    <input type="hidden" name="id" value="${product.productId}">
                                                                                    <input type="hidden" name="action" value="reject">
                                                                                    <input type="hidden" name="status" value="${status}">
                                                                                    <input type="hidden" name="page" value="${page}">
                                                                                    <input type="hidden" name="pageSize" value="${pageSize}">
                                                                                    <div class="mb-3">
                                                                                        <textarea name="rejectReason" class="form-control" rows="3" required></textarea>
                                                                                    </div>
                                                                                </div>
                                                                                <div class="modal-footer">
                                                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                                                                    <button type="submit" class="btn btn-danger">Từ chối sản phẩm</button>
                                                                                </div>
                                                                            </form>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                        
                        <!-- Pagination -->
                        <c:if test="${total > 0}">
                            <div class="d-flex justify-content-between align-items-center mt-4">
                                <div>
                                    Hiển thị ${(page - 1) * pageSize + 1} đến ${(page * pageSize < total) ? page * pageSize : total} 
                                    trong tổng số ${total} sản phẩm
                                </div>
                                <nav>
                                    <ul class="pagination mb-0">
                                        <fmt:parseNumber var="totalPages" value="${(total / pageSize) + (total % pageSize > 0 ? 1 : 0)}" integerOnly="true" />
                                        
                                        <li class="page-item ${page <= 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="<c:url value='/admin/products/approval?status=${status}&page=${page-1}&pageSize=${pageSize}'/>">&laquo;</a>
                                        </li>
                                        
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <c:choose>
                                                <c:when test="${page == i}">
                                                    <li class="page-item active">
                                                        <span class="page-link">${i}</span>
                                                    </li>
                                                </c:when>
                                                <c:otherwise>
                                                    <li class="page-item">
                                                        <a class="page-link" href="<c:url value='/admin/products/approval?status=${status}&page=${i}&pageSize=${pageSize}'/>">${i}</a>
                                                    </li>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                        
                                        <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="<c:url value='/admin/products/approval?status=${status}&page=${page+1}&pageSize=${pageSize}'/>">&raquo;</a>
                                        </li>
                                    </ul>
                                </nav>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </main>
        
        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
    </div>
</div>

<jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

<script>
    // Auto-dismiss alerts after 5 seconds
    document.addEventListener('DOMContentLoaded', function() {
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    });
</script>

</body>
</html>
