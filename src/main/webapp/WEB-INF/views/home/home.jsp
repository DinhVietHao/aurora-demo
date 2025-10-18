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
            <!-- Toast notification Add To Cart -->
            <div id="notify-toast"></div>

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

                <div id="bookSuggest" class="carousel slide" data-bs-ride="false">
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

            <%-- FLASH SALES --%>
              <div class="container my-4 flash-sale">
                <div class="text-center mb-4">
                  <h4 class="flash-sale-title">🔥 Flash Sale 🔥</h4>
                  <p class="text-danger fw-bold">
                    <i class="bi bi-clock"></i> Ends in:
                    <span class="flash-sale-time">23h</span>
                    <span class="flash-sale-time">42m</span>
                    <span class="flash-sale-time">29s</span>
                  </p>
                </div>

                <div class="row g-3 product">
                  <div class="col-6 col-md-4 col-lg-2">
                    <div class="product-card">
                      <div class="product-img">
                        <span class="discount">-16%</span>
                        <img src="${ctx}/assets/images/catalog/products/product-2.jpg" alt="Sách" />
                      </div>
                      <div class="product-body">
                        <h6 class="price">128.300 ₫</h6>
                        <small class="author">GLENDY VANDERAH</small>
                        <p class="title">Nơi Khu Rừng Chạm Tới Những Vì Sao</p>
                        <div class="rating">
                          <i class="bi bi-star-fill text-warning small"></i><i
                            class="bi bi-star-fill text-warning small"></i><i
                            class="bi bi-star-fill text-warning small"></i><i
                            class="bi bi-star-fill text-warning small"></i><i
                            class="bi bi-star-half text-warning small"></i>
                          <span>Đã bán 292</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <%-- … các card còn lại --%>
                </div>
              </div>

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
                                      <img
                                        src="http://localhost:8080/assets/images/catalog/products/${p.primaryImageUrl}"
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

          <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />

          <!-- JS của thông báo Toast -->
          <script src="${ctx}/assets/js/common/toast.js?v=1.0.1"></script>

          <!-- Scripts (Bootstrap + validator + auth_form) -->
          <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
        </body>

        </html>