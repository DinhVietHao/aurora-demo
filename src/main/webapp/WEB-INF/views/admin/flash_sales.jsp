<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Flash Sales - Aurora Admin</title>
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
                    <h1 class="mt-4 promotion-title">Quản lý Flash sale</h1>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="<c:url value='/'/>">Trang chủ</a></li>
                            <li class="breadcrumb-item"><a href="<c:url value='/admin/dashboard'/>">Dashboard</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Flash sale</li>
                        </ol>
                    </nav>
                </div>
                <div class="mt-3">
                    <a class="btn btn-success" href="<c:url value='/admin/flash-sales/create'/>"><i class="bi bi-plus-circle me-1"></i>Tạo flash sale</a>
                </div>

                <form class="card mt-4 mb-3" method="get">
                    <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div class="input-group" style="max-width: 360px;">
                            <span class="input-group-text"><i class="bi bi-search"></i></span>
                            <input class="form-control" type="text" name="q" value="${q}" placeholder="Tìm kiếm tên chương trình">
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
                                <th>Tên</th>
                                <th>Thời gian</th>
                                <th>Trạng thái</th>
                                <th>Tạo lúc</th>
                                <th>Hành động</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${items}" var="it">
                                <tr>
                                    <td>${it.flashSaleId}</td>
                                    <td>${it.name}</td>
                                    <td>
                                        <small>${it.startAt}</small>
                                        <div class="text-muted">→ ${it.endAt}</div>
                                    </td>
                                    <td><span class="badge bg-secondary">${it.status}</span></td>
                                    <td>${it.createdAt}</td>
                                    <td>
                                        <c:url var="detailUrl" value="/admin/flash-sales/detail">
                                            <c:param name="id" value="${it.flashSaleId}" />
                                        </c:url>
                                        <c:url var="editUrl" value="/admin/flash-sales/edit">
                                            <c:param name="id" value="${it.flashSaleId}" />
                                        </c:url>
                                        <c:url var="createUrl" value="/admin/flash-sales/create" />
                                        <a class="btn btn-sm btn-outline-secondary" href="${detailUrl}" title="Xem chi tiết">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                        <a class="btn btn-sm btn-primary ms-1" href="${editUrl}" title="Cập nhật">
                                            <i class="bi bi-pencil-square"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

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
            </div>
        </main>
        
        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
    </div>
</div>

<jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
<script src="<c:url value='/assets/js/adminDashboard.js'/>"></script>
</body>
</html>
