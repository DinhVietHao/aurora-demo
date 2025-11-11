<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

                <c:set var="pageTitle" value="Chi tiết Flash Sale Item" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/flashSale.css" />
                </head>

                <body class="sb-nav-fixed" data-ctx="${ctx}" data-page="shop-flashsale-detail">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />

                        <div id="layoutSidenav_content">
                            <main class="container-fluid px-4 mt-4">
                                <!-- Thông báo -->
                                <c:if test="${not empty errorMessage}">
                                    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                                        ${fn:escapeXml(errorMessage)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>

                                <c:if test="${not empty successMessage}">
                                    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                                        ${fn:escapeXml(successMessage)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>

                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <h2 class="fw-bold text-success">
                                        <i class="bi bi-lightning-charge-fill me-2"></i>Chi tiết Flash Sale Item
                                    </h2>
                                    <button type="button" class="btn btn-outline-secondary" onclick="history.back()">
                                        <i class="bi bi-arrow-left me-1"></i> Quay lại
                                    </button>
                                </div>

                                <c:if test="${not empty item}">
                                    <!-- Thông tin sản phẩm -->
                                    <div class="card shadow-sm mb-4">
                                        <div class="card-body">
                                            <div class="row g-4 align-items-center">
                                                <div class="col-md-3 text-center">
                                                    <img src="${ctx}/assets/images/catalog/products/${item.imageUrl}"
                                                        alt="${item.title}" class="rounded border"
                                                        style="width: 180px; height: 180px; object-fit: cover;">
                                                </div>
                                                <div class="col-md-9">
                                                    <h4 class="fw-bold mb-1">${item.title}</h4>
                                                    <p class="text-muted mb-3">Mã sản phẩm: ${item.productID}</p>

                                                    <div class="row mb-2">
                                                        <div class="col-md-6">
                                                            <strong>Giá gốc:</strong>
                                                            <span class="text-decoration-line-through text-muted">
                                                                <fmt:formatNumber value="${item.originalPrice}"
                                                                    type="number" pattern="#,##0" /> VND
                                                            </span>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <strong>Giá Flash Sale:</strong>
                                                            <span class="text-danger fw-semibold">
                                                                <fmt:formatNumber value="${item.flashPrice}"
                                                                    type="number" pattern="#,##0" /> VND
                                                            </span>
                                                        </div>
                                                    </div>

                                                    <div class="row mb-2">
                                                        <div class="col-md-6">
                                                            <strong>Số lượng:</strong> ${item.fsStock}
                                                        </div>
                                                        <div class="col-md-6">
                                                            <strong>Đã bán:</strong> ${item.soldCount}
                                                        </div>
                                                    </div>

                                                    <div class="mb-2">
                                                        <strong>Trạng thái duyệt:</strong>
                                                        <c:choose>
                                                            <c:when test="${item.approvalStatus == 'PENDING'}">
                                                                <span class="badge bg-warning text-dark">Chờ
                                                                    duyệt</span>
                                                            </c:when>
                                                            <c:when test="${item.approvalStatus == 'APPROVED'}">
                                                                <span class="badge bg-success">Đã duyệt</span>
                                                            </c:when>
                                                            <c:when test="${item.approvalStatus == 'REJECTED'}">
                                                                <span class="badge bg-danger">Từ chối</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span
                                                                    class="badge bg-secondary">${item.approvalStatus}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>

                                                    <!-- Thanh tiến độ -->
                                                    <div class="mt-3">
                                                        <c:set var="totalStock"
                                                            value="${item.soldCount + item.fsStock}" />
                                                        <c:set var="progress"
                                                            value="${(item.soldCount * 100.0) / (totalStock > 0 ? totalStock : 1)}" />

                                                        <div class="progress" style="height: 20px; position: relative;">
                                                            <div class="progress-bar bg-success" role="progressbar"
                                                                style="width: ${progress}%; transition: width 0.6s;">
                                                            </div>
                                                            <div class="position-absolute w-100 text-center"
                                                                style="top: 0; left: 0; height: 100%; line-height: 20px; font-size: 0.9rem;">
                                                                <fmt:formatNumber value="${progress}"
                                                                    maxFractionDigits="1" />%
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Thống kê doanh thu -->
                                    <div class="card shadow-sm">
                                        <div class="card-header bg-light fw-semibold">
                                            <i class="bi bi-bar-chart-line me-2"></i>Doanh thu theo ngày
                                        </div>
                                        <div class="card-body text-center">
                                            <canvas id="revenueChart" height="120" style="display: none;"></canvas>
                                            <div id="noDataMessage" class="text-muted py-4" style="display: none;">
                                                <i class="bi bi-graph-up-arrow me-2"></i>Chưa có dữ liệu doanh thu trong
                                                khoảng thời gian này
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </main>
                        </div>
                    </div>

                    <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
                    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                    <script>
                        const ctxChart = document.getElementById("revenueChart");
                        const noDataMsg = document.getElementById("noDataMessage");

                        // ✅ Giả sử backend render sẵn JSON labels và data
                        // const labels = ${revenueLabelsJson != null ? revenueLabelsJson : "[]"};
                        // const data = ${revenueValuesJson != null ? revenueValuesJson : "[]"};

                        console.log("Labels:", labels);
                        console.log("Data:", data);

                        if (ctxChart && labels.length > 0 && data.length > 0) {
                            ctxChart.style.display = "block";
                            noDataMsg.style.display = "none";

                            const chartType = data.length === 1 ? 'bar' : 'line';

                            new Chart(ctxChart, {
                                type: chartType,
                                data: {
                                    labels: labels,
                                    datasets: [{
                                        label: 'Doanh thu (VND)',
                                        data: data,
                                        borderWidth: 2,
                                        borderColor: '#164e3f',
                                        backgroundColor: chartType === 'bar'
                                            ? 'rgba(22, 78, 63, 0.5)'
                                            : 'rgba(22, 78, 63, 0.15)',
                                        fill: true,
                                        tension: 0.3,
                                        pointRadius: 4,
                                        pointBackgroundColor: '#198754'
                                    }]
                                },
                                options: {
                                    scales: {
                                        y: {
                                            beginAtZero: true,
                                            ticks: {
                                                callback: function (value) {
                                                    return value.toLocaleString('vi-VN');
                                                }
                                            }
                                        }
                                    },
                                    plugins: {
                                        legend: { display: false },
                                        tooltip: {
                                            callbacks: {
                                                label: function (context) {
                                                    return context.parsed.y.toLocaleString('vi-VN') + ' VND';
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            ctxChart.style.display = "none";
                            noDataMsg.style.display = "block";
                        }
                    </script>
                </body>

                </html>