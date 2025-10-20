<%@page contentType="text/html" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <div id="layoutSidenav_nav">
      <nav class="sb-sidenav accordion sb-sidenav-light" id="sidenavAccordion">
        <div class="sb-sidenav-menu">
          <div class="nav">
            <div class="sb-sidenav-menu-heading">Tổng quan</div>
            <a class="nav-link" href="/dashboard">
              <div class="sb-nav-link-icon"><i class="bi bi-speedometer2"></i></div>
              Dashboard
            </a>

            <div class="sb-sidenav-menu-heading">Quản lý</div>
            <a class="nav-link" href="/shop/information">
              <div class="sb-nav-link-icon"><i class="bi bi-shop"></i></div>
              Quản lý shop
            </a>
            <a class="nav-link" href="/shop/product">
              <div class="sb-nav-link-icon"><i class="bi bi-box-seam"></i></div>
              Sản phẩm
            </a>
            <a class="nav-link" href="/shop/order">
              <div class="sb-nav-link-icon"><i class="bi bi-cart3"></i></div>
              Đơn hàng
            </a>
            <a class="nav-link" href="/shop/voucher">
              <div class="sb-nav-link-icon">
                <i class="bi bi-ticket-perforated"></i>
              </div>
              Khuyến mãi
            </a>
            <a class="nav-link" href="#!">
              <div class="sb-nav-link-icon"><i class="bi bi-people"></i></div>
              Tài khoản
            </a>
          </div>
        </div>
        <div class="sb-sidenav-footer">
          <div class="small">Đăng nhập với:</div>
          Aurora Admin
        </div>
      </nav>
    </div>