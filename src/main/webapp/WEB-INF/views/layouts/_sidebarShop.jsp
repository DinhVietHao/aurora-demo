<%@page contentType="text/html" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <div id="layoutSidenav_nav">
      <nav class="sb-sidenav accordion sb-sidenav-light" id="sidenavAccordion">
        <div class="sb-sidenav-menu">
          <div class="nav">
            <div class="sb-sidenav-menu-heading">Tổng quan</div>
            <a class="nav-link" href="${ctx}/shop?action=dashboard" data-page="shop-dashboard">
              <i class="sb-nav-link-icon bi bi-speedometer2"></i>
              Dashboard
            </a>

            <div class="sb-sidenav-menu-heading">Quản lý</div>
            <a class="nav-link" href="/shop?action=shopProfile" data-page="shop-profile">
              <div class="sb-nav-link-icon"><i class="bi bi-shop"></i></div>
              Thông tin cửa hàng
            </a>
            <a class="nav-link" href="/shop/product" data-page="shop-products">
              <div class="sb-nav-link-icon"><i class="bi bi-box-seam"></i></div>
              Sản phẩm
            </a>
            <a class="nav-link" href="/shop/orders" data-page="shop-orders">
              <div class="sb-nav-link-icon"><i class="bi bi-cart3"></i></div>
              Đơn hàng
            </a>
            <a class="nav-link" href="/shop/voucher" data-page="shop-vouchers">
              <div class="sb-nav-link-icon">
                <i class="bi bi-ticket-perforated"></i>
              </div>
              Khuyến mãi
            </a>
          </div>
        </div>
        <div class="sb-sidenav-footer">
          <div class="small">
            <i class="bi bi-person-circle"></i> Đăng nhập với:
          </div>
          <strong class="user-name">
            ${sessionScope.AUTH_USER.fullName != null ? sessionScope.AUTH_USER.fullName : 'Aurora Bookstore'}
          </strong>
        </div>
      </nav>
    </div>