<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
                                                            tin
                                                            tài
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

                                    <!-- Cart Icon -->
                                    <a href="<c:url value='/cart'/>" class="header-cart">
                                        <i class="bi bi-cart3"></i>
                                        <span class="badge" id="cartCountBadge">
                                            <c:out
                                                value="${sessionScope.cartCount != null ? sessionScope.cartCount : 0}" />
                                        </span>
                                    </a>

                                    <!-- Notification Bell -->
                                    <c:if test="${not empty sessionScope.AUTH_USER 
                                            and sessionScope.AUTH_USER.roles != null 
                                            and sessionScope.AUTH_USER.roles.contains('CUSTOMER')}">
                                        <div class="dropdown notification-dropdown">
                                            <button class="header-notification" type="button" id="notificationDropdown"
                                                data-bs-toggle="dropdown" data-bs-auto-close="outside"
                                                aria-expanded="false">
                                                <i class="bi bi-bell"></i>
                                                <!-- Mock badge count -->
                                                <span class="badge" id="notificationCountBadge">
                                                    <c:out value="${totalNotifications}" default="0" />
                                                </span>
                                            </button>

                                            <!-- Notification Dropdown Menu -->
                                            <div class="dropdown-menu dropdown-menu-end notification-dropdown-menu"
                                                aria-labelledby="notificationDropdown">

                                                <!-- Header -->
                                                <div class="notification-dropdown-header">
                                                    <h6 class="mb-0">
                                                        <i class="bi bi-bell-fill text-primary me-2"></i>
                                                        Thông báo
                                                    </h6>
                                                </div>

                                                <!-- Notification List -->
                                                <div class="notification-dropdown-list" id="notificationDropdownList">
                                                    <c:choose>
                                                        <c:when test="${not empty listNotifications}">
                                                            <c:forEach var="n" items="${listNotifications}">
                                                                <c:set var="iconClass">
                                                                    <c:choose>
                                                                        <c:when test="${n.type == 'ORDER_CONFIRM'}">
                                                                            cus-icon-purple bi-bag</c:when>
                                                                        <c:when test="${n.type == 'ORDER_SHIPPING'}">
                                                                            cus-icon-success bi-truck</c:when>
                                                                        <c:when test="${n.type == 'ORDER_CANCELLED'}">
                                                                            cus-icon-danger bi-x-circle</c:when>
                                                                        <c:when test="${n.type == 'ORDER_RETURNED'}">
                                                                            cus-icon-warning bi-arrow-return-left
                                                                        </c:when>
                                                                        <c:when
                                                                            test="${n.type == 'ORDER_RETURNED_REJECTED'}">
                                                                            cus-icon-danger bi-arrow-repeat</c:when>
                                                                        <c:otherwise>cus-icon-secondary bi-bell
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:set>
                                                                <a href="${ctx}${n.link}" class="cus-noti-item">
                                                                    <div
                                                                        class="cus-noti-icon ${fn:split(iconClass, ' ')[0]}">
                                                                        <i
                                                                            class="bi ${fn:split(iconClass, ' ')[1]}"></i>
                                                                    </div>
                                                                    <div class="cus-noti-text flex-grow-1">
                                                                        <div class="cus-title">${n.title}</div>
                                                                        <div class="cus-message">${n.message}</div>
                                                                        <div class="cus-noti-time">${n.timeAgo}</div>
                                                                    </div>
                                                                </a>
                                                            </c:forEach>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="notification-empty-dropdown text-center py-4">
                                                                <i class="bi bi-bell-slash"></i>
                                                                <p class="mb-0">Chưa có thông báo</p>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                </nav>
                            </div>
                        </div>
                    </div>

                    <!-- Toast notification -->
                    <div id="notify-toast"></div>

                    <!-- Include modal dùng chung -->
                    <jsp:include page="/WEB-INF/views/auth/_modals.jsp" />
                </header>

                <script>
                    document.addEventListener('DOMContentLoaded', function () {
                        // Add animation when dropdown opens
                        const notificationDropdown = document.getElementById('notificationDropdown');
                        if (notificationDropdown) {
                            notificationDropdown.addEventListener('show.bs.dropdown', function () {
                                const items = document.querySelectorAll('.notification-dropdown-item');
                                items.forEach((item, index) => {
                                    item.style.animation = 'slideInRight 0.3s ease-out';
                                    item.style.animationDelay = (index * 0.05) + 's';
                                });
                            });
                        }
                    });
                </script>