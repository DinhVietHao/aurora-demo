<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết sản phẩm - Aurora Admin</title>
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
                    <h1 class="mt-4">Chi tiết sản phẩm</h1>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="<c:url value='/'/>">Trang chủ</a></li>
                            <li class="breadcrumb-item"><a href="<c:url value='/admin/dashboard'/>">Dashboard</a></li>
                            <li class="breadcrumb-item"><a href="<c:url value='/admin/products'/>">Sản phẩm</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Chi tiết</li>
                        </ol>
                    </nav>
                </div>

                <div class="mt-3">
                    <a class="btn btn-outline-secondary" href="<c:url value='/admin/products'/>">
                        <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách
                    </a>
                </div>

                <!-- Product Information Card -->
                <div class="card mt-4 mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-info-circle me-1"></i>Thông tin sản phẩm</span>
                        <span class="badge bg-primary">ID: ${product.productId}</span>
                    </div>
                    <div class="card-body">
                        <div class="row g-4">
                            <!-- Product Images -->
                            <div class="col-md-4">
                                <div class="text-center">
                                    <c:choose>
                                        <c:when test="${not empty product.images && product.images.size() > 0}">
                                            <img src="http://localhost:8080/assets/images/catalog/thumbnails/${product.images[0].imageUrl}" 
                                                 alt="${product.title}" 
                                                 class="img-fluid img-thumbnail mb-3" 
                                                 style="max-height:300px;object-fit:contain;">
                                            <div class="row g-2">
                                                <c:forEach items="${product.images}" var="img" begin="0" end="3">
                                                    <div class="col-3">
                                                        <img src="http://localhost:8080/assets/images/catalog/thumbnails/${img.imageUrl}" 
                                                             alt="${product.title}" 
                                                             class="img-thumbnail" 
                                                             style="width:100%;height:60px;object-fit:cover;">
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="img-thumbnail d-inline-flex align-items-center justify-content-center" 
                                                 style="width:200px;height:300px;background:#f0f0f0;">
                                                <i class="bi bi-book" style="font-size:4rem;color:#999;"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <!-- Product Details -->
                            <div class="col-md-8">
                                <div class="row g-3">
                                    <div class="col-12">
                                        <label class="form-label fw-bold">Tên sản phẩm</label>
                                        <input type="text" class="form-control" value="<c:out value='${product.title}'/>" readonly>
                                    </div>

                                    <div class="col-md-12">
                                        <label class="form-label fw-bold">Mô tả</label>
                                        <textarea class="form-control" rows="3" readonly><c:out value='${product.description}'/></textarea>
                                    </div>

                                    <div class="col-md-3">
                                        <label class="form-label fw-bold">Giá gốc</label>
                                        <div class="input-group">
                                            <input type="text" class="form-control" 
                                                   value="<fmt:formatNumber value='${product.originalPrice}' type='number' groupingUsed='true'/>" 
                                                   readonly>
                                            <span class="input-group-text">đ</span>
                                        </div>
                                    </div>

                                    <div class="col-md-3">
                                        <label class="form-label fw-bold">Giá bán</label>
                                        <div class="input-group">
                                            <input type="text" class="form-control text-danger fw-bold" 
                                                   value="<fmt:formatNumber value='${product.salePrice}' type='number' groupingUsed='true'/>" 
                                                   readonly>
                                            <span class="input-group-text">đ</span>
                                        </div>
                                        <c:if test="${product.salePrice < product.originalPrice}">
                                            <small class="text-danger">
                                                Giảm <fmt:formatNumber value="${((product.originalPrice - product.salePrice) / product.originalPrice) * 100}" maxFractionDigits="0"/>%
                                            </small>
                                        </c:if>
                                    </div>

                                    <div class="col-md-3">
                                        <label class="form-label fw-bold">Đã bán</label>
                                        <input type="text" class="form-control" value="${product.soldCount}" readonly>
                                    </div>

                                    <div class="col-md-3">
                                        <label class="form-label fw-bold">Tồn kho</label>
                                        <input type="text" class="form-control ${product.stock <= 10 ? 'text-danger' : ''}" 
                                               value="${product.stock}" readonly>
                                    </div>

                                    <div class="col-md-4">
                                        <label class="form-label fw-bold">Shop ID</label>
                                        <input type="text" class="form-control" value="${product.shopId}" readonly>
                                    </div>

                                    <div class="col-md-4">
                                        <label class="form-label fw-bold">Category ID</label>
                                        <input type="text" class="form-control" value="${product.categoryId}" readonly>
                                    </div>

                                    <div class="col-md-4">
                                        <label class="form-label fw-bold">Loại sản phẩm</label>
                                        <input type="text" class="form-control" 
                                               value="${product.isBundle ? 'Bundle' : 'Đơn lẻ'}" readonly>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-bold">Nhà xuất bản</label>
                                        <input type="text" class="form-control" 
                                               value="<c:out value='${product.publisherString}'/>" readonly>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-bold">Ngày xuất bản</label>
                                        <input type="text" class="form-control" value="${product.publishedDate}" readonly>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Book Details Card -->
                <c:if test="${not empty product.bookDetail}">
                    <div class="card mb-4">
                        <div class="card-header"><i class="bi bi-book me-1"></i>Thông tin sách</div>
                        <div class="card-body">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Tác giả</label>
                                    <input type="text" class="form-control" 
                                           value="<c:out value='${product.bookDetail.author}'/>" readonly>
                                </div>

                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Người dịch</label>
                                    <input type="text" class="form-control" 
                                           value="<c:out value='${product.bookDetail.translator}'/>" readonly>
                                </div>

                                <div class="col-md-3">
                                    <label class="form-label fw-bold">Phiên bản</label>
                                    <input type="text" class="form-control" 
                                           value="<c:out value='${product.bookDetail.version}'/>" readonly>
                                </div>

                                <div class="col-md-3">
                                    <label class="form-label fw-bold">Loại bìa</label>
                                    <input type="text" class="form-control" 
                                           value="<c:out value='${product.bookDetail.coverType}'/>" readonly>
                                </div>

                                <div class="col-md-3">
                                    <label class="form-label fw-bold">Số trang</label>
                                    <input type="text" class="form-control" value="${product.bookDetail.pages}" readonly>
                                </div>

                                <div class="col-md-3">
                                    <label class="form-label fw-bold">Kích thước</label>
                                    <input type="text" class="form-control" 
                                           value="<c:out value='${product.bookDetail.size}'/>" readonly>
                                </div>

                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Ngôn ngữ</label>
                                    <input type="text" class="form-control" 
                                           value="<c:out value='${product.bookDetail.language}'/>" readonly>
                                </div>

                                <div class="col-md-6">
                                    <label class="form-label fw-bold">ISBN</label>
                                    <input type="text" class="form-control" 
                                           value="<c:out value='${product.bookDetail.ISBN}'/>" readonly>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- Actions -->
                <div class="mb-5">
                    <a class="btn btn-outline-primary" href="<c:url value='/book/${product.productId}'/>">
                        <i class="bi bi-eye me-1"></i>Xem trang sản phẩm
                    </a>
                    <a class="btn btn-outline-secondary" href="<c:url value='/admin/products'/>">
                        <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách
                    </a>
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

