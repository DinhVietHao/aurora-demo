<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết Flash Sale - Aurora Admin</title>
    <jsp:include page="/WEB-INF/views/layouts/_head_admin.jsp" />
    <link rel="stylesheet" href="<c:url value='/assets/styles/book.css'/>">
</head>
<body class="sb-nav-fixed">
<jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

<div id="layoutSidenav">
    <jsp:include page="/WEB-INF/views/layouts/_sidebar_admin.jsp" />

    <div id="layoutSidenav_content">
        <main>
            <div class="container-fluid px-4">
                <div class="d-flex justify-content-between align-items-center">
                    <h1 class="mt-4 promotion-title">Chi tiết Flash sale: ${fs.name}</h1>
                    <div class="d-flex gap-2">
                       
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="<c:url value='/'/>">Trang chủ</a></li>
                                <li class="breadcrumb-item"><a href="<c:url value='/admin/flash-sales'/>">Flash sale</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Chi tiết</li>
                            </ol>
                        </nav>
                    </div>
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
               
                <!-- Flash Sale Info -->
                <div class="card mt-4 mb-3">
                    <div class="card-body">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <strong>ID:</strong> ${fs.flashSaleId}
                            </div>
                            <div class="col-md-3">
                                <strong>Trạng thái:</strong> 
                                <c:choose>
                                    <c:when test="${fs.status == 'ACTIVE'}">
                                        <span class="badge bg-success">Đang diễn ra</span>
                                    </c:when>
                                    <c:when test="${fs.status == 'SCHEDULED'}">
                                        <span class="badge bg-warning">Đã lên lịch</span>
                                    </c:when>
                                    <c:when test="${fs.status == 'ENDED'}">
                                        <span class="badge bg-secondary">Đã kết thúc</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">${fs.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="col-md-6">
                                <strong>Thời gian:</strong> 
                                <fmt:formatDate value="${fs.startAt}" pattern="dd/MM/yyyy HH:mm"/> 
                                → 
                                <fmt:formatDate value="${fs.endAt}" pattern="dd/MM/yyyy HH:mm"/>
                            </div>
                            <div class="col-12">
                                <strong>Tổng sản phẩm:</strong> ${total} sản phẩm
                                <small class="text-muted ms-3">Tạo lúc: <fmt:formatDate value="${fs.createdAt}" pattern="dd/MM/yyyy HH:mm"/></small>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Filter Form -->
                <form class="card mb-3" method="get">
                    <input type="hidden" name="id" value="${fs.flashSaleId}">
                    <div class="card-body">
                        <div class="row g-3 align-items-end">
                            <div class="col-md-3">
                                <label class="form-label">Tìm kiếm</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-search"></i></span>
                                    <input class="form-control" name="name" value="${name}" placeholder="Tên sản phẩm">
                                </div>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Nhà xuất bản</label>
                                <input class="form-control" name="publisher" value="${publisher}" placeholder="NXB">
                            </div>
                            
                            <div class="col-md-2">
                                <label class="form-label">Sắp xếp</label>
                                <select class="form-select" name="sort">
                                    <option value="" ${empty sort ? 'selected' : ''}>Mặc định</option>
                                    <option value="price_asc" ${sort == 'price_asc' ? 'selected' : ''}>Giá thấp → cao</option>
                                    <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>Giá cao → thấp</option>
                                    <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Tên A → Z</option>
                                    <option value="stock_desc" ${sort == 'stock_desc' ? 'selected' : ''}>Tồn kho cao</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Hiển thị</label>
                                <select class="form-select" name="pageSize">
                                    <option value="12" ${pageSize == 12 ? 'selected' : ''}>12 sản phẩm</option>
                                    <option value="24" ${pageSize == 24 ? 'selected' : ''}>24 sản phẩm</option>
                                    <option value="48" ${pageSize == 48 ? 'selected' : ''}>48 sản phẩm</option>
                                </select>
                            </div>
                            <div class="col-md-1">
                                <button class="btn btn-primary w-100" type="submit">
                                    <i class="bi bi-funnel"></i> Lọc
                                </button>
                            </div>
                            <div class="col-md-2">
                                <a href="<c:url value='/admin/flash-sales/approval'/>" class="btn btn-outline-primary">
                                    <i class="bi bi-check-circle"></i> Duyệt sản phẩm
                                </a>
                            </div>
                        </div>
                    </div>
                </form>

                <!-- Products Grid -->
                <div class="card">
                    <div class="card-body">
                        <div class="book-content-header mb-3">
                            <h4 class="book-content-title">Sản phẩm Flash Sale (${total} sản phẩm)</h4>
                        </div>

                        <c:choose>
                            <c:when test="${empty items}">
                                <div class="text-center py-5">
                                    <i class="bi bi-box-seam display-1 text-muted"></i>
                                    <h5 class="mt-3">Không tìm thấy sản phẩm nào</h5>
                                    <p class="text-muted">Thử điều chỉnh bộ lọc để tìm kiếm sản phẩm khác</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="row g-3 product">
                                    <c:forEach items="${items}" var="p" varStatus="loop">
                                        <div class="col-6 col-md-4 col-lg-3">
                                            <div class="product-card">
                                                <div class="product-img">
                                                    <span class="discount">
                                                        <fmt:formatNumber value="${((p.originalPrice.doubleValue() - p.flashPrice.doubleValue()) / p.originalPrice.doubleValue()) * 100}" 
                                                                        pattern="#,##0" maxFractionDigits="0"/>%
                                                    </span>
                                                    <img src="<c:url value='/assets/images/product-${(loop.index % 3) + 1}.png'/>" alt="${p.title}">
                                                </div>
                                                <div class="product-body">
                                                    <h6 class="price">
                                                        <fmt:formatNumber value="${p.flashPrice}" type="currency" currencyCode="VND" pattern="#,##0 ₫"/>
                                                    </h6>
                                                    <small class="author">${p.publisherName}</small>
                                                    <div class="mt-1">
                                                        <small class="text-muted"><i class="bi bi-shop me-1"></i>${p.shopName}</small>
                                                    </div>
                                                    <p class="title">${p.title}</p>
                                                    <div class="rating">
                                                        <i class="bi bi-star-fill text-warning small"></i>
                                                        <i class="bi bi-star-fill text-warning small"></i>
                                                        <i class="bi bi-star-fill text-warning small"></i>
                                                        <i class="bi bi-star-fill text-warning small"></i>
                                                        <i class="bi bi-star-half text-warning small"></i>
                                                        <span>Tồn: ${p.fsStock}</span>
                                                    </div>
                                                    <div class="mt-2">
                                                        <small class="text-muted">
                                                            Giá gốc: <span class="text-decoration-line-through">
                                                                <fmt:formatNumber value="${p.originalPrice}" type="currency" currencyCode="VND" pattern="#,##0 ₫"/>
                                                            </span>
                                                        </small>
                                                    </div>
                                                    <div class="mt-2 d-flex gap-1">
                                                        <a class="btn btn-sm btn-outline-primary" 
                                                           href="<c:url value='/admin/product/detail'><c:param name='id' value='${p.productId}'/></c:url>">
                                                            <i class="bi bi-eye"></i> Chi tiết
                                                        </a>
                                                        <form method="post" style="display: inline;" onsubmit="return confirmRemove()">
                                                            <input type="hidden" name="action" value="remove_product">
                                                            <input type="hidden" name="flashSaleId" value="${fs.flashSaleId}">
                                                            <input type="hidden" name="productId" value="${p.productId}">
                                                            <!-- Preserve filter parameters -->
                                                            <input type="hidden" name="name" value="${name}">
                                                            <input type="hidden" name="publisher" value="${publisher}">
                                                            <input type="hidden" name="price" value="${price}">
                                                            <input type="hidden" name="sort" value="${sort}">
                                                            <input type="hidden" name="page" value="${page}">
                                                            <input type="hidden" name="pageSize" value="${pageSize}">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                                <i class="bi bi-trash"></i> Xóa
                                                            </button>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>

                                <!-- Pagination -->
                                <c:set var="totalPages" value="${(total + pageSize - 1) / pageSize}"/>
                                <c:if test="${totalPages > 1}">
                                    <nav aria-label="pagination" class="mt-4">
                                        <ul class="pagination justify-content-center">
                                            <li class="page-item ${page==1?'disabled':''}">
                                                <a class="page-link" href="?id=${fs.flashSaleId}&name=${name}&publisher=${publisher}&price=${price}&sort=${sort}&page=${page-1}&pageSize=${pageSize}">‹</a>
                                            </li>
                                            <c:forEach var="pnum" begin="1" end="${totalPages}">
                                                <li class="page-item ${pnum==page?'active':''}">
                                                    <a class="page-link" href="?id=${fs.flashSaleId}&name=${name}&publisher=${publisher}&price=${price}&sort=${sort}&page=${pnum}&pageSize=${pageSize}">${pnum}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${page>=totalPages?'disabled':''}">
                                                <a class="page-link" href="?id=${fs.flashSaleId}&name=${name}&publisher=${publisher}&price=${price}&sort=${sort}&page=${page+1}&pageSize=${pageSize}">›</a>
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
function confirmRemove() {
    return confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi flash sale?');
}
</script>
</body>
</html>

