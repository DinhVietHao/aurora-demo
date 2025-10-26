<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <header class="header">
                    <div class="container">
                        <div class="row header-content">
                            <div class="col-3 col-md-2">
                                <a href="${ctx}/home" class="header-logo">
                                    <img src="${ctx}/assets/images/branding/logo-header.png" alt="Logo"
                                        style="height:60px; width:auto;">
                                </a>
                            </div>

                            <div class="col-6 col-md-5">
                                <div class="header-search">
                                    <form method="GET" action="${ctx}/home">
                                        <input type="hidden" name="action" value="search" />
                                        <input type="text" class="form-control rounded-pill" name="keyword"
                                            placeholder="Hôm nay bạn mua gì ..." value="${param.keyword}" />
                                        <button type="submit" class="btn btn-light btn-sm rounded-pill">Tìm
                                            kiếm</button>
                                    </form>
                                </div>
                            </div>

                            <div class="col-3 col-md-5">
                                <nav class="header-nav">
                                    <a href="${ctx}/" class="header-nav-item header-mobile-disable">
                                        <i class="bi bi-house"></i> <span>Trang chủ</span>
                                    </a>
                                    <a href="${ctx}/home?action=bookstore"
                                        class="header-nav-item header-mobile-disable">
                                        <i class="bi bi-journal-bookmark"></i> <span>Nhà sách</span>
                                    </a>

                                    <c:choose>
                                        <c:when test="${empty sessionScope.AUTH_USER}">
                                            <a type="button" class="header-nav-item" data-open='login'>
                                                <i class="bi bi-person"></i> <span>Tài khoản</span>
                                            </a>
                                        </c:when>

                                        <c:otherwise>

                                            <div class="dropdown">
                                                <button class="header-user dropdown-toggle" type="button"
                                                    data-bs-toggle="dropdown" aria-expanded="false">
                                                    <i class="bi bi-person-circle me-1"></i>
                                                    <c:out value="${sessionScope.AUTH_USER.fullName}" />
                                                </button>
                                                <ul class="dropdown-menu dropdown-menu-end">
                                                    <li><a class="dropdown-item" href="<c:url value='/profile'/>">Thông
                                                            tin tài
                                                            khoản</a></li>
                                                    <li><a class="dropdown-item" href="<c:url value='/order'/>">Đơn hàng
                                                            của
                                                            tôi</a></li>
                                                    <li>
                                                        <a class="dropdown-item" id="shopStatusBtn"
                                                            style="cursor: pointer;">
                                                            <i class="bi bi-people"></i> <span>Kênh người bán</span>
                                                        </a>
                                                    </li>
                                                    <li>
                                                        <hr class="dropdown-divider">
                                                    </li>
                                                    <li>
                                                        <form id="logoutForm" action="/auth" method="POST"
                                                            class="px-3 py-1">
                                                            <input hidden name="action" value="logout">
                                                            <button type="submit" class="dropdown-item text-danger">
                                                                <i class="bi bi-box-arrow-right me-1"></i> Đăng xuất
                                                            </button>
                                                        </form>
                                                    </li>
                                                </ul>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <a href="<c:url value='/cart'/>" class="header-cart">
                                        <i class="bi bi-cart3"></i>
                                        <span class="badge" id="cartCountBadge">
                                            <c:out
                                                value="${sessionScope.cartCount != null ? sessionScope.cartCount : 0}" />
                                        </span>
                                    </a>
                                    <a href="#" class="header-notification" data-bs-toggle="offcanvas"
                                        data-bs-target="#cusNotificationOffcanvas"
                                        aria-controls="cusNotificationOffcanvas">
                                        <i class="bi bi-bell"></i>
                                        <c:if test="${sessionScope.unreadNotificationCount > 0}">
                                            <span class="badge" id="notificationCountBadge">
                                                <c:out value="${sessionScope.unreadNotificationCount}" />
                                            </span>
                                        </c:if>
                                    </a>
                                </nav>
                            </div>
                        </div>
                    </div>

                    <!-- Toast notification -->
                    <div id="notify-toast"></div>

                    <!-- Include modal dùng chung -->
                    <jsp:include page="/WEB-INF/views/auth/_modals.jsp" />
                </header>


                <style>
                    /* Offcanvas vùng thông báo */
                    #cusNotificationOffcanvas.offcanvas {
                        top: 70px;
                        border-top-left-radius: 1rem;
                        border-top-right-radius: 1rem;
                        box-shadow: 0 0 20px rgba(0, 0, 0, 0.15);
                    }

                    #cusNotificationOffcanvas .offcanvas-header {
                        background-color: #f8f9fa;
                        padding: 1rem 1.25rem;
                    }

                    #cusNotificationOffcanvas .offcanvas-title {
                        font-weight: 600;
                        color: #333;
                    }

                    /* Item thông báo */
                    .cus-noti-item {
                        display: flex;
                        align-items: flex-start;
                        padding: 0.9rem 1.2rem;
                        border-bottom: 1px solid #f1f1f1;
                        text-decoration: none;
                        transition: background-color 0.2s ease;
                    }

                    .cus-noti-item:hover {
                        background-color: #f8f9fa;
                    }

                    /* Icon tròn màu */
                    .cus-noti-icon {
                        width: 40px;
                        height: 40px;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin-right: 12px;
                        flex-shrink: 0;
                    }

                    .cus-noti-icon i {
                        font-size: 18px;
                        color: #fff;
                    }

                    /* Màu từng loại */
                    .cus-icon-success {
                        background-color: #28a745;
                    }

                    .cus-icon-primary {
                        background-color: #0d6efd;
                    }

                    .cus-icon-danger {
                        background-color: #dc3545;
                    }

                    .cus-icon-warning {
                        background-color: #ffc107;
                        color: #333 !important;
                    }

                    .cus-icon-purple {
                        background-color: #6f42c1;
                    }

                    .cus-icon-secondary {
                        background-color: #6c757d;
                    }

                    /* Nội dung thông báo */
                    .cus-noti-text .cus-title {
                        font-weight: 600;
                        color: #212529;
                        margin-bottom: 2px;
                    }

                    .cus-noti-text .cus-message {
                        font-size: 0.9rem;
                        color: #555;
                    }

                    .cus-noti-time {
                        font-size: 0.8rem;
                        color: #999;
                        margin-top: 2px;
                    }

                    .cus-empty {
                        padding: 3rem 1rem;
                        text-align: center;
                        color: #888;
                    }
                </style>

                <div class="offcanvas offcanvas-end shadow-lg" tabindex="-1" id="cusNotificationOffcanvas"
                    aria-labelledby="cusNotificationOffcanvasLabel">
                    <div class="offcanvas-header border-bottom">
                        <h5 class="offcanvas-title" id="cusNotificationOffcanvasLabel">
                            <i class="bi bi-bell me-2"></i>Thông báo gần đây
                        </h5>
                        <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas"
                            aria-label="Close"></button>
                    </div>

                    <div class="offcanvas-body p-0">
                        <div class="list-group list-group-flush">
                            <c:forEach var="n" items="${listNotifications}">
                                <c:set var="iconClass">
                                    <c:choose>
                                        <c:when test="${n.type == 'ORDER_CONFIRM'}">cus-icon-purple bi-bag</c:when>
                                        <c:when test="${n.type == 'ORDER_SHIPPING'}">cus-icon-success bi-truck</c:when>
                                        <c:when test="${n.type == 'ORDER_CANCELLED'}">cus-icon-danger bi-x-circle
                                        </c:when>
                                        <c:when test="${n.type == 'ORDER_RETURNED'}">cus-icon-warning
                                            bi-arrow-return-left</c:when>
                                        <c:when test="${n.type == 'ORDER_RETURNED_REJECTED'}">cus-icon-danger
                                            bi-arrow-repeat</c:when>
                                        <c:otherwise>cus-icon-secondary bi-bell</c:otherwise>
                                    </c:choose>
                                </c:set>

                                <a href="${ctx}${n.link}" class="cus-noti-item">
                                    <div class="cus-noti-icon ${fn:split(iconClass, ' ')[0]}">
                                        <i class="bi ${fn:split(iconClass, ' ')[1]}"></i>
                                    </div>
                                    <div class="cus-noti-text flex-grow-1">
                                        <div class="cus-title">${n.title}</div>
                                        <div class="cus-message">${n.message}</div>
                                        <div class="cus-noti-time">${n.timeAgo}</div>
                                    </div>
                                </a>
                            </c:forEach>

                            <c:if test="${empty listNotifications}">
                                <div class="cus-empty">
                                    <i class="bi bi-bell-slash fs-3 d-block mb-2"></i>
                                    Chưa có thông báo nào
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>