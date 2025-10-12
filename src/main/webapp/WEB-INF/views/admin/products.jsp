<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý sản phẩm - Aurora Admin</title>
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
                    <h1 class="mt-4">Quản lý sản phẩm</h1>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="<c:url value='/'/>">Trang chủ</a></li>
                            <li class="breadcrumb-item"><a href="<c:url value='/admin/dashboard'/>">Dashboard</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Sản phẩm</li>
                        </ol>
                    </nav>
                </div>

                <form class="card mt-4 mb-3" method="get">
                    <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div class="input-group" style="max-width: 360px;">
                            <span class="input-group-text"><i class="bi bi-search"></i></span>
                            <input class="form-control" type="text" name="q" value="${q}" placeholder="Tìm kiếm tên sản phẩm, tác giả...">
                        </div>
                        <div class="d-flex align-items-center gap-2">
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
                            <button class="btn btn-primary"><i class="bi bi-search me-1"></i>Tìm kiếm</button>
                        </div>
                    </div>
                </form>

                <div class="card">
                    <div class="card-body table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Ảnh</th>
                                <th>Tên sản phẩm</th>
                                <th>Nhà xuất bản</th>
                                <th>Giá gốc</th>
                                <th>Giá bán</th>
                                <th>Đã bán</th>
                                <th>Tồn kho</th>
                                <th>Shop</th>
                                <th>Hành động</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${products}" var="product">
                                <tr>
                                    <td>${product.productId}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty product.primaryImageUrl}">
                                                <img src="http://localhost:8080/assets/images/catalog/thumbnails/${product.primaryImageUrl}" 
                                                     alt="${product.title}" 
                                                     style="width:50px;height:50px;object-fit:cover;border-radius:4px;">
                                            </c:when>
                                            <c:otherwise>
                                                <div style="width:50px;height:50px;background:#ddd;border-radius:4px;display:flex;align-items:center;justify-content:center;">
                                                    <i class="bi bi-book"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <strong>${product.title}</strong>
                                        <c:if test="${not empty product.authors}">
                                            <div class="text-muted small">
                                                <i class="bi bi-person"></i>
                                                <c:forEach items="${product.authors}" var="author" varStatus="status">
                                                    ${author.authorName}<c:if test="${!status.last}">, </c:if>
                                                </c:forEach>
                                            </div>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty product.publisher}">
                                                <small>${product.publisher.publisherName}</small>
                                            </c:when>
                                            <c:otherwise>
                                                <small class="text-muted">-</small>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatNumber value="${product.originalPrice}" type="number" groupingUsed="true"/>đ
                                    </td>
                                    <td>
                                        <strong class="text-danger">
                                            <fmt:formatNumber value="${product.salePrice}" type="number" groupingUsed="true"/>đ
                                        </strong>
                                        <c:if test="${product.salePrice < product.originalPrice}">
                                            <div class="badge bg-danger">
                                                -<fmt:formatNumber value="${((product.originalPrice - product.salePrice) / product.originalPrice) * 100}" maxFractionDigits="0"/>%
                                            </div>
                                        </c:if>
                                    </td>
                                    <td>
                                        <span class="badge bg-success">${product.soldCount}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${product.stock > 50}">
                                                <span class="badge bg-success">${product.stock}</span>
                                            </c:when>
                                            <c:when test="${product.stock > 10}">
                                                <span class="badge bg-warning text-dark">${product.stock}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">${product.stock}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <small class="text-muted">ID: ${product.shopId}</small>
                                    </td>
                                    <td>
                                        <c:url var="detailUrl" value="/admin/products/detail">
                                            <c:param name="id" value="${product.productId}" />
                                        </c:url>
                                        <a class="btn btn-sm btn-outline-primary" href="${detailUrl}" title="Xem chi tiết">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty products}">
                                <tr>
                                    <td colspan="10" class="text-center text-muted py-4">
                                        <i class="bi bi-inbox" style="font-size: 2rem;"></i>
                                        <div class="mt-2">Không tìm thấy sản phẩm nào</div>
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
                                <a class="page-link" href="?q=${q}&page=${page-1}&pageSize=${pageSize}">«</a>
                            </li>
                            <c:forEach var="p" begin="1" end="${totalPages}">
                                <li class="page-item ${p==page?'active':''}">
                                    <a class="page-link" href="?q=${q}&page=${p}&pageSize=${pageSize}">${p}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${page>=totalPages?'disabled':''}">
                                <a class="page-link" href="?q=${q}&page=${page+1}&pageSize=${pageSize}">»</a>
                            </li>
                        </ul>
                    </nav>
                    <div class="text-muted text-center mb-4">
                        <c:choose>
                            <c:when test="${not empty q}">
                                Tìm thấy ${total} sản phẩm cho từ khóa "<strong>${q}</strong>". 
                                Hiển thị ${(page-1)*pageSize + 1} - ${page*pageSize > total ? total : page*pageSize}
                            </c:when>
                            <c:otherwise>
                                Hiển thị ${(page-1)*pageSize + 1} - ${page*pageSize > total ? total : page*pageSize} trong tổng số ${total} sản phẩm
                            </c:otherwise>
                        </c:choose>
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

