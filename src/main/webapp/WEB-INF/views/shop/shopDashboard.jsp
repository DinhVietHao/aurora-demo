<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <c:set var="pageTitle" value="Aurora" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/shop_products.css?v=1.0.1" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/shop_dashboard.css?v=1.0.1" />
                </head>

                <body class="sb-nav-fixed" data-page="shop-dashboard">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />
                        <div id="layoutSidenav_content">
                            <main>
                                <c:if test="${not empty successMessage}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        ${fn:escapeXml(successMessage)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>

                                <c:if test="${not empty errorMessage}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        ${fn:escapeXml(errorMessage)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>

                                <c:if test="${not empty warning}">
                                    <div class="alert alert-warning alert-dismissible fade show" role="alert">
                                        ${fn:escapeXml(warning)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>
                                <div class="container-fluid px-4">
                                    <!-- Page Header -->
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h1 class="mt-4 dashboard-title">Tổng quan bán hàng</h1>
                                    </div>

                                    <!-- Stats Cards Row -->
                                    <div class="row mt-4">
                                        <div class="col-xl-3 col-md-6">
                                            <div class="card stats-card stats-card-primary mb-4">
                                                <div class="card-body">
                                                    <a href="${ctx}/shop?action=revenueHistory&startDate=${startDate}&endDate=${endDate}"
                                                        class="text-decoration-none">
                                                        <div class="d-flex align-items-center">
                                                            <div class="stats-icon" style="color: white">
                                                                <i class="bi bi-graph-up"></i>
                                                            </div>
                                                            <div class="ms-3">
                                                                <div class="stats-label text-light">Tổng doanh thu</div>
                                                                <div class="stats-value text-light">
                                                                    <fmt:formatNumber value="${totalRevenue}"
                                                                        type="currency" currencySymbol="₫"
                                                                        maxFractionDigits="0" groupingUsed="true" />
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </a>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-xl-3 col-md-6">
                                            <div class="card stats-card stats-card-warning mb-4">
                                                <div class="card-body">
                                                    <a href="/shop/orders?status=COMPLETED">
                                                        <div class="d-flex align-items-center">
                                                            <div class="stats-icon" style="color: white">
                                                                <i class="bi bi-cart-check"></i>
                                                            </div>
                                                            <div class="ms-3">
                                                                <div class="stats-label text-light">Tổng đơn hàng</div>
                                                                <div class="stats-value text-light">${shop.totalOrders}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </a>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-xl-3 col-md-6">
                                            <div class="card stats-card stats-card-success mb-4">
                                                <div class="card-body">
                                                    <a href="/shop/product?action=view">
                                                        <div class="d-flex align-items-center">
                                                            <div class="stats-icon" style="color: white">
                                                                <i class="bi bi-people"></i>
                                                            </div>
                                                            <div class="ms-3">
                                                                <div class="stats-label text-light">Sản phẩm đang bán
                                                                </div>
                                                                <div class="stats-value text-light">
                                                                    ${shop.totalProducts}</div>
                                                            </div>
                                                        </div>
                                                    </a>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-xl-3 col-md-6">
                                            <div class="card stats-card stats-card-info mb-4">
                                                <div class="card-body">
                                                    <div class="d-flex align-items-center">
                                                        <div class="stats-icon">
                                                            <i class="bi bi-star"></i>
                                                        </div>
                                                        <div class="ms-3">
                                                            <div class="stats-label">Đánh giá trung bình</div>
                                                            <div class="stats-value">${shop.avgRating}/5</div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Chart and Notifications -->
                                    <div class="row">
                                        <div class="col-xl-8">
                                            <div class="card mb-4">
                                                <div
                                                    class="card-header d-flex justify-content-between align-items-center">
                                                    <div>
                                                        <i class="bi bi-bar-chart me-1"></i>
                                                        Doanh thu
                                                    </div>

                                                    <!-- Bộ lọc thời gian -->
                                                    <form method="get" action="${ctx}/shop"
                                                        class="d-flex align-items-center gap-2">
                                                        <input type="hidden" name="action" value="dashboard" />
                                                        <label>Từ:</label>
                                                        <input type="date" name="startDate"
                                                            class="form-control form-control-sm" value="${startDate}"
                                                            max="<fmt:formatDate value='${now}' pattern='yyyy-MM-dd'/>" />
                                                        <label>Đến:</label>
                                                        <input type="date" name="endDate"
                                                            class="form-control form-control-sm" value="${endDate}"
                                                            max="<fmt:formatDate value='${now}' pattern='yyyy-MM-dd'/>" />
                                                        <button type="submit"
                                                            class="btn btn-primary btn-sm">Xem</button>
                                                    </form>
                                                </div>
                                                <div class="card-body">
                                                    <c:choose>
                                                        <c:when test="${not empty revenueData}">
                                                            <canvas id="revenueChart" height="120"></canvas>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="text-center py-5">
                                                                <i
                                                                    class="bi bi-bar-chart-line display-1 text-muted"></i>
                                                                <p class="text-muted mt-3">Chưa có dữ liệu doanh thu</p>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-xl-4">
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <i class="bi bi-clock-history me-1"></i>
                                                    Thông báo gần đây
                                                </div>
                                                <div class="card-body">
                                                    <div class="activity-list">

                                                        <c:forEach var="n" items="${notifications}">
                                                            <c:set var="iconClass">
                                                                <c:choose>
                                                                    <c:when test="${n.type == 'ORDER_NEW'}">
                                                                        activity-icon-warning</c:when>
                                                                    <c:when test="${n.type == 'ORDER_DELIVERED'}">
                                                                        activity-icon-primary</c:when>
                                                                    <c:when test="${n.type == 'OUT_OF_STOCK'}">
                                                                        activity-icon-warning</c:when>
                                                                    <c:when test="${n.type == 'RETURN_REQUESTED'}">
                                                                        activity-icon-info</c:when>
                                                                    <c:when test="${n.type == 'ORDER_CANCELLED'}">
                                                                        activity-icon-danger
                                                                    </c:when>
                                                                    <c:when test="${n.type == 'VOUCHER_ACTIVE'}">
                                                                        activity-icon-success</c:when>
                                                                    <c:when test="${n.type == 'VOUCHER_OUT_OF_STOCK'}">
                                                                        activity-icon-warning</c:when>
                                                                    <c:when test="${n.type == 'VOUCHER_EXPIRED'}">
                                                                        activity-icon-secondary</c:when>
                                                                    <c:when test="${n.type == 'PRODUCT_REJECTED'}">
                                                                        activity-icon-danger
                                                                    </c:when>
                                                                    <c:when test="${n.type == 'PRODUCT_ACTIVE'}">
                                                                        activity-icon-success</c:when>
                                                                    <c:otherwise>activity-icon-secondary
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:set>

                                                            <c:set var="biIcon">
                                                                <c:choose>
                                                                    <c:when test="${n.type == 'ORDER_NEW'}">bi
                                                                        bi-cart-plus</c:when>
                                                                    <c:when test="${n.type == 'ORDER_DELIVERED'}">bi
                                                                        bi-box-seam</c:when>
                                                                    <c:when test="${n.type == 'OUT_OF_STOCK'}">bi
                                                                        bi-exclamation-triangle</c:when>
                                                                    <c:when test="${n.type == 'RETURN_REQUESTED'}">bi
                                                                        bi-arrow-return-left</c:when>
                                                                    <c:when test="${n.type == 'ORDER_CANCELLED'}">bi
                                                                        bi-x-circle</c:when>
                                                                    <c:when test="${n.type == 'VOUCHER_ACTIVE'}">bi
                                                                        bi-ticket-perforated</c:when>
                                                                    <c:when test="${n.type == 'VOUCHER_OUT_OF_STOCK'}">
                                                                        bi bi-exclamation-triangle</c:when>
                                                                    <c:when test="${n.type == 'VOUCHER_EXPIRED'}">bi
                                                                        bi-calendar-x</c:when>
                                                                    <c:when test="${n.type == 'PRODUCT_ACTIVE'}">bi
                                                                        bi-award</c:when>
                                                                    <c:when test="${n.type == 'PRODUCT_REJECTED'}">bi
                                                                        bi-slash-circle</c:when>
                                                                    <c:otherwise>bi bi-bell</c:otherwise>
                                                                </c:choose>
                                                            </c:set>


                                                            <!-- Form bao quanh thông báo -->
                                                            <a href="${ctx}${n.link}"
                                                                class="activity-item btn w-100 text-start border-0 bg-transparent p-0">
                                                                <div class="activity-icon ${iconClass}">
                                                                    <i class="${biIcon}"></i>
                                                                </div>
                                                                <div class="activity-content">
                                                                    <div class="activity-title fw-semibold">${n.title}
                                                                    </div>
                                                                    <div class="activity-message text-muted small">
                                                                        ${n.message}</div>
                                                                    <div class="activity-time text-secondary small">
                                                                        ${n.timeAgo}</div>
                                                                </div>
                                                            </a>
                                                        </c:forEach>

                                                        <c:if test="${empty notifications}">
                                                            <p class="text-muted text-center">Chưa có hoạt động nào gần
                                                                đây</p>
                                                        </c:if>

                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                    </div>
                                </div>
                            </main>
                            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp?v=1.0.1" />
                        </div>
                    </div>
                    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
                    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

                    <c:if test="${not empty revenueData}">
                        <script>
                            const revenueLabels = [
                                <c:forEach var="r" items="${revenueData}" varStatus="loop">
                                    "${r.date}"<c:if test="${!loop.last}">,</c:if>
                                </c:forEach>
                            ];
                            const revenueValues = [
                                <c:forEach var="r" items="${revenueData}" varStatus="loop">
                                    ${r.revenue}<c:if test="${!loop.last}">,</c:if>
                                </c:forEach>
                            ];

                            document.addEventListener("DOMContentLoaded", function () {
                                const ctx = document.getElementById('revenueChart').getContext('2d');
                                new Chart(ctx, {
                                    type: 'bar',
                                    data: {
                                        labels: revenueLabels,
                                        datasets: [{
                                            label: 'Doanh thu (₫)',
                                            data: revenueValues,
                                            backgroundColor: '#164e3f',
                                            borderColor: '#164e3f',
                                            borderWidth: 1
                                        }]
                                    },
                                    options: {
                                        responsive: true,
                                        scales: {
                                            y: {
                                                beginAtZero: true,
                                                ticks: {
                                                    callback: function (value) {
                                                        return value.toLocaleString('vi-VN') + ' ₫';
                                                    }
                                                }
                                            }
                                        },
                                        plugins: {
                                            legend: { display: false },
                                            tooltip: {
                                                callbacks: {
                                                    label: function (context) {
                                                        return context.formattedValue + ' ₫';
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            });
                        </script>
                    </c:if>

                    <script src="${ctx}/assets/js/shop/shopDashboard.js"></script>
                </body>

                </html>