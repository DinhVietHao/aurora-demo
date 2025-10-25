<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <fmt:setLocale value="vi_VN" />
            <c:set var="ctx" value="${pageContext.request.contextPath}" />
            <c:set var="pageTitle" value="Thông báo - Aurora" />

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
                <link rel="stylesheet" href="${ctx}/assets/css/customer/profile/notification.css?v=1.0.1">
                <link rel="stylesheet" href="${ctx}/assets/css/customer/profile/information_account.css">
            </head>

            <body>
                <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                <!-- Giao diện chính -->
                <div class="container mt-3 information-account">
                    <div class="row">
                        <!-- Sidebar người dùng -->
                        <div class="col-3 col-md-2 information-account__sidebar">
                            <div class="text-center mb-4">
                                <c:choose>
                                    <c:when test="${not empty user.avatarUrl}">
                                        <img src="${ctx}/assets/images/avatars/${user.avatarUrl}" alt="avatar"
                                            class="information-account__image">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${ctx}/assets/images/common/avatar.png" alt="avatar"
                                            class="information-account__image">
                                    </c:otherwise>
                                </c:choose>
                                <p class="mt-2 fw-bold mb-0">
                                    <c:out value="${user.fullName}" />
                                </p>
                            </div>

                            <!-- Menu điều hướng -->
                            <ul class="nav mb-3" id="profileTabs" role="tablist">
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark active" href="${ctx}/profile?action=notification">
                                        <i class="bi bi-bell me-2"></i> Thông báo
                                        <c:if test="${unreadNotificationCount > 0}">
                                            <span class="badge bg-danger ms-2">${unreadNotificationCount}</span>
                                        </c:if>
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark" href="${ctx}/profile">
                                        <i class="bi bi-person me-2"></i> Hồ sơ
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark" href="${ctx}/address">
                                        <i class="bi bi-geo-alt me-2"></i> Địa Chỉ
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark" href="${ctx}/order">
                                        <i class="bi bi-box-seam me-2"></i> Quản lý đơn hàng
                                    </a>
                                </li>
                            </ul>
                        </div>

                        <!-- Nội dung chính -->
                        <div class="col-9 col-md-10">
                            <div class="tab-content" id="notificationTabContent">
                                <div class="tab-pane fade show active" role="tabpanel">
                                    <div class="notification-content-wrapper">
                                        <!-- Tiêu đề và nút thao tác -->
                                        <div class="notification-header-section mb-4">
                                            <div class="d-flex justify-content-between align-items-center mb-3">
                                                <h5 class="mb-0 profile-title">
                                                    <i class="bi bi-bell-fill text-primary me-2"></i>
                                                    Thông báo
                                                </h5>
                                            </div>

                                            <!-- Danh sách thông báo -->
                                            <div class="tab-content">
                                                <div class="tab-pane fade show active" id="all" role="tabpanel">
                                                    <div class="notification-list">
                                                        <c:choose>
                                                            <c:when test="${not empty notifications}">
                                                                <c:forEach var="notif" items="${notifications}">
                                                                    <!-- Thông báo đơn -->
                                                                    <div class="notification-item ${notif.isRead ? 'read' : 'unread'}"
                                                                        data-notification-id="${notif.notificationId}">
                                                                        <div class="notification-icon ${notif.type}">
                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${notif.type == 'ORDER_NEW' || notif.type == 'ORDER_DELIVERED'}">
                                                                                    <i class="bi bi-box-seam"></i>
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${notif.type == 'PROMOTION'}">
                                                                                    <i class="bi bi-gift"></i>
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${notif.type == 'SYSTEM'}">
                                                                                    <i class="bi bi-info-circle"></i>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <i class="bi bi-bell"></i>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </div>

                                                                        <div class="notification-content">
                                                                            <h6 class="notification-title">
                                                                                <c:out value="${notif.title}" />
                                                                            </h6>
                                                                            <p class="notification-message">
                                                                                <c:out value="${notif.message}" />
                                                                            </p>
                                                                            <div class="notification-meta">
                                                                                <span class="notification-time">
                                                                                    <i class="bi bi-clock me-1"></i>
                                                                                    <fmt:formatDate
                                                                                        value="${notif.createdAt}"
                                                                                        pattern="dd/MM/yyyy HH:mm" />
                                                                                </span>
                                                                                <c:if
                                                                                    test="${not empty notif.referenceType && not empty notif.referenceId}">
                                                                                    <a href="${ctx}/${notif.referenceType}?id=${notif.referenceId}"
                                                                                        class="notification-action">
                                                                                        Xem chi tiết
                                                                                        <i
                                                                                            class="bi bi-arrow-right ms-1"></i>
                                                                                    </a>
                                                                                </c:if>
                                                                            </div>
                                                                        </div>

                                                                        <!-- Menu hành động -->
                                                                        <div class="notification-actions-menu">
                                                                            <div class="dropdown">
                                                                                <button class="btn btn-sm btn-light"
                                                                                    type="button"
                                                                                    data-bs-toggle="dropdown">
                                                                                    <i
                                                                                        class="bi bi-three-dots-vertical"></i>
                                                                                </button>
                                                                                <ul
                                                                                    class="dropdown-menu dropdown-menu-end">
                                                                                    <li>
                                                                                        <button
                                                                                            class="dropdown-item mark-read-btn"
                                                                                            data-id="${notif.notificationId}">
                                                                                            <i
                                                                                                class="bi bi-check me-2"></i>
                                                                                            Đánh dấu đã đọc
                                                                                        </button>
                                                                                    </li>
                                                                                    <li>
                                                                                        <button
                                                                                            class="dropdown-item text-danger delete-btn"
                                                                                            data-id="${notif.notificationId}">
                                                                                            <i
                                                                                                class="bi bi-trash me-2"></i>
                                                                                            Xóa
                                                                                        </button>
                                                                                    </li>
                                                                                </ul>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </c:forEach>

                                                                <!-- Phân trang -->
                                                                <c:if test="${totalPages > 1}">
                                                                    <nav class="mt-4">
                                                                        <ul class="pagination justify-content-center">
                                                                            <c:if test="${currentPage > 1}">
                                                                                <li class="page-item">
                                                                                    <a class="page-link"
                                                                                        href="?page=${currentPage - 1}">‹</a>
                                                                                </li>
                                                                            </c:if>

                                                                            <c:forEach begin="1" end="${totalPages}"
                                                                                var="i">
                                                                                <li
                                                                                    class="page-item ${i == currentPage ? 'active' : ''}">
                                                                                    <a class="page-link"
                                                                                        href="?page=${i}">${i}</a>
                                                                                </li>
                                                                            </c:forEach>

                                                                            <c:if test="${currentPage < totalPages}">
                                                                                <li class="page-item">
                                                                                    <a class="page-link"
                                                                                        href="?page=${currentPage + 1}">›</a>
                                                                                </li>
                                                                            </c:if>
                                                                        </ul>
                                                                    </nav>
                                                                </c:if>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <!-- Khi chưa có thông báo -->
                                                                <div class="notification-empty">
                                                                    <div class="empty-icon">
                                                                        <i class="bi bi-bell-slash"></i>
                                                                    </div>
                                                                    <h5>Chưa có thông báo</h5>
                                                                    <p class="text-muted">
                                                                        Bạn chưa có thông báo nào. Các thông báo về đơn
                                                                        hàng,
                                                                        khuyến mãi sẽ hiển thị ở đây.
                                                                    </p>
                                                                    <a href="${ctx}/home?action=bookstore"
                                                                        class="btn btn-primary mt-3">
                                                                        <i class="bi bi-shop me-2"></i>
                                                                        Khám phá sản phẩm
                                                                    </a>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
                    <script src="${ctx}/assets/js/customer/notification.js?v=1.0.0"></script>
            </body>

            </html>