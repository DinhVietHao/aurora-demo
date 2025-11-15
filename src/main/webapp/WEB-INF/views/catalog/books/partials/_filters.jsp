<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <c:set var="ctx" value="${pageContext.request.contextPath}" />

            <div class="filter-sidebar">
                <!-- Filter Header -->
                <div class="filter-header">
                    <h5 class="filter-header-title">
                        <i class="bi bi-funnel"></i>
                        Bộ lọc
                    </h5>
                    <button type="button" class="btn-reset-filter" id="resetFilterBtn">
                        <i class="bi bi-arrow-clockwise"></i>
                        Đặt lại
                    </button>
                </div>

                <form action="${ctx}/home" method="GET" id="filterForm" class="filter-form">
                    <input type="hidden" name="action" value="filter" />

                    <!-- Price Range Filter -->
                    <div class="filter-section">
                        <h6 class="filter-section-title">
                            <i class="bi bi-currency-dollar"></i>
                            Khoảng giá
                        </h6>

                        <!-- Price Display -->
                        <div class="price-display-group">
                            <div class="price-display">
                                <span class="price-label">Từ:</span>
                                <span id="minPriceDisplay" class="price-value">
                                    <fmt:formatNumber value="${minPriceDB}" type="number" maxFractionDigits="0" />đ
                                </span>
                            </div>
                            <div class="price-display">
                                <span class="price-label">Đến:</span>
                                <span id="maxPriceDisplay" class="price-value">
                                    <fmt:formatNumber value="${maxPriceDB}" type="number" maxFractionDigits="0" />đ
                                </span>
                            </div>
                        </div>

                        <!-- Dual Range Slider -->
                        <div class="price-slider-wrapper">
                            <div class="price-slider-track">
                                <div id="priceSliderRange" class="price-slider-range"></div>
                            </div>

                            <input type="range" id="minPriceSlider" class="price-slider price-slider-min"
                                min="${minPriceDB}" max="${maxPriceDB}"
                                value="${empty param.minPrice ? minPriceDB : param.minPrice}" step="10000">

                            <input type="range" id="maxPriceSlider" class="price-slider price-slider-max"
                                min="${minPriceDB}" max="${maxPriceDB}"
                                value="${empty param.maxPrice ? maxPriceDB : param.maxPrice}" step="10000">
                        </div>

                        <!-- Hidden inputs for form submit -->
                        <input type="hidden" id="minPriceInput" name="minPrice" value="${param.minPrice}">
                        <input type="hidden" id="maxPriceInput" name="maxPrice" value="${param.maxPrice}">
                    </div>

                    <!-- Category Filter -->
                    <div class="filter-section">
                        <h6 class="filter-section-title">
                            <i class="bi bi-bookmarks"></i>
                            Thể loại
                        </h6>
                        <select class="form-select filter-select" name="category" id="categorySelect">
                            <option value="">Tất cả thể loại</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat}" ${param.category==cat ? 'selected' : '' }>
                                    <c:out value="${cat}" />
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty param.category}">
                            <span class="filter-active-badge">
                                <i class="bi bi-check-circle-fill"></i>
                                Đang lọc
                            </span>
                        </c:if>
                    </div>

                    <!-- Author Filter -->
                    <div class="filter-section">
                        <h6 class="filter-section-title">
                            <i class="bi bi-person"></i>
                            Tác giả
                        </h6>
                        <select class="form-select filter-select" name="author" id="authorSelect">
                            <option value="">Tất cả tác giả</option>
                            <c:forEach var="auth" items="${authors}">
                                <option value="${auth}" ${param.author==auth ? 'selected' : '' }>
                                    <c:out value="${auth}" />
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty param.author}">
                            <span class="filter-active-badge">
                                <i class="bi bi-check-circle-fill"></i>
                                Đang lọc
                            </span>
                        </c:if>
                    </div>

                    <!-- Publisher Filter -->
                    <div class="filter-section">
                        <h6 class="filter-section-title">
                            <i class="bi bi-building"></i>
                            Nhà xuất bản
                        </h6>
                        <select class="form-select filter-select" name="publisher" id="publisherSelect">
                            <option value="">Tất cả NXB</option>
                            <c:forEach var="pub" items="${publishers}">
                                <option value="${pub}" ${param.publisher==pub ? 'selected' : '' }>
                                    <c:out value="${pub}" />
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty param.publisher}">
                            <span class="filter-active-badge">
                                <i class="bi bi-check-circle-fill"></i>
                                Đang lọc
                            </span>
                        </c:if>
                    </div>

                    <!-- Language Filter -->
                    <div class="filter-section">
                        <h6 class="filter-section-title">
                            <i class="bi bi-translate"></i>
                            Ngôn ngữ
                        </h6>
                        <select class="form-select filter-select" name="language" id="languageSelect">
                            <option value="">Tất cả ngôn ngữ</option>
                            <c:forEach var="lang" items="${languages}">
                                <option value="${lang}" ${param.language==lang ? 'selected' : '' }>
                                    <c:out value="${lang}" />
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty param.language}">
                            <span class="filter-active-badge">
                                <i class="bi bi-check-circle-fill"></i>
                                Đang lọc
                            </span>
                        </c:if>
                    </div>

                    <!-- Apply Button -->
                    <div class="filter-actions">
                        <button type="submit" class="btn-apply-filter">
                            <i class="bi bi-check-lg"></i>
                            Áp dụng bộ lọc
                        </button>
                    </div>
                </form>
            </div>

            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    const minSlider = document.getElementById('minPriceSlider');
                    const maxSlider = document.getElementById('maxPriceSlider');
                    const minDisplay = document.getElementById('minPriceDisplay');
                    const maxDisplay = document.getElementById('maxPriceDisplay');
                    const minInput = document.getElementById('minPriceInput');
                    const maxInput = document.getElementById('maxPriceInput');
                    const sliderRange = document.getElementById('priceSliderRange');
                    const resetBtn = document.getElementById('resetFilterBtn');
                    const form = document.getElementById('filterForm');

                    if (!minSlider || !maxSlider) return;

                    const minPrice = parseInt(minSlider.min);
                    const maxPrice = parseInt(minSlider.max);

                    // Format currency VND
                    function formatCurrency(value) {
                        return new Intl.NumberFormat('vi-VN', {
                            maximumFractionDigits: 0
                        }).format(value) + 'đ';
                    }

                    // Update slider range visual
                    function updateSliderRange() {
                        const minVal = parseInt(minSlider.value);
                        const maxVal = parseInt(maxSlider.value);

                        const percentMin = ((minVal - minPrice) / (maxPrice - minPrice)) * 100;
                        const percentMax = ((maxVal - minPrice) / (maxPrice - minPrice)) * 100;

                        sliderRange.style.left = percentMin + '%';
                        sliderRange.style.right = (100 - percentMax) + '%';
                    }

                    // Update price displays and hidden inputs
                    function updatePriceDisplays() {
                        let minVal = parseInt(minSlider.value);
                        let maxVal = parseInt(maxSlider.value);

                        // Prevent overlap
                        if (minVal > maxVal - 10000) {
                            if (this === minSlider) {
                                minVal = maxVal - 10000;
                                minSlider.value = minVal;
                            } else {
                                maxVal = minVal + 10000;
                                maxSlider.value = maxVal;
                            }
                        }

                        // Update displays
                        minDisplay.textContent = formatCurrency(minVal);
                        maxDisplay.textContent = formatCurrency(maxVal);

                        // Update hidden inputs
                        minInput.value = minVal;
                        maxInput.value = maxVal;

                        // Update visual range
                        updateSliderRange();
                    }

                    // Event listeners for sliders
                    minSlider.addEventListener('input', updatePriceDisplays);
                    maxSlider.addEventListener('input', updatePriceDisplays);

                    // Reset filter button
                    resetBtn.addEventListener('click', function () {
                        window.location.href = '${ctx}/home?action=bookstore';
                    });

                    // Auto-submit on select change (optional)
                    const selects = form.querySelectorAll('select');
                    selects.forEach(select => {
                        select.addEventListener('change', function () {
                            form.submit();
                        });
                    });

                    // Initialize
                    updatePriceDisplays();
                });
            </script>