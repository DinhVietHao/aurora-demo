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

                <!-- Display messages -->
                <c:if test="${not empty sessionScope.message}">
                    <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show mt-3" role="alert">
                        ${sessionScope.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <% session.removeAttribute("message"); %>
                    <% session.removeAttribute("messageType"); %>
                </c:if>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                        ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <div class="card mt-4 mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-info-circle me-1"></i>Thông tin cửa hàng</span>
                        <span class="badge bg-primary">ID: ${shop.shopId}</span>
                    </div>
                    <div class="card-body">
                        <div class="row g-3">
                            <div class="col-md-12 mb-3">
                                <div class="text-center">
                                    <c:choose>
                                        <c:when test="${not empty shop.avatarUrl}">
                                            <img src="${shop.avatarUrl}" alt="Logo" class="img-thumbnail" style="max-width:150px;max-height:150px;object-fit:cover;">
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
                                 </div>
                             </div>
                            <div class="col-md-8">
                                <label class="form-label">Tên cửa hàng</label>
                                <input type="text" class="form-control" value="<c:out value='${shop.name}'/>" readonly>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Trạng thái</label>
                                <form id="statusForm" method="post" action="<c:url value='/admin/shops/detail'/>?action=update&id=${shop.shopId}">
                                    <select class="form-select" name="status" onchange="document.getElementById('statusForm').submit();">
                                        <c:forEach items="${shopStatuses}" var="st">
                                            <option value="${st}" ${st == shop.status ? 'selected' : ''}>${st}</option>
                                        </c:forEach>
                                    </select>
                                </form>
                            </div>
                            <div class="col-md-12">
                                <label class="form-label">Mô tả</label>
                                <textarea class="form-control" rows="3" readonly><c:out value='${shop.description}'/></textarea>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Lý do từ chối (nếu có)</label>
                                <textarea class="form-control" rows="2" readonly><c:out value='${shop.rejectReason}'/></textarea>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Email hóa đơn</label>
                                <input type="email" class="form-control" value="<c:out value='${shop.invoiceEmail}'/>" readonly>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Chủ sở hữu</label>
                                <input type="text" class="form-control" value="<c:out value='${shop.ownerName}'/> (ID: ${shop.ownerUserId})" readonly>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Ngày tạo</label>
                                <input type="text" class="form-control" value="${shop.createdAt}" readonly>
                            </div>
                        </div>
                        
                        <!-- Shop Approval Actions Card -->
                        <div class="card mt-4">
                            <div class="card-header bg-light">
                                <strong><i class="bi bi-shield-check me-1"></i>Duyệt Shop</strong>
                            </div>
                            <div class="card-body">
                                <h5 class="card-title">Trạng thái hiện tại: 
                                    <c:choose>
                                        <c:when test="${shop.status == 'Đã duyệt' || shop.status == 'Đang hoạt động'}">
                                            <span class="badge bg-success">${shop.status}</span>
                                        </c:when>
                                        <c:when test="${shop.status == 'Chờ duyệt'}">
                                            <span class="badge bg-warning text-dark">${shop.status}</span>
                                        </c:when>
                                        <c:when test="${shop.status == 'Đã từ chối'}">
                                            <span class="badge bg-danger">${shop.status}</span>
                                        </c:when>
                                    </c:choose>
                                </h5>
                                
                                <div class="row mt-3">
                                    <!-- Action Buttons -->
                                    <div class="col-md-12 d-flex gap-2 mb-3 flex-wrap">
                                        <c:if test="${shop.status == 'Chờ duyệt' || shop.status == 'Đã từ chối'}">
                                            <form action="<c:url value='/admin/shops/approval'/>" method="post" style="display:inline;">
                                                <input type="hidden" name="id" value="${shop.shopId}">
                                                <input type="hidden" name="action" value="approve">
                                                <button type="submit" class="btn btn-success">
                                                    <i class="bi bi-check-circle me-1"></i>Duyệt cửa hàng
                                                </button>
                                            </form>
                                        </c:if>
                                        
                                        <c:if test="${shop.status == 'Chờ duyệt'}">
                                            <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#rejectModal">
                                                <i class="bi bi-x-circle me-1"></i>Từ chối
                                            </button>
                                        </c:if>
                                    </div>
                                    
                                    <!-- Reason display if rejected -->
                                    <c:if test="${not empty shop.rejectReason && shop.status == 'Đã từ chối'}">
                                        <div class="col-12">
                                            <div class="alert alert-secondary">
                                                <h6>Lý do:</h6>
                                                <p class="mb-0">${shop.rejectReason}</p>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Reject Modal -->
                <div class="modal fade" id="rejectModal" tabindex="-1" aria-labelledby="rejectModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <form action="<c:url value='/admin/shops/approval'/>" method="post">
                                <input type="hidden" name="id" value="${shop.shopId}">
                                <input type="hidden" name="action" value="reject">
                                
                                <div class="modal-header">
                                    <h5 class="modal-title" id="rejectModalLabel">Từ chối cửa hàng</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label for="rejectReason" class="form-label">Lý do từ chối</label>
                                        <textarea class="form-control" id="rejectReason" name="rejectReason" rows="4" required 
                                                  placeholder="Nhập lý do từ chối cửa hàng này..."></textarea>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                    <button type="submit" class="btn btn-danger">Xác nhận từ chối</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="card mb-5">
                    <div class="card-header"><i class="bi bi-geo-alt me-1"></i>Địa chỉ lấy hàng</div>
                    <div class="card-body">
                        <div class="row g-3">
                            <div class="col-md-6"><label class="form-label">Người nhận</label><input class="form-control" value="<c:out value='${pickup.recipientName}'/>" readonly></div>
                            <div class="col-md-6"><label class="form-label">Điện thoại</label><input class="form-control" value="<c:out value='${pickup.phone}'/>" readonly></div>
                            <div class="col-12"><label class="form-label">Địa chỉ</label><input class="form-control" value="<c:out value='${pickup.description}'/>, <c:out value='${pickup.ward}'/>, <c:out value='${pickup.city}'/>" readonly></div>
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