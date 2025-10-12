
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Aurora Bookstore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.css">
    
    <jsp:include page="/WEB-INF/views/layouts/_head_admin.jsp" />

</head>
<body class="sb-nav-fixed">
    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />
    
    <div id="layoutSidenav">
        
        <jsp:include page="/WEB-INF/views/layouts/_sidebar_admin.jsp" />

        <div id="layoutSidenav_content">
            <main>
                <div class="container-fluid px-4">
                    <!-- Page Header -->
                    <div class="d-flex justify-content-between align-items-center flex-wrap">
                        <div class="d-flex align-items-center">
                            <button id="sidebarToggle" class="btn btn-outline-secondary btn-sm me-3 d-lg-none" type="button" aria-label="Toggle sidebar">
                                <i class="bi bi-list"></i>
                            </button>
                            <h1 class="mt-4 dashboard-title">Tổng quan cửa hàng</h1>
                        </div>
                        <div class="d-flex align-items-center mt-4">
                            <span class="me-3 text-muted d-none d-md-inline">Xin chào, Admin</span>
                            <div class="dropdown">
                                <button class="btn btn-outline-secondary btn-sm dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                    <i class="bi bi-bell"></i>
                                    <c:if test="${pendingOrders > 0}">
                                        <span class="badge bg-danger rounded-pill">${pendingOrders}</span>
                                    </c:if>
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li><h6 class="dropdown-header">Thông báo</h6></li>
                                    <c:if test="${pendingOrders > 0}">
                                        <li><a class="dropdown-item" href="<c:url value='/admin/orders?status=pending'/>">
                                            <i class="bi bi-cart me-1"></i> ${pendingOrders} đơn hàng đang chờ xử lý
                                        </a></li>
                                    </c:if>
                                    <c:if test="${lowStockProducts > 0}">
                                        <li><a class="dropdown-item" href="<c:url value='/admin/products?lowStock=true'/>">
                                            <i class="bi bi-exclamation-triangle me-1"></i> ${lowStockProducts} sản phẩm sắp hết hàng
                                        </a></li>
                                    </c:if>
                                </ul>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Stats Cards Row -->
                    <div class="row mt-4">
                        <div class="col-xl-3 col-md-6">
                            <div class="card stats-card stats-card-primary mb-4">
                                <div class="card-body">
                                    <div class="d-flex align-items-center">
                                        <div class="stats-icon">
                                            <i class="bi bi-graph-up"></i>
                                        </div>
                                        <div class="ms-3">
                                            <div class="stats-label">Doanh thu</div>
                                            <div class="stats-value">
                                                <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                            </div>
                                            <div class="stats-change ${revenueChangePercent >= 0 ? 'positive' : 'negative'}">
                                                <fmt:formatNumber value="${revenueChangePercent}" pattern="0.0"/>%
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-xl-3 col-md-6">
                            <div class="card stats-card stats-card-warning mb-4">
                                <div class="card-body">
                                    <div class="d-flex align-items-center">
                                        <div class="stats-icon">
                                            <i class="bi bi-cart-check"></i>
                                        </div>
                                        <div class="ms-3">
                                            <div class="stats-label">Đơn hàng</div>
                                            <div class="stats-value">${totalOrders}</div>
                                            <div class="stats-change ${orderChangePercent >= 0 ? 'positive' : 'negative'}">
                                                <fmt:formatNumber value="${orderChangePercent}" pattern="0.0"/>%
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-xl-3 col-md-6">
                            <div class="card stats-card stats-card-success mb-4">
                                <div class="card-body">
                                    <div class="d-flex align-items-center">
                                        <div class="stats-icon">
                                            <i class="bi bi-book"></i>
                                        </div>
                                        <div class="ms-3">
                                            <div class="stats-label">Sản phẩm</div>
                                            <div class="stats-value">${totalProducts}</div>
                                            <div class="stats-change ${productChangePercent >= 0 ? 'positive' : 'negative'}">
                                                <fmt:formatNumber value="${productChangePercent}" pattern="0.0"/>%
                                            </div>
                                        </div>
                                    </div>
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
                                            <div class="stats-label">Đánh giá TB</div>
                                            <div class="stats-value">
                                                <fmt:formatNumber value="${avgRating}" pattern="0.0"/>/<span class="small">5</span>
                                            </div>
                                            <div class="stats-change neutral">
                                                <i class="bi bi-star-fill text-warning"></i>
                                                <i class="bi bi-star-fill text-warning"></i>
                                                <i class="bi bi-star-fill text-warning"></i>
                                                <i class="bi bi-star-fill text-warning"></i>
                                                <i class="bi bi-star-half text-warning"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Additional Stats Row -->
                    <div class="row">
                        <div class="col-xl-6 col-md-12 mb-4">
                            <div class="card h-100">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <div>
                                        <i class="bi bi-bar-chart-line me-1"></i>
                                        Tổng quan đơn hàng
                                    </div>
                                    <div class="small text-muted">Cập nhật gần đây</div>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-4 text-center border-end">
                                            <div class="display-6">${totalOrders}</div>
                                            <div class="small text-muted">Tổng đơn hàng</div>
                                        </div>
                                        <div class="col-4 text-center border-end">
                                            <div class="display-6">${pendingOrders}</div>
                                            <div class="small text-muted">Đang chờ xử lý</div>
                                        </div>
                                        <div class="col-4 text-center">
                                            <div class="display-6">${newOrdersToday}</div>
                                            <div class="small text-muted">Đơn hàng mới</div>
                                        </div>
                                    </div>
                                    <div class="mt-4">
                                        <a href="<c:url value='/admin/orders'/>" class="btn btn-sm btn-outline-primary">Quản lý đơn hàng</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-xl-6 col-md-12 mb-4">
                            <div class="card h-100">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <div>
                                        <i class="bi bi-book me-1"></i>
                                        Tổng quan sản phẩm
                                    </div>
                                    <div class="small text-muted">Cập nhật gần đây</div>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-4 text-center border-end">
                                            <div class="display-6">${totalProducts}</div>
                                            <div class="small text-muted">Tổng sản phẩm</div>
                                        </div>
                                        <div class="col-4 text-center border-end">
                                            <div class="display-6">${lowStockProducts}</div>
                                            <div class="small text-muted">Sắp hết hàng</div>
                                        </div>
                                        <div class="col-4 text-center">
                                            <div class="display-6 text-success">
                                                <i class="bi bi-plus-circle"></i>
                                            </div>
                                            <div class="small text-muted">Thêm sản phẩm</div>
                                        </div>
                                    </div>
                                    <div class="mt-4">
                                        <a href="<c:url value='/admin/products'/>" class="btn btn-sm btn-outline-success">Quản lý sản phẩm</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Charts and Activity Row -->
                    <div class="row">
                        <div class="col-xl-8">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <div>
                                        <i class="bi bi-bar-chart me-1"></i>
                                        Doanh thu 7 ngày qua
                                    </div>
                                    <div class="small text-muted">
                                        <fmt:formatNumber value="${revenue7Days}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                    </div>
                                </div>
                                <div class="card-body">
                                    <canvas id="revenueChart" width="100%" height="40"></canvas>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-xl-4">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <div>
                                        <i class="bi bi-clock-history me-1"></i>
                                        Hoạt động gần đây
                                    </div>
                                    <a href="#" class="small text-muted">Xem tất cả</a>
                                </div>
                                <div class="card-body">
                                    <div class="activity-list">
                                        <c:forEach var="activity" items="${recentActivities}">
                                            <div class="activity-item">
                                                <c:choose>
                                                    <c:when test="${activity.type eq 'order'}">
                                                        <div class="activity-icon activity-icon-success">
                                                            <i class="bi bi-cart-plus"></i>
                                                        </div>
                                                        <div class="activity-content">
                                                            <div class="activity-title">
                                                                <a href="<c:url value='/admin/orders/detail?id=${activity.id}'/>">
                                                                    Đơn hàng mới #${activity.id}
                                                                </a>
                                                            </div>
                                                            <div class="activity-details">
                                                                ${activity.name} - <fmt:formatNumber value="${activity.value}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                                            </div>
                                                            <div class="activity-time small text-muted">
                                                                <fmt:formatDate value="${activity.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                            </div>
                                                        </div>
                                                    </c:when>
                                                    <c:when test="${activity.type eq 'review'}">
                                                        <div class="activity-icon activity-icon-warning">
                                                            <i class="bi bi-star"></i>
                                                        </div>
                                                        <div class="activity-content">
                                                            <div class="activity-title">
                                                                Đánh giá "${activity.productName}"
                                                            </div>
                                                            <div class="activity-details">
                                                                ${activity.name} - 
                                                                <c:forEach begin="1" end="${activity.value}">
                                                                    <i class="bi bi-star-fill text-warning small"></i>
                                                                </c:forEach>
                                                            </div>
                                                            <div class="activity-time small text-muted">
                                                                <fmt:formatDate value="${activity.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                            </div>
                                                        </div>
                                                    </c:when>
                                                </c:choose>
                                            </div>
                                        </c:forEach>
                                        
                                        <c:if test="${empty recentActivities}">
                                            <div class="text-center text-muted py-3">
                                                <i class="bi bi-hourglass display-6"></i>
                                                <p>Không có hoạt động gần đây</p>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
    <script>
    // Chart initialization
    document.addEventListener('DOMContentLoaded', function() {
        // Extract data from JSP
        const dailyRevenue = {
            <c:forEach var="entry" items="${dailyRevenue}" varStatus="status">
                "${entry.key}": ${entry.value}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        };
        
        // Sort keys chronologically
        const sortedDates = Object.keys(dailyRevenue).sort();
        const labels = sortedDates.map(date => {
            const parts = date.split('-');
            return `${parts[2]}/${parts[1]}`;  // Format as DD/MM
        });
        const values = sortedDates.map(date => dailyRevenue[date]);
        
        // Initialize chart
        const ctx = document.getElementById('revenueChart');
        const revenueChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Doanh thu (đ)',
                    data: values,
                    backgroundColor: 'rgba(54, 162, 235, 0.5)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return new Intl.NumberFormat('vi-VN').format(value) + 'đ';
                            }
                        }
                    }
                },
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return new Intl.NumberFormat('vi-VN').format(context.raw) + 'đ';
                            }
                        }
                    }
                }
            }
        });
    });
    </script>
</body>
</html>
