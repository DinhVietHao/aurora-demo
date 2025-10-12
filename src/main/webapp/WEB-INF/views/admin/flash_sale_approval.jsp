<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Duyệt sản phẩm Flash Sale - Aurora Admin</title>
    
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
                    <h1 class="mt-4">Danh sách sản phẩm đăng ký Flash Sale</h1>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="<c:url value='/'/>">Trang chủ</a></li>
                            <li class="breadcrumb-item"><a href="<c:url value='/admin/flash-sales'/>">Flash sale</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Duyệt sản phẩm</li>
                        </ol>
                    </nav>
                </div>

                <!-- Success Message -->
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success alert-dismissible fade show mt-4" role="alert">
                        <i class="bi bi-check-circle me-2"></i>${successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Error Message -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show mt-4" role="alert">
                        <i class="bi bi-exclamation-triangle me-2"></i>${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Filter Form -->
                <form class="card mb-3" method="get">
                    <div class="card-body">
                        <div class="row g-3 align-items-end">
                            <div class="col-md-3">
                                <label class="form-label">Trạng thái</label>
                                <select class="form-select" name="status">
                                    <option value="" ${empty status ? 'selected' : ''}>Tất cả</option>
                                    <c:forEach items="${approvalStatuses}" var="approvalStatus">
                                        <option value="${approvalStatus}" ${status == approvalStatus ? 'selected' : ''}>
                                            <c:choose>
                                                <c:when test="${approvalStatus == 'PENDING'}">Chờ duyệt</c:when>
                                                <c:when test="${approvalStatus == 'APPROVED'}">Đã duyệt</c:when>
                                                <c:when test="${approvalStatus == 'REJECTED'}">Đã từ chối</c:when>
                                                <c:otherwise>${approvalStatus}</c:otherwise>
                                            </c:choose>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Hiển thị</label>
                                <select class="form-select" name="pageSize">
                                    <option value="10" ${pageSize == 10 ? 'selected' : ''}>10 sản phẩm</option>
                                    <option value="25" ${pageSize == 25 ? 'selected' : ''}>25 sản phẩm</option>
                                    <option value="50" ${pageSize == 50 ? 'selected' : ''}>50 sản phẩm</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <button class="btn btn-primary w-100" type="submit">
                                    <i class="bi bi-funnel"></i> Lọc
                                </button>
                            </div>
                        </div>
                    </div>
                </form>

                <!-- Products Table -->
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h5 class="card-title">Danh sách sản phẩm (${total} sản phẩm)</h5>
                        </div>

                        <c:choose>
                            <c:when test="${empty items}">
                                <div class="text-center py-5">
                                    <i class="bi bi-box-seam display-1 text-muted"></i>
                                    <h5 class="mt-3">Không có sản phẩm nào</h5>
                                    <p class="text-muted">Thử điều chỉnh bộ lọc để tìm kiếm sản phẩm khác</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle">
                                        <thead class="table-light">
                                            <tr>
                                                <th>ID</th>
                                                <th>Tên sản phẩm</th>
                                                <th>Shop</th>
                                                <th>Giá Flash</th>
                                                <th>Tồn kho</th>
                                                <th>Giá gốc</th>
                                                <th>Trạng thái</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${items}" var="item">
                                                <tr>
                                                    <td>${item.flashSaleItemId}</td>
                                                    <td>
                                                        <div>
                                                            <strong>${item.title}</strong>
                                                            <br>
                                                            <small class="text-muted">${item.publisherName}</small>
                                                        </div>
                                                    </td>
                                                    <td>${item.shopName}</td>
                                                    <td>
                                                        <strong class="text-danger">
                                                            <fmt:formatNumber value="${item.flashPrice}" type="currency" currencyCode="VND" pattern="#,##0 ₫"/>
                                                        </strong>
                                                    </td>
                                                    <td>${item.fsStock}</td>
                                                    <td>
                                                        <span class="text-decoration-line-through">
                                                            <fmt:formatNumber value="${item.originalPrice}" type="currency" currencyCode="VND" pattern="#,##0 ₫"/>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${item.approvalStatus == 'PENDING'}">
                                                                <span class="badge bg-warning">Chờ duyệt</span>
                                                            </c:when>
                                                            <c:when test="${item.approvalStatus == 'APPROVED'}">
                                                                <span class="badge bg-success">Đã duyệt</span>
                                                            </c:when>
                                                            <c:when test="${item.approvalStatus == 'REJECTED'}">
                                                                <span class="badge bg-danger">Đã từ chối</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">${item.approvalStatus}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group" role="group">
                                                            <c:if test="${item.approvalStatus == 'PENDING'}">
                                                                <form method="post" style="display: inline;" onsubmit="return confirmApprove()">
                                                                    <input type="hidden" name="action" value="approve">
                                                                    <input type="hidden" name="id" value="${item.flashSaleItemId}">
                                                                    <input type="hidden" name="status" value="${status}">
                                                                    <input type="hidden" name="page" value="${page}">
                                                                    <input type="hidden" name="pageSize" value="${pageSize}">
                                                                    <button type="submit" class="btn btn-sm btn-success" title="Duyệt">
                                                                        <i class="bi bi-check-lg"></i>
                                                                    </button>
                                                                </form>
                                                                <form method="post" style="display: inline;" onsubmit="return confirmReject()">
                                                                    <input type="hidden" name="action" value="reject">
                                                                    <input type="hidden" name="id" value="${item.flashSaleItemId}">
                                                                    <input type="hidden" name="status" value="${status}">
                                                                    <input type="hidden" name="page" value="${page}">
                                                                    <input type="hidden" name="pageSize" value="${pageSize}">
                                                                    <button type="submit" class="btn btn-sm btn-danger" title="Từ chối">
                                                                        <i class="bi bi-x-lg"></i>
                                                                    </button>
                                                                </form>
                                                            </c:if>
                                                            <a class="btn btn-sm btn-outline-primary" 
                                                               href="<c:url value='/admin/product/detail'><c:param name='id' value='${item.productId}'/></c:url>" 
                                                               title="Xem chi tiết">
                                                                <i class="bi bi-eye"></i>
                                                            </a>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- Pagination -->
                                <c:set var="totalPages" value="${(total + pageSize - 1) / pageSize}"/>
                                <c:if test="${totalPages > 1}">
                                    <nav aria-label="pagination" class="mt-4">
                                        <ul class="pagination justify-content-center">
                                            <li class="page-item ${page==1?'disabled':''}">
                                                <a class="page-link" href="?status=${status}&page=${page-1}&pageSize=${pageSize}">‹</a>
                                            </li>
                                            <c:forEach var="pnum" begin="1" end="${totalPages}">
                                                <li class="page-item ${pnum==page?'active':''}">
                                                    <a class="page-link" href="?status=${status}&page=${pnum}&pageSize=${pageSize}">${pnum}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${page>=totalPages?'disabled':''}">
                                                <a class="page-link" href="?status=${status}&page=${page+1}&pageSize=${pageSize}">›</a>
                                            </li>
                                        </ul>
                                    </nav>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </main>
        
        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
    </div>
</div>

<jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
<script src="<c:url value='/assets/js/adminDashboard.js'/>"></script>
<script>
function confirmApprove() {
    return confirm('Bạn có chắc chắn muốn duyệt sản phẩm này?');
}

function confirmReject() {
    return confirm('Bạn có chắc chắn muốn từ chối sản phẩm này?');
}
</script>
</body>
</html>
