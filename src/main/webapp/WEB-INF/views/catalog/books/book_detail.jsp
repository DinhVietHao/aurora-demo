<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
      <fmt:setLocale value="vi_VN" />
      <c:set var="ctx" value="${pageContext.request.contextPath}" />
      <c:set var="pageTitle" value="${empty product.title ? 'Aurora' : product.title}" />

      <!DOCTYPE html>
      <html lang="vi">

      <head>
        <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
        <link rel="stylesheet" href="${ctx}/assets/css/catalog/book_detail.css" />
        <link rel="stylesheet" href="${ctx}/assets/css/catalog/comment.css" />
      </head>

      <body>
        <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

        <div class="container book-detail mt-3">
          <div class="row justify-content-evenly">
            <div class="col-md-5">
              <div class="book-detail-images">
                <div class="product-image mb-3">
                  <c:if test="${not empty product.images}">
                    <img id="mainImage" src="${ctx}/assets/images/catalog/products/${product.images[0].url}" alt="Sách"
                      class="img-fluid border"
                      style="width: 100%; height: 500px; object-fit: contain; background-color: #f8f9fa;" />
                  </c:if>
                </div>

                <div class="row g-2 mb-3">
                  <c:forEach var="img" items="${product.images}" varStatus="s">
                    <c:if test="${!s.first}">
                      <div class="col-3">
                        <img src="${ctx}/assets/images/catalog/products/${img.url}" class="img-fluid border thumbnail"
                          alt="" style="width: 100%; height: 150px; object-fit: contain; background-color: #f8f9fa;" />
                      </div>
                    </c:if>
                  </c:forEach>
                </div>

                <div class="row gap-2 justify-content-evenly">
                  <button class="button-two col-lg-5" id="add-to-cart" data-product-id="${product.productId}">
                    <i class="bi bi-cart3"></i> Thêm vào giỏ hàng
                  </button>
                  <button class="button-three col-lg-5" id="buyNow" data-product-id="${product.productId}">Mua
                    Ngay</button>
                </div>
              </div>

              <div class="shop-info-card mt-3">
                <div class="shop-header-section-new">
                  <div class="shop-header-left">
                    <!-- Avatar -->
                    <div class="shop-avatar-wrapper">
                      <img
                        src="${ctx}/assets/images/shops/${empty shop.avatarUrl ? 'default-shop.png' : shop.avatarUrl}"
                        class="shop-avatar-img" alt="${shop.name}"
                        onerror="this.src='${ctx}/assets/images/shops/default-shop.png';">
                    </div>

                    <!-- Thông tin cửa hàng -->
                    <div class="shop-info-section">
                      <h5 class="shop-name">
                        <c:out value="${shop.name}" />
                      </h5>
                      <c:if test="${not empty shop.description}">
                        <p class="shop-description">
                          <c:out value="${shop.description}" />
                        </p>
                      </c:if>
                    </div>
                  </div>

                  <div class="shop-header-right">
                    <a href="${ctx}/shop/view?id=${shop.shopId}" class="shop-view-button-compact">
                      <i class="bi bi-shop"></i>
                      Xem shop
                    </a>
                  </div>
                </div>

                <!-- Thống kê -->
                <div class="shop-stats-section">
                  <!-- Số lượng sản phẩm đang bán -->
                  <div class="shop-stat-item">
                    <i class="bi bi-box-seam"></i>
                    <div class="stat-content">
                      <strong class="stat-value">
                        <fmt:formatNumber value="${shop.productCount}" />
                      </strong>
                      <span class="stat-label">Sản phẩm</span>
                    </div>
                  </div>

                  <!-- Đánh giá trung bình -->
                  <div class="shop-stat-item">
                    <i class="bi bi-star-fill"></i>
                    <div class="stat-content">
                      <strong class="stat-value">
                        <fmt:formatNumber value="${shop.avgRating}" maxFractionDigits="1" />
                      </strong>
                      <span class="stat-label">
                        (
                        <fmt:formatNumber value="${shop.reviewCount}" /> đánh giá)
                      </span>
                    </div>
                  </div>

                  <!-- Thời gian tham gia -->
                  <div class="shop-stat-item">
                    <i class="bi bi-calendar-event"></i>
                    <div class="stat-content">
                      <strong class="stat-value">
                        <fmt:formatDate value="${shop.createdAt}" pattern="dd/MM/yyyy" />
                      </strong>
                      <span class="stat-label">Tham gia</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Thông tin chính -->
            <div class="col-md-7">
              <div class="book-detail-header">
                <h6 class="author">Tác giả:
                  <c:forEach var="author" items="${product.authors}" varStatus="status">
                    <c:out value="${author.authorName}" />
                    <c:if test="${!status.last}">, </c:if>
                  </c:forEach>
                </h6>
                <h4 class="title">
                  <c:out value="${product.title}" />
                </h4>

                <div class="mb-2 rating">
                  <c:forEach begin="1" end="5" var="k">
                    <c:choose>
                      <c:when test="${k <= product.avgRating}">
                        <i class="bi bi-star-fill text-warning small"></i>
                      </c:when>
                      <c:when test="${k - product.avgRating <= 0.5}">
                        <i class="bi bi-star-half text-warning small"></i>
                      </c:when>
                      <c:otherwise>
                        <i class="bi bi-star text-warning small"></i>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
                  <span>
                    <fmt:formatNumber value="${product.avgRating}" maxFractionDigits="1" /> (${reviewCount}) |
                    <c:out value="${product.soldCount}" />
                  </span>
                </div>

                <div class="mb-3">
                  <span class="price">
                    <fmt:formatNumber value="${product.salePrice}" type="currency" currencySymbol="đ"
                      groupingUsed="true" />
                  </span>
                  <c:if test="${product.discountPercent != 0}">
                    <span class="discount">-
                      <c:out value="${product.discountPercent}" />%
                    </span>
                    <span class="text-muted text-decoration-line-through">
                      <fmt:formatNumber value="${product.originalPrice}" type="currency" currencySymbol="đ"
                        groupingUsed="true" />
                    </span>
                  </c:if>
                </div>
              </div>

              <!-- Thông tin chi tiết sách -->
              <div class="book-information my-3">
                <div class="book-information-header">Thông tin chi tiết</div>

                <div class="book-information-body">
                  <div class="row mb-1 book-information-box">
                    <div class="col-5 text-muted">Thể loại</div>
                    <div class="col-7">
                      <c:forEach var="cat" items="${product.categories}" varStatus="status">
                        <c:out value="${cat.name}" />
                        <c:if test="${!status.last}">, </c:if>
                      </c:forEach>
                    </div>
                  </div>

                  <div class="row mb-1 book-information-box">
                    <div class="col-5 text-muted">Ngôn ngữ</div>
                    <div class="col-7">
                      <c:out value="${product.bookDetail.language.languageName}" />
                    </div>
                  </div>

                  <div class="row mb-1 book-information-box">
                    <div class="col-5 text-muted">Số trang</div>
                    <div class="col-7">
                      <c:out value="${product.bookDetail.pages}" />
                    </div>
                  </div>

                  <div class="row mb-1 book-information-box">
                    <div class="col-5 text-muted">Kích thước</div>
                    <div class="col-7">
                      <c:out value="${product.bookDetail.size}" />
                    </div>
                  </div>

                  <div class="row mb-1 book-information-box">
                    <div class="col-5 text-muted">Loại bìa</div>
                    <div class="col-7">
                      <c:out value="${product.bookDetail.coverType}" />
                    </div>
                  </div>

                  <div class="row mb-1 book-information-box">
                    <div class="col-5 text-muted">Nhà xuất bản</div>
                    <div class="col-7">
                      <c:out value="${product.publisher.name}" />
                    </div>
                  </div>

                  <div class="row mb-1 book-information-box">
                    <div class="col-5 text-muted">Ngày xuất bản</div>
                    <div class="col-7">
                      <c:out value="${product.publishedDate}" />
                    </div>
                  </div>

                  <div class="row mb-1 book-information-box">
                    <div class="col-5 text-muted">Phiên bản sách</div>
                    <div class="col-7">
                      <c:out value="${product.bookDetail.version}" />
                    </div>
                  </div>

                  <div class="row mb-1 book-information-box">
                    <div class="col-5 text-muted">Dịch giả</div>
                    <div class="col-7">
                      <c:out value="${product.bookDetail.translator}" />
                    </div>
                  </div>

                  <div class="row mb-1 book-information-box">
                    <div class="col-5 text-muted">ISBN</div>
                    <div class="col-7">
                      <c:out value="${product.bookDetail.isbn}" />
                    </div>
                  </div>
                </div>
              </div>

              <!-- Thông tin mô tả -->
              <div class="mt-4">
                <div class="book-description">
                  <div class="book-description-header">Mô tả sản phẩm</div>
                  <div class="book-description-body">
                    <p class="fw-medium">
                      <c:out value="${product.title}" />
                    </p>
                    <p id="moreText">
                      <c:out value="${product.description}" />
                    </p>
                    <div class="gradient"></div>
                  </div>
                  <a class="d-block mt-2 text-primary text-center cursor-pointer" id="more">
                    Xem thêm
                  </a>
                </div>
              </div>
            </div>
          </div>

          <!-- Đánh giá -->
          <div class="row mt-4">
            <div class="col-12 comment">
              <div class="row comment-header">
                <h5 class="comment-title">ĐÁNH GIÁ SẢN PHẨM</h5>
                <div class="col-md-3 px-5">
                  <h2 class="comment-average">
                    <fmt:formatNumber value="${product.avgRating}" minFractionDigits="1" maxFractionDigits="1" />
                    <small class="fs-6">trên 5</small>
                  </h2>
                  <div class="text-warning">
                    <c:forEach begin="1" end="5" var="k">
                      <c:choose>
                        <c:when test="${k <= product.avgRating}">
                          <i class="bi bi-star-fill"></i>
                        </c:when>
                        <c:when test="${k - product.avgRating <= 0.5}">
                          <i class="bi bi-star-half"></i>
                        </c:when>
                        <c:otherwise>
                          <i class="bi bi-star"></i>
                        </c:otherwise>
                      </c:choose>
                    </c:forEach>
                  </div>
                </div>
                <!-- Filter Buttons -->
                <div class="col-md-9 d-flex align-items-center">
                  <div class="comment-filter">
                    <a href="?action=detail&id=${product.productId}#reviews"
                      class="button-outline ${selectedRating == 'all' && empty selectedFilter ? 'active' : ''}">
                      Tất Cả
                    </a>
                    <a href="?action=detail&id=${product.productId}&rating=5#reviews"
                      class="button-outline ${selectedRating == '5' ? 'active' : ''}">
                      5 Sao
                    </a>
                    <a href="?action=detail&id=${product.productId}&rating=4#reviews"
                      class="button-outline ${selectedRating == '4' ? 'active' : ''}">
                      4 Sao
                    </a>
                    <a href="?action=detail&id=${product.productId}&rating=3#reviews"
                      class="button-outline ${selectedRating == '3' ? 'active' : ''}">
                      3 Sao
                    </a>
                    <a href="?action=detail&id=${product.productId}&rating=2#reviews"
                      class="button-outline ${selectedRating == '2' ? 'active' : ''}">
                      2 Sao
                    </a>
                    <a href="?action=detail&id=${product.productId}&rating=1#reviews"
                      class="button-outline ${selectedRating == '1' ? 'active' : ''}">
                      1 Sao
                    </a>
                    <a href="?action=detail&id=${product.productId}&filter=comment#reviews"
                      class="button-outline ${selectedFilter == 'comment' ? 'active' : ''}">
                      Có Bình Luận
                    </a>
                    <a href="?action=detail&id=${product.productId}&filter=image#reviews"
                      class="button-outline ${selectedFilter == 'image' ? 'active' : ''}">
                      Có Hình Ảnh
                    </a>
                  </div>
                </div>
              </div>

              <!-- Danh sách review -->
              <c:choose>
                <c:when test="${not empty reviews}">
                  <c:forEach var="review" items="${reviews}">
                    <div class="row comment-body">
                      <div class="col-auto comment-image">
                        <c:choose>
                          <c:when test="${not empty review.user.avatarUrl}">
                            <img src="${ctx}/assets/images/avatars/${review.user.avatarUrl}" alt="avatar">
                          </c:when>
                          <c:otherwise>
                            <img src="${ctx}/assets/images/common/avatar.png" alt="avatar">
                          </c:otherwise>
                        </c:choose>
                      </div>

                      <div class="col">
                        <div class="d-flex justify-content-between">
                          <h6 class="mb-0 fw-bold">
                            <c:out value="${review.user.fullName}" />
                          </h6>
                          <div class="dropdown">
                            <button class="btn btn-sm btn-light" type="button" data-bs-toggle="dropdown"
                              aria-expanded="false">
                              <i class="fa fa-ellipsis-h"></i>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                              <li>
                                <a class="dropdown-item" href="#" data-bs-toggle="modal"
                                  data-bs-target="#reportModal">Báo cáo</a>
                              </li>
                            </ul>
                          </div>
                        </div>
                        <small class="text-muted">
                          <fmt:formatDate value="${review.createdAt}" pattern="yyyy-MM-dd HH:mm" />
                        </small>

                        <!-- Rating -->
                        <div class="text-warning my-1">
                          <c:forEach begin="1" end="${review.rating}">
                            <i class="bi bi-star-fill small"></i>
                          </c:forEach>
                          <c:forEach begin="${review.rating + 1}" end="5">
                            <i class="bi bi-star small"></i>
                          </c:forEach>
                        </div>

                        <p class="mb-1">
                          <c:out value="${review.comment}" />
                        </p>

                        <!-- Hình ảnh review (nếu có) -->
                        <c:if test="${not empty review.images}">
                          <div class="d-flex gap-2 comment-review">
                            <c:forEach var="img" items="${review.images}" varStatus="status">
                              <img src="${ctx}/assets/images/reviews/${img.url}" class="" alt="ảnh review"
                                data-bs-toggle="modal" data-bs-target="#reviewModal${review.reviewId}"
                                data-bs-slide-to="${status.index}">
                            </c:forEach>
                          </div>
                        </c:if>
                      </div>
                    </div>

                    <!-- Modal xem ảnh review -->
                    <c:if test="${not empty review.images}">
                      <div class="modal fade" id="reviewModal${review.reviewId}" tabindex="-1"
                        aria-labelledby="reviewModalLabel" aria-hidden="true">
                        <div class="modal-dialog modal-lg modal-dialog-centered">
                          <div class="modal-content">
                            <div class="modal-body">
                              <div class="d-flex justify-content-end">
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                  aria-label="Close"></button>
                              </div>
                              <div id="reviewCarousel${review.reviewId}" class="carousel slide">
                                <div class="carousel-inner">
                                  <c:forEach var="img" items="${review.images}" varStatus="status">
                                    <div class="carousel-item ${status.first ? 'active' : ''}">
                                      <img src="${ctx}/assets/images/reviews/${img.url}" class="d-block w-100"
                                        alt="ảnh review">
                                    </div>
                                  </c:forEach>
                                </div>
                                <button class="carousel-control-prev" type="button"
                                  data-bs-target="#reviewCarousel${review.reviewId}" data-bs-slide="prev">
                                  <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                </button>
                                <button class="carousel-control-next" type="button"
                                  data-bs-target="#reviewCarousel${review.reviewId}" data-bs-slide="next">
                                  <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                </button>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </c:if>
                  </c:forEach>

                  <!-- Pagination for reviews -->
                  <c:if test="${totalReviews > 10}">
                    <nav class="mt-4" aria-label="Review pagination">
                      <ul class="pagination justify-content-center">
                        <!-- Previous Button -->
                        <c:if test="${currentPage > 1}">
                          <li class="page-item">
                            <c:url var="prevUrl" value="">
                              <c:param name="action" value="detail" />
                              <c:param name="id" value="${product.productId}" />
                              <c:param name="reviewPage" value="${currentPage - 1}" />
                              <c:if test="${not empty selectedRating && selectedRating != 'all'}">
                                <c:param name="rating" value="${selectedRating}" />
                              </c:if>
                              <c:if test="${not empty selectedFilter}">
                                <c:param name="filter" value="${selectedFilter}" />
                              </c:if>
                            </c:url>
                            <a class="page-link" href="${prevUrl}#reviews" aria-label="Previous">‹</a>
                          </li>
                        </c:if>

                        <!-- Smart Pagination -->
                        <c:choose>
                          <c:when test="${totalPages <= 7}">
                            <!-- Show all pages if ≤7 -->
                            <c:forEach begin="1" end="${totalPages}" var="i">
                              <li class="page-item ${i == currentPage ? 'active' : ''}">
                                <c:url var="pageUrl" value="">
                                  <c:param name="action" value="detail" />
                                  <c:param name="id" value="${product.productId}" />
                                  <c:param name="reviewPage" value="${i}" />
                                  <c:if test="${not empty selectedRating && selectedRating != 'all'}">
                                    <c:param name="rating" value="${selectedRating}" />
                                  </c:if>
                                  <c:if test="${not empty selectedFilter}">
                                    <c:param name="filter" value="${selectedFilter}" />
                                  </c:if>
                                </c:url>
                                <a class="page-link" href="${pageUrl}#reviews">${i}</a>
                              </li>
                            </c:forEach>
                          </c:when>
                          <c:otherwise>
                            <!-- Smart pagination with ellipsis -->

                            <!-- First Page -->
                            <li class="page-item ${currentPage == 1 ? 'active' : ''}">
                              <c:url var="firstUrl" value="">
                                <c:param name="action" value="detail" />
                                <c:param name="id" value="${product.productId}" />
                                <c:param name="reviewPage" value="1" />
                                <c:if test="${not empty selectedRating && selectedRating != 'all'}">
                                  <c:param name="rating" value="${selectedRating}" />
                                </c:if>
                                <c:if test="${not empty selectedFilter}">
                                  <c:param name="filter" value="${selectedFilter}" />
                                </c:if>
                              </c:url>
                              <a class="page-link" href="${firstUrl}#reviews">1</a>
                            </li>

                            <!-- Left Ellipsis -->
                            <c:if test="${currentPage > 3}">
                              <li class="page-item disabled">
                                <span class="page-link">...</span>
                              </li>
                            </c:if>

                            <!-- Current Page Group -->
                            <c:forEach begin="${currentPage - 1}" end="${currentPage + 1}" var="i">
                              <c:if test="${i > 1 && i < totalPages}">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                  <c:url var="pageUrl" value="">
                                    <c:param name="action" value="detail" />
                                    <c:param name="id" value="${product.productId}" />
                                    <c:param name="reviewPage" value="${i}" />
                                    <c:if test="${not empty selectedRating && selectedRating != 'all'}">
                                      <c:param name="rating" value="${selectedRating}" />
                                    </c:if>
                                    <c:if test="${not empty selectedFilter}">
                                      <c:param name="filter" value="${selectedFilter}" />
                                    </c:if>
                                  </c:url>
                                  <a class="page-link" href="${pageUrl}#reviews">${i}</a>
                                </li>
                              </c:if>
                            </c:forEach>

                            <!-- Right Ellipsis -->
                            <c:if test="${currentPage < totalPages - 2}">
                              <li class="page-item disabled">
                                <span class="page-link">...</span>
                              </li>
                            </c:if>

                            <!-- Last Page -->
                            <li class="page-item ${currentPage == totalPages ? 'active' : ''}">
                              <c:url var="lastUrl" value="">
                                <c:param name="action" value="detail" />
                                <c:param name="id" value="${product.productId}" />
                                <c:param name="reviewPage" value="${totalPages}" />
                                <c:if test="${not empty selectedRating && selectedRating != 'all'}">
                                  <c:param name="rating" value="${selectedRating}" />
                                </c:if>
                                <c:if test="${not empty selectedFilter}">
                                  <c:param name="filter" value="${selectedFilter}" />
                                </c:if>
                              </c:url>
                              <a class="page-link" href="${lastUrl}#reviews">${totalPages}</a>
                            </li>
                          </c:otherwise>
                        </c:choose>

                        <!-- Next Button -->
                        <c:if test="${currentPage < totalPages}">
                          <li class="page-item">
                            <c:url var="nextUrl" value="">
                              <c:param name="action" value="detail" />
                              <c:param name="id" value="${product.productId}" />
                              <c:param name="reviewPage" value="${currentPage + 1}" />
                              <c:if test="${not empty selectedRating && selectedRating != 'all'}">
                                <c:param name="rating" value="${selectedRating}" />
                              </c:if>
                              <c:if test="${not empty selectedFilter}">
                                <c:param name="filter" value="${selectedFilter}" />
                              </c:if>
                            </c:url>
                            <a class="page-link" href="${nextUrl}#reviews" aria-label="Next">›</a>
                          </li>
                        </c:if>
                      </ul>
                    </nav>
                  </c:if>
                </c:when>
                <c:otherwise>
                  <div class="text-center py-5">
                    <p class="text-muted">Chưa có đánh giá nào cho sản phẩm này.</p>
                    <c:if test="${not empty sessionScope.AUTH_USER}">
                      <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#writeReviewModal">
                        Viết đánh giá đầu tiên
                      </button>
                    </c:if>
                  </div>
                </c:otherwise>
              </c:choose>
            </div>
          </div>

          <!-- CAROUSEL: AURORA GIỚI THIỆU (tái dùng thẻ sản phẩm) -->
          <div class="book-introduction container">
            <h5 class="book-introduction-title">Aurora giới thiệu</h5>
            <jsp:include page="/WEB-INF/views/catalog/books/partials/_intro_carousel.jsp">
              <jsp:param name="carouselId" value="bookIntroduction" />
            </jsp:include>
          </div>
        </div>

        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
        <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

        <!-- JS riêng của trang -->
        <script src="${ctx}/assets/js/catalog/book_detail.js?v=1.0.1"></script>
        <script src="${ctx}/assets/js/catalog/comment.js"></script>
      </body>

      </html>