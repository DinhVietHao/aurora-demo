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
      <%-- Banner --%>
      <div class="container banner">
        <div id="carouselExample" class="carousel slide banner-book" data-bs-ride="carousel">
          <div class="carousel-inner">
            <div class="carousel-item active">
              <img
                src="${ctx}/assets/images/catalog/banners/banner-book.png"
                class="d-block w-100 banner-image"
                alt="..."
              />
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
              <img
                src="${ctx}/assets/images/catalog/banners/banner-book.png"
                class="d-block w-100"
                alt="..."
              />
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
              <img
                src="${ctx}/assets/images/catalog/banners/banner-book.png"
                class="d-block w-100"
                alt="..."
              />
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

          <button
            class="carousel-control-prev"
            type="button"
            data-bs-target="#carouselExample"
            data-bs-slide="prev"
          >
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Previous</span>
          </button>
          <button
            class="carousel-control-next"
            type="button"
            data-bs-target="#carouselExample"
            data-bs-slide="next"
          >
            <span class="carousel-control-next-icon icon-next" aria-hidden="true"></span>
            <span class="visually-hidden">Next</span>
          </button>
        </div>

        <%-- Ads --%>
        <div class="banner-advertisemet row my-4">
          <div class="col-6 col-md-4 col-lg-3">
            <a
              ><img
                src="${ctx}/assets/images/catalog/banners/advertisement-1.png"
                alt="advertisement"
            /></a>
          </div>
          <div class="col-6 col-md-4 col-lg-3">
            <a
              ><img
                src="${ctx}/assets/images/catalog/banners/advertisement-2.png"
                alt="advertisement"
            /></a>
          </div>
          <div class="col-6 col-md-4 col-lg-3">
            <a
              ><img
                src="${ctx}/assets/images/catalog/banners/advertisement-3.png"
                alt="advertisement"
            /></a>
          </div>
          <div class="col-6 col-md-4 col-lg-3">
            <a
              ><img
                src="${ctx}/assets/images/catalog/banners/advertisement-4.png"
                alt="advertisement"
            /></a>
          </div>
        </div>
      </div>

      <%-- Gợi ý cho bạn --%>
      <div class="suggest container">
        <h5 class="suggest-title"><i class="bi bi-lightbulb"></i> Gợi ý cho bạn</h5>

        <div id="bookSuggest" class="carousel slide" data-bs-ride="false">
          <div class="carousel-inner">
            <div class="carousel-item active">
              <div class="row g-3 product">
                <%-- Nếu products rỗng thì show thông báo --%>
                <c:if test="${empty products}">
                  <div class="alert alert-warning">Chưa có sản phẩm để hiển thị.</div>
                </c:if>
                <c:forEach var="p" items="${products}">
                  <div class="col-6 col-md-4 col-lg-2">
                    <a href="${ctx}/book?id=${p.productId}">
                      <div class="product-card">
                        <div class="product-img">
                          <c:if
                            test="${p.originalPrice != null && p.salePrice != null && p.originalPrice > p.salePrice}"
                          >
                            <span class="discount">
                              -<fmt__colon__formatNumber
                                value="${((p.originalPrice - p.salePrice) / p.originalPrice) * 100}"
                                maxFractionDigits="0"
                              />%
                            </span>
                          </c:if>
                          <img
                            src="http://localhost:8080/assets/images/catalog/thumbnails/${p.primaryImageUrl}"
                          />
                        </div>
                        <div class="product-body">
                          <h6 class="price">
                            <c:choose>
                              <c:when
                                test="${p.salePrice != null && p.originalPrice != null && p.salePrice < p.originalPrice}"
                              >
                                <fmt:formatNumber
                                  value="${p.salePrice}"
                                  type="currency"
                                  currencySymbol="đ"
                                  maxFractionDigits="0"
                                />
                                <span class="text-muted text-decoration-line-through ms-2">
                                  <fmt:formatNumber
                                    value="${p.originalPrice}"
                                    type="currency"
                                    currencySymbol="đ"
                                    maxFractionDigits="0"
                                  />
                                </span>
                              </c:when>
                              <c:otherwise>
                                <fmt:formatNumber
                                  value="${p.originalPrice}"
                                  type="currency"
                                  currencySymbol="đ"
                                  maxFractionDigits="0"
                                />
                              </c:otherwise>
                            </c:choose>
                          </h6>
                          <small class="author">${p.publisher.publisherName}</small>
                          <p class="title">${p.title}</p>
                          <div class="rating">
                            <i class="bi bi-star-fill text-warning small"></i>
                            <i class="bi bi-star-fill text-warning small"></i>
                            <i class="bi bi-star-fill text-warning small"></i>
                            <i class="bi bi-star-fill text-warning small"></i>
                            <i class="bi bi-star-half text-warning small"></i>
                            <span>Đã bán ${p.soldCount}</span>
                          </div>
                        </div>
                      </div>
                    </a>
                  </div>
                </c:forEach>
              </div>
            </div>
          </div>

          <%-- Nút điều hướng --%>
          <button
            class="carousel-control-prev"
            type="button"
            data-bs-target="#bookSuggest"
            data-bs-slide="prev"
          >
            <span class="carousel-control-prev-icon"></span>
          </button>
          <button
            class="carousel-control-next"
            type="button"
            data-bs-target="#bookSuggest"
            data-bs-slide="next"
          >
            <span class="carousel-control-next-icon"></span>
          </button>
        </div>
      </div>

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
                  <i class="bi bi-star-fill text-warning small"></i
                  ><i class="bi bi-star-fill text-warning small"></i
                  ><i class="bi bi-star-fill text-warning small"></i
                  ><i class="bi bi-star-fill text-warning small"></i
                  ><i class="bi bi-star-half text-warning small"></i>
                  <span>Đã bán 292</span>
                </div>
              </div>
            </div>
          </div>
          <%-- … các card còn lại --%>
        </div>
      </div>

      <%-- Tủ sách nổi bật (giữ nguyên bố cục, sửa đường dẫn ảnh) --%>
      <div class="featured-bookcase container">
        <h5 class="featured-bookcase-title"><i class="bi bi-book"></i> Tủ sách nổi bật</h5>
        <div class="row g-3 product">
          <div class="col-6 col-md-4 col-lg-2">
            <div class="product-card">
              <div class="product-img">
                <span class="discount">-16%</span>
                <img src="${ctx}/assets/images/catalog/products/product-1.png" alt="Sách" />
              </div>
              <div class="product-body">
                <h6 class="price">128.300 ₫</h6>
                <small class="author">GLENDY VANDERAH</small>
                <p class="title">Nơi Khu Rừng Chạm Tới Những Vì Sao</p>
                <div class="rating">
                  <i class="bi bi-star-fill text-warning small"></i
                  ><i class="bi bi-star-fill text-warning small"></i
                  ><i class="bi bi-star-fill text-warning small"></i
                  ><i class="bi bi-star-fill text-warning small"></i
                  ><i class="bi bi-star-half text-warning small"></i>
                  <span>Đã bán 99</span>
                </div>
              </div>
            </div>
          </div>
          <%-- … các card còn lại --%>
        </div>

        <div class="text-center mt-4">
          <a href="${ctx}/book" class="button-two">Xem thêm</a>
        </div>
      </div>
    </main>

    <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />

    <%-- Scripts (Bootstrap + validator + auth_form) --%>
    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
  </body>
</html>
