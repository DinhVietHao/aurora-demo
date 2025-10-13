<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div id="layoutSidenav_nav">
    <nav class="sb-sidenav accordion sb-sidenav-dark" id="sidenavAccordion">
        <div class="sb-sidenav-menu">
            <div class="nav">
                <div class="sb-sidenav-menu-heading">Tổng quan</div>
                <a class="nav-link active" href="<c:url value='/admin/dashboard'/>">
                    <div class="sb-nav-link-icon"><i class="bi bi-speedometer2"></i></div>
                    Dashboard
                </a>
                
                <div class="sb-sidenav-menu-heading">Quản lý</div>
                <a class="nav-link" href="<c:url value='/admin/shops'/>">
                    <div class="sb-nav-link-icon"><i class="bi bi-shop"></i></div>
                    Quản lý shop
                </a>
                
                <a class="nav-link" href="<c:url value='/admin/products'/>">
                    <div class="sb-nav-link-icon"><i class="bi bi-box-seam"></i></div>
                    Sản phẩm
                </a>
                
                <a class="nav-link" href="<c:url value='/admin/orders'/>">
                    <div class="sb-nav-link-icon"><i class="bi bi-cart3"></i></div>
                    Đơn hàng
                </a>
                
                <a class="nav-link" href="<c:url value='/admin/vouchers'/>">
                    <div class="sb-nav-link-icon"><i class="bi bi-ticket-perforated"></i></div>
                    Khuyến mãi
                </a>
                
                <a class="nav-link" href="<c:url value='/admin/users'/>">
                    <div class="sb-nav-link-icon"><i class="bi bi-people"></i></div>
                    Tài khoản
                </a>
                
                <a class="nav-link" href="<c:url value='/admin/flash-sales'/>">
                    <div class="sb-nav-link-icon"><i class="bi bi-lightning-charge"></i></div>
                    Flash sales
                </a>
            </div>
        </div>
        <div class="sb-sidenav-footer">
            <div class="small">Đăng nhập với:</div>
            Aurora Admin
        </div>
    </nav>
</div>