<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <fmt:setLocale value="vi_VN" />
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <!-- Reuse head chung -->
                <jsp:include page="/WEB-INF/views/layouts/_head.jsp">
                    <jsp:param name="title" value="Giỏ hàng - Aurora" />
                </jsp:include>

                <!-- CSS riêng trang Cart -->
                <link rel="stylesheet" href="./assets/css/customer/profile/information_account.css?v=1.0.1">
                <link rel="stylesheet" href="./assets/css/customer/order/order.css?v=1.0.1">
                <link rel="stylesheet" href="./assets/css/customer/address/address.css?v=1.0.1">
            </head>

            <body>
                <!-- Header + các modal auth dùng chung -->
                <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                <div class="container mt-3 information-account">
                    <div class="row ">
                        <div class="col-3 col-md-2 information-account__sidebar">

                            <div class="text-center mb-4">
                                <img src="./assets/images/common/avatar.png" alt="avatar"
                                    class="information-account__image">
                                <p class="mt-2 fw-bold mb-0">Leminhkha220</p>
                            </div>

                            <!-- sidebar profile -->
                            <ul class="nav mb-3 " id="profileTabs" role="tablist">
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark" id="notify-tab" data-bs-toggle="tab" href="#notify"
                                        role="tab">
                                        <i class="bi bi-bell me-2"></i> Thông báo
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark " href="/profile">
                                        <i class="bi bi-person me-2"></i> Hồ sơ
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark " href="/address">
                                        <i class="bi bi-geo-alt me-2"></i> Địa Chỉ
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark active" href="/order">
                                        <i class="bi bi-box-seam me-2"></i> Quản lý đơn hàng
                                    </a>
                                </li>
                            </ul>
                        </div>

                        <div class="col-9 col-md-10 ">
                            <div class="tab-content" id="profileTabsContent">
                                <!-- Thông báo -->
                                <div class="tab-pane fade" id="notify" role="tabpanel" aria-labelledby="notify-tab">
                                    <div class="text-center mt-5">
                                        <img src="./assets/images/mascot_fail.svg" alt="">
                                        <p class="text-muted mt-3">Chưa có thông báo</p>
                                    </div>
                                </div>

                                <!-- Quản lý đơn hàng -->
                                <div class="tab-pane fade show active order-management" id="order" role="tabpanel"
                                    aria-labelledby="order-tab">
                                    <div class="order-content">
                                        <ul class="nav nav-tabs mb-3 order-tabs" id="orderTabs" role="tablist">
                                            <li class="nav-item"><a class="nav-link active" id="all-tab"
                                                    data-bs-toggle="tab" href="#all" role="tab">Tất
                                                    cả</a></li>
                                            <li class="nav-item"><a class="nav-link" id="pending-tab"
                                                    data-bs-toggle="tab" href="#pending" role="tab">Chờ xác nhận</a>
                                            </li>
                                            <li class="nav-item"><a class="nav-link" id="shipping-tab"
                                                    data-bs-toggle="tab" href="#shipping" role="tab">Đang giao</a></li>
                                            <li class="nav-item"><a class="nav-link" id="ready-tab" data-bs-toggle="tab"
                                                    href="#ready" role="tab">Chờ
                                                    giao hàng</a></li>
                                            <li class="nav-item"><a class="nav-link" id="done-tab" data-bs-toggle="tab"
                                                    href="#done" role="tab">Hoàn
                                                    thành</a></li>
                                            <li class="nav-item"><a class="nav-link" id="cancel-tab"
                                                    data-bs-toggle="tab" href="#cancel" role="tab">Đã
                                                    hủy</a></li>
                                        </ul>

                                        <!-- Search -->
                                        <div class="header-search mb-3">
                                            <span class="icon">
                                                <i class="bi bi-search"></i>
                                            </span>
                                            <input type="text" class="form-control rounded-pill"
                                                placeholder="Tìm đơn hàng theo mã đơn hàng, nhà bán hoặc tên sản phẩm">
                                            <button class="btn btn-light btn-sm rounded-pill">Tìm kiếm</button>
                                        </div>

                                        <!-- Tabs content -->
                                        <div class="tab-content order-body">

                                            <!-- Tất cả -->
                                            <div class="tab-pane fade show active " id="all" role="tabpanel">

                                                <div class="order-card">
                                                    <div class="order-card__header">
                                                        <span><strong><i class="bi bi-shop me-2"></i> BookHaven
                                                                Store</strong>
                                                            <a href="./viewShop.html" class="button-outline mx-2"> Xem
                                                                shop</a></span>
                                                        <div>
                                                            <span class="text-color">Trạng thái: </span>
                                                            <span class="badge bg-success">Giao hàng</span>
                                                        </div>
                                                    </div>

                                                    <div class="order-card__body">
                                                        <div class="col-2">
                                                            <img class="order-card__image"
                                                                src="./assets/images/product-1.png" alt="product">
                                                        </div>
                                                        <div class="col-10">
                                                            <h6>The Great Adventure - Fantasy Novel</h6>
                                                            <p class="mb-2 text-color">Phần loại: Fiction</p>
                                                            <div class="d-flex justify-content-between">
                                                                <p class="text-color">Số lượng: 2</p>
                                                                <div>
                                                                    <span
                                                                        class="text-decoration-line-through text-color">188.000đ</span>
                                                                    <span class="fw-bold text-danger">149.000đ</span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class="text-end">
                                                        <p class="text-color">Thành tiền: <span
                                                                class="order-card__price">50.000đ</span>
                                                        </p>
                                                        <button class="button-four"><i
                                                                class="bi bi-arrow-repeat me-1"></i> Mua
                                                            lại</button>
                                                    </div>
                                                </div>

                                                <div class="order-card">
                                                    <div class="order-card__header">
                                                        <span><strong><i class="bi bi-shop me-2"></i> BookHaven
                                                                Store</strong>
                                                            <a href="./viewShop.html" class="button-outline mx-2"> Xem
                                                                shop</a></span>
                                                        <div>
                                                            <span class="text-color">Trạng thái: </span>
                                                            <span class="badge bg-success">Hoàn thành</span>
                                                        </div>
                                                    </div>

                                                    <div class="order-card__body">
                                                        <div class="col-2">
                                                            <img class="order-card__image"
                                                                src="./assets/images/product-1.png" alt="product">
                                                        </div>
                                                        <div class="col-10">
                                                            <h6>The Great Adventure - Fantasy Novel</h6>
                                                            <p class="mb-2 text-color">Phần loại: Fiction</p>
                                                            <div class="d-flex justify-content-between">
                                                                <p class="text-color">Số lượng: 2</p>
                                                                <div>
                                                                    <span
                                                                        class="text-decoration-line-through text-color">188.000đ</span>
                                                                    <span class="fw-bold text-danger">149.000đ</span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class="text-end">
                                                        <p class="text-color">Thành tiền: <span
                                                                class="order-card__price">50.000đ</span>
                                                        </p>
                                                        <button class="button-four"><i
                                                                class="bi bi-arrow-repeat me-1"></i> Mua
                                                            lại</button>

                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Chờ xác nhận -->
                                            <div class="tab-pane fade order-card" id="pending" role="tabpanel">
                                                <div class="order-card__header">
                                                    <span><strong><i class="bi bi-shop me-2"></i> BookHaven
                                                            Store</strong>
                                                        <a href="./viewShop.html" class="button-outline mx-2"> Xem
                                                            shop</a></span>
                                                    <div>
                                                        <span class="text-color">Trạng thái: </span>
                                                        <span class="badge bg-success">Giao hàng</span>
                                                    </div>
                                                </div>

                                                <div class="order-card__body">
                                                    <div class="col-2">
                                                        <img class="order-card__image"
                                                            src="./assets/images/product-1.png" alt="product">
                                                    </div>
                                                    <div class="col-10">
                                                        <h6>The Great Adventure - Fantasy Novel</h6>
                                                        <p class="mb-2 text-color">Phân loại: Fiction</p>
                                                        <div class="d-flex justify-content-between">
                                                            <p class="text-color">Số lượng: 2</p>
                                                            <div>
                                                                <span
                                                                    class="text-decoration-line-through text-color">188.000đ</span>
                                                                <span class="fw-bold text-danger">149.000đ</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="text-end">
                                                    <p class="text-color">Thành tiền: <span
                                                            class="order-card__price">50.000đ</span>
                                                    </p>
                                                    <button class="button-six" data-bs-toggle="modal"
                                                        data-bs-target="#cancelOrderModal"> Hủy
                                                        đơn</button>
                                                </div>
                                            </div>

                                            <!-- Đang giao -->
                                            <div class="tab-pane fade order-card" id="shipping" role="tabpanel">
                                                <div class="text-center">
                                                    <img src="./assets/images/empty-order.png" alt="">
                                                    <p class="text-muted">Chưa có đơn hàng</p>
                                                </div>
                                            </div>

                                            <!-- Đang Chờ giao -->
                                            <div class="tab-pane fade order-card" id="ready" role="tabpanel">
                                                <div class="order-card__header">
                                                    <span><strong><i class="bi bi-shop me-2"></i> BookHaven
                                                            Store</strong>
                                                        <a href="./viewShop.html" class="button-outline mx-2"> Xem
                                                            shop</a></span>
                                                    <div>
                                                        <span class="text-color">Trạng thái: </span>
                                                        <span class="badge bg-success">Giao hàng thành công</span>
                                                    </div>
                                                </div>

                                                <div class="order-card__body">
                                                    <div class="col-2">
                                                        <img class="order-card__image"
                                                            src="./assets/images/product-1.png" alt="product">
                                                    </div>
                                                    <div class="col-10">
                                                        <h6>The Great Adventure - Fantasy Novel</h6>
                                                        <p class="mb-2 text-color">Phân loại: Fiction</p>
                                                        <div class="d-flex justify-content-between">
                                                            <p class="text-color">Số lượng: 2</p>
                                                            <div>
                                                                <span
                                                                    class="text-decoration-line-through text-color">188.000đ</span>
                                                                <span class="fw-bold text-danger">149.000đ</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="text-end">
                                                    <p class="text-color">Thành tiền: <span
                                                            class="order-card__price">50.000đ</span>
                                                    </p>
                                                    <button class="button-four" data-bs-toggle="modal"
                                                        data-bs-target="#confirmOrderModal">Đã nhận
                                                        hàng</button>
                                                </div>
                                            </div>

                                            <!-- Hoàn thành -->
                                            <div class="tab-pane fade order-card" id="done" role="tabpanel">
                                                <div class="order-card__header">
                                                    <span><strong><i class="bi bi-shop me-2"></i> BookHaven
                                                            Store</strong>
                                                        <a href="./viewShop.html" class="button-outline mx-2"> Xem
                                                            shop</a></span>
                                                    <div>
                                                        <span class="text-color">Trạng thái: </span>
                                                        <span class="badge bg-success">Giao hàng thành công</span>
                                                    </div>
                                                </div>

                                                <div class="order-card__body">
                                                    <div class="col-2">
                                                        <img class="order-card__image"
                                                            src="./assets/images/product-1.png" alt="product">
                                                    </div>
                                                    <div class="col-10">
                                                        <h6>The Great Adventure - Fantasy Novel</h6>
                                                        <p class="mb-2 text-color">Phân loại: Fiction</p>
                                                        <div class="d-flex justify-content-between">
                                                            <p class="text-color">Số lượng: 2</p>
                                                            <div>
                                                                <span
                                                                    class="text-decoration-line-through text-color">188.000đ</span>
                                                                <span class="fw-bold text-danger">149.000đ</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="text-end">
                                                    <p class="text-color">Thành tiền: <span
                                                            class="order-card__price">50.000đ</span>
                                                    </p>
                                                    <button class="button-four"><i class="bi bi-arrow-repeat me-1"></i>
                                                        Mua
                                                        lại</button>
                                                    <button class="button-five" data-bs-toggle="modal"
                                                        data-bs-target="#ratingModal">
                                                        Đánh giá
                                                        shop</button>

                                                </div>
                                            </div>

                                            <!-- Đã hủy -->
                                            <div class="tab-pane fade order-card" id="cancel" role="tabpanel">
                                                <div class="text-center">
                                                    <img src="./assets/images/empty-order.png" alt="">
                                                    <p class="text-muted">Chưa có đơn hàng</p>
                                                </div>
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Footer & scripts chung -->
                <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

                <!-- Cancel Order Modal -->
                <div class="modal fade" id="cancelOrderModal" tabindex="-1" aria-labelledby="cancelOrderLabel"
                    aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content"> <!-- Header -->
                            <div class="modal-header">
                                <h5 class="modal-title" id="cancelOrderLabel">Huỷ đơn hàng</h5> <button type="button"
                                    class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div> <!-- Body -->
                            <div class="modal-body">
                                <p>Bạn có chắc chắn muốn huỷ đơn hàng <strong>#12345</strong> không?</p>
                                <form id="cancelOrderForm">
                                    <div class="mt-3"> <label for="otherReason" class="form-label">Lý do hủy</label>
                                        <textarea class="form-control" id="otherReason" name="otherReason" rows="2"
                                            placeholder="Nhập lý do khác..."></textarea>
                                    </div> <input type="hidden" name="orderId" id="orderId" value="">
                                </form>
                            </div>
                            <div class="modal-footer"> <button type="button" class="button-five"
                                    data-bs-dismiss="modal">Đóng</button> <button type="submit" form="cancelOrderForm"
                                    class="button-seven">Xác nhận huỷ</button> </div>
                        </div>
                    </div>
                </div>
                <!--End Cancel Order Modal -->



                <!-- Confirm Order Received Modal -->
                <div class="modal fade" id="confirmOrderModal" tabindex="-1" aria-labelledby="confirmOrderLabel"
                    aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="confirmOrderLabel">Xác nhận đã nhận hàng</h5> <button
                                    type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div>
                            <div class="modal-body">
                                <p>Bạn đã chắc chắn nhận đủ hàng từ đơn <strong>#12345</strong> chưa?</p>
                                <p>Sau khi xác nhận, đơn hàng sẽ được chuyển sang trạng thái <strong>Hoàn
                                        thành</strong>.</p> <input type="hidden" name="orderId" id="confirmOrderId"
                                    value="">
                            </div>
                            <div class="modal-footer"> <button type="button" class="button-five"
                                    data-bs-dismiss="modal">Đóng</button> <button type="button" id="btnConfirmOrder"
                                    class="button-four">Xác nhận</button> </div>
                        </div>
                    </div>
                </div>
                <!--End Confirm Order Received Modal -->

                <!-- Link Javascript of Comment -->
                <script src="./assets/js/customer/order/order.js"></script>


                <script src="./assets/js/customer/profile/information_account.js"></script>
            </body>

            </html>