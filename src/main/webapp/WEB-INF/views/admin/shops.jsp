<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý cửa hàng - Aurora Admin</title>
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
                    <h1 class="mt-4">Quản lý cửa hàng</h1>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="<c:url value='/'/>">Trang chủ</a></li>
                            <li class="breadcrumb-item"><a href="<c:url value='/admin/dashboard'/>">Dashboard</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Cửa hàng</li>
                        </ol>
                    </nav>
                </div>

                <form class="card mt-4 mb-3" method="get">
                    <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div class="input-group" style="max-width: 360px;">
                            <span class="input-group-text"><i class="bi bi-search"></i></span>
                            <input class="form-control" type="text" name="q" value="${q}" placeholder="Tìm kiếm tên cửa hàng, email">
                        </div>
                        <div class="d-flex align-items-center gap-2">
                            <select class="form-select" name="status" style="min-width: 180px;">
                                <option value="">Tất cả trạng thái</option>
                                <c:forEach items="${statuses}" var="st">
                                    <c:choose>
                                        <c:when test="${st == status}"><option value="${st}" selected="selected">${st}</option></c:when>
                                        <c:otherwise><option value="${st}">${st}</option></c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                            <select class="form-select" name="pageSize" style="width: 120px;">
                                <c:choose>
                                    <c:when test="${pageSize==10}"><option value="10" selected="selected">10</option></c:when>
                                    <c:otherwise><option value="10">10</option></c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${pageSize==20}"><option value="20" selected="selected">20</option></c:when>
                                    <c:otherwise><option value="20">20</option></c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${pageSize==50}"><option value="50" selected="selected">50</option></c:when>
                                    <c:otherwise><option value="50">50</option></c:otherwise>
                                </c:choose>
                            </select>
                            <button class="btn btn-primary"><i class="bi bi-funnel me-1"></i>Lọc</button>
                        </div>
                    </div>
                </form>

                <div class="card">
                    <div class="card-body table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Logo</th>
                                <th>Tên cửa hàng</th>
                                <th>Chủ sở hữu</th>
                                <th>Email</th>
                                <th>Đánh giá</th>
                                <th>Sản phẩm</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th>Hành động</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${shops}" var="shop">
                                <tr>
                                    <td>${shop.shopId}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty shop.avatarUrl}">
                                                <img src="${shop.avatarUrl}" alt="Logo" style="width:40px;height:40px;object-fit:cover;border-radius:4px;">
                                            </c:when>
                                            <c:otherwise>
                                                <div style="width:40px;height:40px;background:#ddd;border-radius:4px;display:flex;align-items:center;justify-content:center;">
                                                    <i class="bi bi-shop"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <strong>${shop.name}</strong>
                                        <c:if test="${not empty shop.description}">
                                            <div class="text-muted small">${shop.description}</div>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:if test="${not empty shop.ownerName}">
                                            ${shop.ownerName}
                                            <div class="text-muted small">ID: ${shop.ownerUserId}</div>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty shop.ownerEmail}">
                                                <small>${shop.ownerEmail}</small>
                                            </c:when>
                                            <c:otherwise>
                                                <small class="text-muted">${shop.invoiceEmail}</small>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="badge bg-warning text-dark">
                                            <i class="bi bi-star-fill"></i> ${shop.ratingAvg}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge bg-info text-dark">${shop.productCount}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${shop.status == 'APPROVED'}">
                                                <span class="badge bg-success">${shop.status}</span>
                                            </c:when>
                                            <c:when test="${shop.status == 'PENDING'}">
                                                <span class="badge bg-warning text-dark">${shop.status}</span>
                                            </c:when>
                                            <c:when test="${shop.status == 'SUSPENDED'}">
                                                <span class="badge bg-danger">${shop.status}</span>
                                            </c:when>
                                            <c:when test="${shop.status == 'BANNED'}">
                                                <span class="badge bg-dark">${shop.status}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">${shop.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <small>${shop.createdAt}</small>
                                    </td>
                                    <td>
                                        <div class="d-flex gap-1">
                                            <c:url var="detailUrl" value="/admin/shops/detail">
                                                <c:param name="id" value="${shop.shopId}" />
                                            </c:url>
                                            <a class="btn btn-sm btn-outline-primary" href="${detailUrl}" title="Xem chi tiết">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                            
                                            <c:if test="${shop.status == 'PENDING'}">
                                                <form action="<c:url value='/admin/shops/approval'/>" method="post" style="display:inline;">
                                                    <input type="hidden" name="id" value="${shop.shopId}">
                                                    <input type="hidden" name="action" value="approve">
                                                    <button type="submit" class="btn btn-sm btn-success" title="Duyệt cửa hàng">
                                                        <i class="bi bi-check-circle"></i>
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty shops}">
                                <tr>
                                    <td colspan="10" class="text-center text-muted py-4">
                                        <i class="bi bi-inbox" style="font-size: 2rem;"></i>
                                        <div class="mt-2">Không tìm thấy cửa hàng nào</div>
                                    </td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>

                <c:if test="${total > 0}">
                    <c:set var="totalPages" value="${(total + pageSize - 1) / pageSize}"/>
                    <nav aria-label="pagination" class="mt-3">
                        <ul class="pagination">
                            <li class="page-item ${page==1?'disabled':''}">
                                <a class="page-link" href="?q=${q}&status=${status}&page=${page-1}&pageSize=${pageSize}">«</a>
                            </li>
                            <c:forEach var="p" begin="1" end="${totalPages}">
                                <li class="page-item ${p==page?'active':''}">
                                    <a class="page-link" href="?q=${q}&status=${status}&page=${p}&pageSize=${pageSize}">${p}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${page>=totalPages?'disabled':''}">
                                <a class="page-link" href="?q=${q}&status=${status}&page=${page+1}&pageSize=${pageSize}">»</a>
                            </li>
                        </ul>
                    </nav>
                    <div class="text-muted text-center mb-4">
                        Hiển thị ${(page-1)*pageSize + 1} - ${page*pageSize > total ? total : page*pageSize} trong tổng số ${total} cửa hàng
                    </div>
                </c:if>
            </div>
        </main>
        
        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
    </div>
</div>

<jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
<script src="<c:url value='/assets/js/adminDashboard.js'/>"></script>
</body>
</html>

