<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<header class="header">
    <div class="container">
        <div class="row header-content">
            <div class="col-3 col-md-2">
                <a href="${ctx}/" class="header-logo">
                    <img src="${ctx}/assets/images/branding/logo-header.png" alt="Logo" style="height:60px; width:auto;">
                </a>
            </div>

            <div class="col-6 col-md-5">
                <div class="header-search">
                    <span class="icon"><i class="bi bi-search"></i></span>
                    <input type="text" class="form-control rounded-pill" placeholder="Hôm nay bạn mua gì ...">
                    <button class="btn btn-light btn-sm rounded-pill">Tìm kiếm</button>
                </div>
            </div>

            <div class="col-3 col-md-5">
                <nav class="header-nav">
                    <a href="${ctx}/" class="header-nav-item header-mobile-disable">
                        <i class="bi bi-house"></i> <span>Trang chủ</span>
                    </a>
                    <a href="${ctx}/books" class="header-nav-item header-mobile-disable">
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
                                    <c:out value="${sessionScope.AUTH_USER.fullName}"/>
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li><a class="dropdown-item" href="<c:url value='/account'/>">Thông tin tài khoản</a></li>
                                    <li><a class="dropdown-item" href="<c:url value='/orders'/>">Đơn hàng của tôi</a></li>
                                    <li><a class="dropdown-item" href="<c:url value='/support'/>">Trung tâm hỗ trợ</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <form id="logoutForm" action="<c:url value='/auth/logout'/>" method="post" class="px-3 py-1">
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
                            <c:out value="${sessionScope.cartCount != null ? sessionScope.cartCount : 0}" />
                        </span>
                    </a>
                </nav>
            </div>
        </div>
    </div>

    <!-- Include modal dùng chung -->
    <jsp:include page="/WEB-INF/views/auth/_modals.jsp"/>

</header>