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
        </head>

        <body>
          <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

          <main>
            <!-- Banner -->
            <div class="container banner">
              <div id="carouselExample" class="carousel slide banner-book" data-bs-ride="carousel">
                <div class="carousel-inner">
                  <div class="carousel-item active">
                    <img src="${ctx}/assets/images/catalog/banners/banner-book.png" class="d-block w-100 banner-image"
                      alt="..." />
                    <div class="container">
                      <div class="row align-items-center banner-content">
                        <div class="col-lg-6 col-md-12 text-white px-4">
                          <h1 class="fw-bold">Nhà sách Aurora</h1>
                          <h1 class="banner-title">Sách hay mê ly</h1>
                          <p>
                            Khám phá hàng ngàn cuốn sách thuộc mọi thể loại. Từ những cuốn sách bán chạy
                            nhất đến những viên ngọc ẩn, hãy tìm cuốn sách hoàn hảo của bạn với mức giá
                            không thể cạnh tranh hơn.
                          </p>
                          <a href="${ctx}/book" class="button">Mua ngay</a>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="carousel-item">
                    <img src="${ctx}/assets/images/catalog/banners/banner-book.png" class="d-block w-100" alt="..." />
                    <div class="container">
                      <div class="row align-items-center banner-content">
                        <div class="col-lg-6 col-md-12 text-white">
                          <h1 class="fw-bold">Nhà sách Book Zone</h1>
                          <h1 class="banner-title">Sách hay mê ly</h1>
                          <p>
                            Khám phá hàng ngàn cuốn sách thuộc mọi thể loại. Từ những cuốn sách bán chạy
                            nhất đến những viên ngọc ẩn, hãy tìm cuốn sách hoàn hảo của bạn với mức giá
                            không thể cạnh tranh hơn.
                          </p>
                          <a href="${ctx}/book" class="button">Mua ngay</a>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="carousel-item">
                    <img src="${ctx}/assets/images/catalog/banners/banner-book.png" class="d-block w-100" alt="..." />
                    <div class="container">
                      <div class="row align-items-center banner-content">
                        <div class="col-lg-6 col-md-12 text-white">
                          <h1 class="fw-bold">Nhà sách Book Zone</h1>
                          <h1 class="banner-title">Sách hay mê ly</h1>
                          <p>
                            Khám phá hàng ngàn cuốn sách thuộc mọi thể loại. Từ những cuốn sách bán chạy
                            nhất đến những viên ngọc ẩn, hãy tìm cuốn sách hoàn hảo của bạn với mức giá
                            không thể cạnh tranh hơn.
                          </p>
                          <a href="${ctx}/book" class="button">Mua ngay</a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <button class="carousel-control-prev" type="button" data-bs-target="#carouselExample"
                  data-bs-slide="prev">
                  <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                  <span class="visually-hidden">Previous</span>
                </button>
                <button class="carousel-control-next" type="button" data-bs-target="#carouselExample"
                  data-bs-slide="next">
                  <span class="carousel-control-next-icon icon-next" aria-hidden="true"></span>
                  <span class="visually-hidden">Next</span>
                </button>
              </div>

              <!-- Ads -->
              <div class="banner-advertisemet row my-4">
                <div class="col-6 col-md-4 col-lg-3">
                  <a><img src="${ctx}/assets/images/catalog/banners/advertisement-1.png" alt="advertisement" /></a>
                </div>
                <div class="col-6 col-md-4 col-lg-3">
                  <a><img src="${ctx}/assets/images/catalog/banners/advertisement-2.png" alt="advertisement" /></a>
                </div>
                <div class="col-6 col-md-4 col-lg-3">
                  <a><img src="${ctx}/assets/images/catalog/banners/advertisement-3.png" alt="advertisement" /></a>
                </div>
                <div class="col-6 col-md-4 col-lg-3">
                  <a><img src="${ctx}/assets/images/catalog/banners/advertisement-4.png" alt="advertisement" /></a>
                </div>
              </div>
            </div>

            <!-- Gợi ý cho bạn -->
            <c:if test="${not empty suggestedProducts}">
              <div class="suggest container">
                <h5 class="suggest-title"><i class="bi bi-lightbulb"></i> Gợi ý cho bạn</h5>

                <div id="bookSuggest" class="carousel slide" data-bs-ride="carousel" data-bs-interval="3000">
                  <div class="carousel-inner">
                    <c:set var="chunkSize" value="6" />
                    <c:forEach var="i" begin="0" end="${fn:length(suggestedProducts) - 1}" step="${chunkSize}"
                      varStatus="chunkStatus">
                      <div class="carousel-item ${chunkStatus.first ? 'active' : ''}">
                        <div class="row g-3 product">
                          <c:forEach var="j" begin="${i}" end="${i + chunkSize - 1}">
                            <c:if test="${j < fn:length(suggestedProducts)}">
                              <c:set var="p" value="${suggestedProducts[j]}" />
                              <div class="col-6 col-md-4 col-lg-2">
                                <a href="${ctx}/home?action=detail&id=${p.productId}">
                                  <div class="product-card">
                                    <div class="product-img">
                                      <c:if
                                        test="${p.originalPrice != null && p.salePrice != null && p.originalPrice > p.salePrice}">
                                        <span class="discount">
                                          -
                                          <fmt:formatNumber value="${p.discountPercent}" maxFractionDigits="0" />%
                                        </span>
                                      </c:if>
                                      <img
                                        src="http://localhost:8080/assets/images/catalog/products/${p.primaryImageUrl}"
                                        alt="${p.title}" />
                                    </div>
                                    <div class="product-body">
                                      <h6 class="price">
                                        <c:choose>
                                          <c:when
                                            test="${p.salePrice != null && p.originalPrice != null && p.salePrice < p.originalPrice}">
                                            <fmt:formatNumber value="${p.salePrice}" type="currency" currencySymbol="đ"
                                              maxFractionDigits="0" />
                                            <span class="text-muted text-decoration-line-through ms-2">
                                              <fmt:formatNumber value="${p.originalPrice}" type="currency"
                                                currencySymbol="đ" maxFractionDigits="0" />
                                            </span>
                                          </c:when>
                                          <c:otherwise>
                                            <fmt:formatNumber value="${p.originalPrice}" type="currency"
                                              currencySymbol="đ" maxFractionDigits="0" />
                                          </c:otherwise>
                                        </c:choose>
                                      </h6>
                                      <small class="author">${p.publisher.name}</small>
                                      <p class="title">${p.title}</p>
                                      <div class="rating">
                                        <c:forEach begin="1" end="5" var="k">
                                          <c:choose>
                                            <c:when test="${k <= p.avgRating}">
                                              <i class="bi bi-star-fill text-warning small"></i>
                                            </c:when>
                                            <c:when test="${k - p.avgRating <= 0.5}">
                                              <i class="bi bi-star-half text-warning small"></i>
                                            </c:when>
                                            <c:otherwise>
                                              <i class="bi bi-star text-warning small"></i>
                                            </c:otherwise>
                                          </c:choose>
                                        </c:forEach>
                                        <span>Đã bán ${p.soldCount}</span>
                                      </div>
                                    </div>
                                  </div>
                                </a>
                              </div>
                            </c:if>
                          </c:forEach>
                        </div>
                      </div>
                    </c:forEach>
                  </div>

                  <!-- Chỉ hiển thị nút điều hướng nếu có hơn 1 slide (>6 sản phẩm) -->
                  <c:if test="${fn:length(suggestedProducts) > 6}">
                    <button class="carousel-control-prev" type="button" data-bs-target="#bookSuggest"
                      data-bs-slide="prev">
                      <span class="carousel-control-prev-icon"></span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#bookSuggest"
                      data-bs-slide="next">
                      <span class="carousel-control-next-icon"></span>
                    </button>
                  </c:if>
                </div>
              </div>
            </c:if>

            <!-- FLASH SALES -->
            <c:if test="${not empty flashSaleProducts}">
              <div class="container my-4 flash-sale">
                <div class="text-center mb-4">
                  <h4 class="flash-sale-title">🔥 Flash Sale 🔥</h4>
                  <p class="text-danger fw-bold">
                    <i class="bi bi-clock"></i> Ends in:
                    <span class="flash-sale-time-hours">00</span>h
                    <span class="flash-sale-time-minutes">00</span>m
                    <span class="flash-sale-time-seconds">00</span>s
                  </p>
                </div>

                <div id="flashSaleTimerData"
                  data-end-at="<fmt:formatDate value='${flashSaleEndAt}' pattern='yyyy-MM-dd HH:mm:ss' timeZone='UTC'/>"
                  data-server-time="${currentServerTime}" style="display: none;">
                </div>

                <!-- Carousel Flash Sale -->
                <div id="flashSaleCarousel" class="carousel slide" data-bs-ride="carousel" data-bs-interval="1500">
                  <div class="carousel-inner" id="flashSaleCarouselInner">
                    <c:set var="itemsPerSlide" value="6" />
                    <c:set var="totalProducts" value="${fn:length(flashSaleProducts)}" />

                    <c:forEach var="slideIndex" begin="0" end="${(totalProducts - 1) / itemsPerSlide}"
                      varStatus="status">
                      <c:set var="startIdx" value="${slideIndex * itemsPerSlide}" />
                      <c:set var="endIdx" value="${startIdx + itemsPerSlide - 1}" />

                      <div class="carousel-item ${status.first ? 'active' : ''}">
                        <div class="row g-3 product">
                          <c:forEach var="idx" begin="${startIdx}" end="${endIdx}">
                            <c:if test="${idx < totalProducts}">
                              <c:set var="item" value="${flashSaleProducts[idx]}" />
                              <c:set var="remaining" value="${item.fsStock - item.soldCount}" />
                              <c:set var="isScarce" value="${item.soldPercent >= 80}" />
                              <c:set var="isSoldOut" value="${remaining <= 0}" />

                              <div class="col-6 col-md-4 col-lg-2">
                                <a href="${ctx}/home?action=detail&id=${item.productId}" class="product-card-link"
                                  style="text-decoration: none; color: inherit;">
                                  <div class="product-card position-relative flash-sale-card">

                                    <!-- Badge "Sắp cháy hàng" nếu sold >= 80% -->
                                    <c:if test="${isScarce}">
                                      <div class="position-absolute top-0 end-0 p-2">
                                        <span class="badge bg-danger" style="font-size: 0.75rem;">
                                          Sắp hết hàng!
                                        </span>
                                      </div>
                                    </c:if>

                                    <!-- Product Image & Discount Badge -->
                                    <div class="product-img">
                                      <span class="discount">-${item.discountPercent}%</span>
                                      <img src="http://localhost:8080/assets/images/catalog/products/${item.imageUrl}"
                                        alt="${item.title}" style="object-fit: cover; width: 100%; height: 140px;" />
                                    </div>

                                    <!-- Product Body -->
                                    <div class="product-body">
                                      <h6 class="price">
                                        <fmt:formatNumber value="${item.flashPrice}" type="currency" currencySymbol="đ"
                                          maxFractionDigits="0" />
                                        <span class="text-muted text-decoration-line-through ms-2"
                                          style="font-size: 0.85rem;">
                                          <fmt:formatNumber value="${item.originalPrice}" type="currency"
                                            currencySymbol="đ" maxFractionDigits="0" />
                                        </span>
                                      </h6>
                                      <small class="author">${item.publisherName}</small>
                                      <p class="title" style="font-size: 0.9rem; min-height: 2.5em; overflow: hidden;">
                                        ${item.title}
                                      </p>

                                      <!-- Rating -->
                                      <div class="rating mb-2">
                                        <c:forEach begin="1" end="5" var="k">
                                          <c:choose>
                                            <c:when test="${k <= item.avgRating}">
                                              <i class="bi bi-star-fill text-warning small"></i>
                                            </c:when>
                                            <c:when test="${k - item.avgRating <= 0.5}">
                                              <i class="bi bi-star-half text-warning small"></i>
                                            </c:when>
                                            <c:otherwise>
                                              <i class="bi bi-star text-warning small"></i>
                                            </c:otherwise>
                                          </c:choose>
                                        </c:forEach>
                                      </div>

                                      <!-- Progress Bar - Stock Status -->
                                      <div class="mb-2">
                                        <div class="progress" style="height: 6px;">
                                          <div class="progress-bar bg-danger" role="progressbar"
                                            style="width: ${item.soldPercent}%;">
                                          </div>
                                        </div>
                                        <small class="text-muted d-block mt-1">
                                          Đã bán: <strong>${item.soldCount}/${item.fsStock}</strong>
                                          <c:if test="${remaining > 0}">
                                            • Còn <strong>${remaining}</strong> sản phẩm
                                          </c:if>
                                          <c:if test="${isSoldOut}">
                                            • <span class="text-danger"><strong>Hết hàng</strong></span>
                                          </c:if>
                                        </small>
                                      </div>

                                      <div class="text-center pt-2">
                                        <small class="text-primary">Nhấp để xem chi tiết →</small>
                                      </div>
                                    </div>
                                  </div>
                                </a>
                              </div>
                            </c:if>
                          </c:forEach>
                        </div>
                      </div>
                    </c:forEach>
                  </div>

                  <!-- Navigation buttons -->
                  <c:if test="${totalProducts > 6}">
                    <button class="carousel-control-prev" type="button" data-bs-target="#flashSaleCarousel"
                      data-bs-slide="prev">
                      <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                      <span class="visually-hidden">Previous</span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#flashSaleCarousel"
                      data-bs-slide="next">
                      <span class="carousel-control-next-icon" aria-hidden="true"></span>
                      <span class="visually-hidden">Next</span>
                    </button>
                  </c:if>
                </div>
              </div>
            </c:if>

            <c:if test="${not empty latestProducts}">
              <!-- Tủ sách mới (expandable) -->
              <div class="featured-bookcase container">
                <h5 class="featured-bookcase-title"><i class="bi bi-clock"></i> Tủ sách mới</h5>
                <div class="row g-3 product" id="latestProductsContainer">
                  <c:set var="productsPerRow" value="6" />
                  <c:set var="maxRows" value="6" />
                  <c:set var="totalProducts" value="${fn:length(latestProducts)}" />
                  <c:set var="currentRow" value="0" />

                  <c:forEach var="i" begin="0" end="${totalProducts - 1}" step="${productsPerRow}">
                    <c:set var="currentRow" value="${currentRow + 1}" />
                    <div class="row-item" data-row="${currentRow}" style="${currentRow > 1 ? 'display: none;' : ''}">
                      <div class="row g-3">
                        <c:forEach var="j" begin="${i}" end="${i + productsPerRow - 1}">
                          <c:if test="${j < totalProducts}">
                            <c:set var="p" value="${latestProducts[j]}" />
                            <div class="col-6 col-md-4 col-lg-2">
                              <a href="${ctx}/home?action=detail&id=${p.productId}">
                                <div class="product-card">
                                  <div class="product-img">
                                    <c:if
                                      test="${p.salePrice != null && p.originalPrice != null && p.salePrice < p.originalPrice}">
                                      <span class="discount">
                                        -
                                        <fmt:formatNumber value="${p.discountPercent}" maxFractionDigits="0" />%
                                      </span>
                                    </c:if>
                                    <img src="http://localhost:8080/assets/images/catalog/products/${p.primaryImageUrl}"
                                      alt="${p.title}">
                                  </div>
                                  <div class="product-body">
                                    <h6 class="price">
                                      <c:choose>
                                        <c:when
                                          test="${p.salePrice != null && p.originalPrice != null && p.salePrice < p.originalPrice}">
                                          <fmt:formatNumber value="${p.salePrice}" type="currency" currencySymbol="đ"
                                            maxFractionDigits="0" />
                                          <span class="text-muted text-decoration-line-through ms-2">
                                            <fmt:formatNumber value="${p.originalPrice}" type="currency"
                                              currencySymbol="đ" maxFractionDigits="0" />
                                          </span>
                                        </c:when>
                                        <c:otherwise>
                                          <fmt:formatNumber value="${p.originalPrice}" type="currency"
                                            currencySymbol="đ" maxFractionDigits="0" />
                                        </c:otherwise>
                                      </c:choose>
                                    </h6>
                                    <small class="author">${p.publisher.name}</small>
                                    <p class="title">${p.title}</p>
                                    <div class="rating">
                                      <c:forEach begin="1" end="5" var="k">
                                        <c:choose>
                                          <c:when test="${k <= p.avgRating}">
                                            <i class="bi bi-star-fill text-warning small"></i>
                                          </c:when>
                                          <c:when test="${k - p.avgRating <= 0.5}">
                                            <i class="bi bi-star-half text-warning small"></i>
                                          </c:when>
                                          <c:otherwise>
                                            <i class="bi bi-star text-warning small"></i>
                                          </c:otherwise>
                                        </c:choose>
                                      </c:forEach>
                                      <span>Đã bán ${p.soldCount}</span>
                                    </div>
                                  </div>
                                </div>
                              </a>
                            </div>
                          </c:if>
                        </c:forEach>
                      </div>
                    </div>
                  </c:forEach>
                </div>

                <div class="text-center mt-4">
                  <button id="loadMoreBtn" class="button-two"
                    style="${totalProducts <= productsPerRow ? 'display: none;' : ''}">
                    Xem thêm
                  </button>
                  <button id="collapseBtn" class="button-two" style="display: none;">
                    Thu gọn
                  </button>
                </div>

              </div>
            </c:if>
          </main>

          <script>
            document.addEventListener("DOMContentLoaded", function () {
              const rows = document.querySelectorAll(".row-item");
              const loadMoreBtn = document.getElementById("loadMoreBtn");
              const collapseBtn = document.getElementById("collapseBtn");

              const maxRows = 6;
              let visibleRows = 1;

              loadMoreBtn.addEventListener("click", function () {
                visibleRows++;

                rows.forEach((row, index) => {
                  if (index < visibleRows) row.style.display = "block";
                });

                if (visibleRows >= maxRows || visibleRows >= rows.length) {
                  loadMoreBtn.style.display = "none";
                  collapseBtn.style.display = "inline-block";
                }
              });

              collapseBtn.addEventListener("click", function () {
                visibleRows = 1;

                rows.forEach((row, index) => {
                  row.style.display = index === 0 ? "block" : "none";
                });

                loadMoreBtn.style.display = "inline-block";
                collapseBtn.style.display = "none";
              });

              if (rows.length <= 1) {
                loadMoreBtn.style.display = "none";
                collapseBtn.style.display = "none";
              }
            });
          </script>

          <script>
            document.addEventListener("DOMContentLoaded", function () {
              // Timer countdown
              const timerData = document.getElementById("flashSaleTimerData");
              if (!timerData) return;

              const endAtStr = timerData.getAttribute("data-end-at");
              const serverTimestampMs = parseInt(timerData.getAttribute("data-server-time"));

              const endAtDate = new Date(endAtStr + " UTC");
              const flashSaleEndTime = endAtDate.getTime();
              const clientTimeNow = Date.now();
              const timeOffset = serverTimestampMs - clientTimeNow;

              const timerDisplay = {
                hours: document.querySelector('.flash-sale-time-hours'),
                minutes: document.querySelector('.flash-sale-time-minutes'),
                seconds: document.querySelector('.flash-sale-time-seconds')
              };

              function updateCountdown() {
                const now = Date.now() + timeOffset;
                const distance = flashSaleEndTime - now;

                if (distance < 0) {
                  timerDisplay.hours.textContent = '00';
                  timerDisplay.minutes.textContent = '00';
                  timerDisplay.seconds.textContent = '00';

                  const carousel = document.getElementById('flashSaleCarousel');
                  if (carousel) carousel.style.opacity = '0.5';
                  return;
                }

                const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((distance % (1000 * 60)) / 1000);

                timerDisplay.hours.textContent = String(hours).padStart(2, '0');
                timerDisplay.minutes.textContent = String(minutes).padStart(2, '0');
                timerDisplay.seconds.textContent = String(seconds).padStart(2, '0');
              }

              updateCountdown();
              setInterval(updateCountdown, 1000);

              // Progress bar animation
              setTimeout(() => {
                const progressBars = document.querySelectorAll('.flash-sale-card .progress-bar');
                progressBars.forEach(bar => {
                  const width = bar.style.width;
                  bar.style.width = '0';
                  setTimeout(() => {
                    bar.style.transition = 'width 0.6s ease-out';
                    bar.style.width = width;
                  }, 50);
                });
              }, 200);

              // Hover Effects
              const cards = document.querySelectorAll('.product-card-link');
              cards.forEach(card => {
                card.addEventListener('mouseenter', function () {
                  const productCard = this.querySelector('.product-card');
                  productCard.style.transform = 'translateY(-4px)';
                  productCard.style.boxShadow = '0 4px 12px rgba(255, 193, 7, 0.3)';
                });
                card.addEventListener('mouseleave', function () {
                  const productCard = this.querySelector('.product-card');
                  productCard.style.transform = 'none';
                  productCard.style.boxShadow = 'none';
                });
              });
            });
          </script>

          <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />

          <!-- JS của thông báo Toast -->
          <script src="${ctx}/assets/js/common/toast.js?v=1.0.1"></script>

          <!-- Scripts (Bootstrap + validator + auth_form) -->
          <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
        </body>

        </html>