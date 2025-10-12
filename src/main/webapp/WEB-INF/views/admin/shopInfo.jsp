<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý shop - Aurora Admin</title>
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
                    <h1 class="mt-4">Chi tiết cửa hàng</h1>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="<c:url value='/'/>">Trang chủ</a></li>
                            <li class="breadcrumb-item"><a href="<c:url value='/admin/dashboard'/>">Dashboard</a></li>
                            <li class="breadcrumb-item"><a href="<c:url value='/admin/shops'/>">Cửa hàng</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Chi tiết</li>
                        </ol>
                    </nav>
                </div>

                <div class="mt-3">
                    <a class="btn btn-outline-secondary" href="<c:url value='/admin/shops'/>">
                        <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách
                    </a>
                </div>

                <div class="card mt-4 mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-info-circle me-1"></i>Thông tin cửa hàng</span>
                        <span class="badge bg-primary">ID: ${shop.shopId}</span>
                    </div>
                    <div class="card-body">
                        <form id="shopInfoForm" method="post" action="<c:url value='/admin/shops/detail'/>?action=update&id=${shop.shopId}" enctype="multipart/form-data">
                            <div class="row g-3">
                                <div class="col-md-12 mb-3">
                                    <div class="text-center">
                                        <c:choose>
                                            <c:when test="${not empty shop.avatarUrl}">
                                                <img id="shopLogoPreview" src="${shop.avatarUrl}" alt="Logo" class="img-thumbnail" style="max-width:150px;max-height:150px;object-fit:cover;">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="img-thumbnail d-inline-flex align-items-center justify-content-center" style="width:150px;height:150px;background:#f0f0f0;">
                                                    <i class="bi bi-shop" style="font-size:3rem;color:#999;"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                         <div class="mt-2">
                                             <span class="badge bg-warning text-dark me-2">
                                                 <i class="bi bi-star-fill"></i> Đánh giá: ${shop.ratingAvg}
                                             </span>
                                             <span class="badge bg-info text-dark">
                                                 <i class="bi bi-box-seam"></i> ${shop.productCount} sản phẩm
                                             </span>
                                         </div>
                                         <div class="mt-3">
                                             <label for="shopAvatarInput" class="btn btn-sm btn-outline-primary">
                                                 <i class="bi bi-upload me-1"></i>Tải lên logo mới
                                             </label>
                                             <input type="file" class="d-none" id="shopAvatarInput" name="avatar" accept="image/*">
                                             <div class="mt-2">
                                                 <small class="text-muted">Định dạng: JPG, PNG, GIF. Kích thước tối đa: 5MB</small>
                                             </div>
                                         </div>
                                     </div>
                                 </div>
                                <div class="col-md-8">
                                    <label class="form-label">Tên cửa hàng</label>
                                    <input type="text" class="form-control" id="shopName" name="name" required value="<c:out value='${shop.name}'/>">
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Trạng thái</label>
                                    <select class="form-select" name="status">
                                        <c:forEach items="${shopStatuses}" var="st">
                                            <option value="${st}" ${st == shop.status ? 'selected' : ''}>${st}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-8">
                                    <label class="form-label">Mô tả</label>
                                    <textarea class="form-control" name="description" rows="3"><c:out value='${shop.description}'/></textarea>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Email hóa đơn</label>
                                    <input type="email" class="form-control" name="invoiceEmail" value="<c:out value='${shop.invoiceEmail}'/>">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Chủ sở hữu</label>
                                    <input type="text" class="form-control" value="<c:out value='${shop.ownerName}'/> (ID: ${shop.ownerUserId})" readonly>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Ngày tạo</label>
                                    <input type="text" class="form-control" value="${shop.createdAt}" readonly>
                                </div>

                                <div class="col-12">
                                    <button type="submit" class="btn btn-success">
                                        <i class="bi bi-save me-1"></i>Lưu thay đổi
                                    </button>
                                    <button type="button" class="btn btn-outline-secondary" onclick="window.location.reload()">
                                        <i class="bi bi-arrow-clockwise me-1"></i>Hủy
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="card mb-5">
                    <div class="card-header"><i class="bi bi-geo-alt me-1"></i>Địa chỉ lấy hàng</div>
                    <div class="card-body">
                        <div class="row g-3">
                            <div class="col-md-4"><label class="form-label">Người nhận</label><input class="form-control" value="<c:out value='${pickup.recipientName}'/>" readonly></div>
                            <div class="col-md-4"><label class="form-label">Điện thoại</label><input class="form-control" value="<c:out value='${pickup.phone}'/>" readonly></div>
                            <div class="col-md-4"><label class="form-label">Mã bưu chính</label><input class="form-control" value="<c:out value='${pickup.postalCode}'/>" readonly></div>
                            <div class="col-12"><label class="form-label">Địa chỉ</label><input class="form-control" value="<c:out value='${pickup.line}'/>, <c:out value='${pickup.ward}'/>, <c:out value='${pickup.district}'/>, <c:out value='${pickup.city}'/>" readonly></div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        
        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
    </div>
</div>

<jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
<script src="<c:url value='/assets/js/shopInfo.js'/>"></script>
</body>
</html>


