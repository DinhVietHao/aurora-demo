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
        <link rel="stylesheet" href="${ctx}/assets/css/catalog/book_detail.css?v=1.0.1" />
        <link rel="stylesheet" href="${ctx}/assets/css/catalog/comment.css" />
      </head>

      <body>
        <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

        <!-- Flash Sale Info -->
        <div class="container book-detail mt-3">
          <c:if test="${flashSaleInfo.isFlashSale}">
            <!-- Flash Sale Section -->
            <div class="flash-sale-banner mb-4">
              <div class="flash-sale-header">
                <div class="flash-icon-wrapper">
                  <img src="${ctx}/assets/images/branding/flash-sale-info.png" alt="Flash Sale" class="flash-icon">
                </div>
                <div class="flash-header-text" style="margin-left: -16%;">
                  <h3 class="flash-title">FLASH SALE</h3>
                  <p class="flash-subtitle">Giảm giá sốc - Hàng chất</p>
                </div>

                <!-- Price Section -->
                <div class="flash-price-section">
                  <div class="flash-price-wrapper">
                    <span class="flash-price">
                      <fmt:formatNumber value="${flashSaleInfo.flashPrice}" type="currency" currencySymbol="đ"
                        groupingUsed="true" />
                    </span>
                    <span class="flash-discount-badge">-${flashSaleInfo.discountPercent}%</span>
                  </div>
                  <div class="original-price">
                    <fmt:formatNumber value="${product.originalPrice}" type="currency" currencySymbol="đ"
                      groupingUsed="true" />
                  </div>
                </div>

                <!-- Countdown Timer -->
                <div class="flash-countdown-wrapper">
                  <div class="countdown-timer" data-end-time="${flashSaleInfo.endAt.time}">
                    <div class="time-box">
                      <span class="time-value days">00</span>
                      <span class="time-label">NGÀY</span>
                    </div>
                    <div class="time-box">
                      <span class="time-value hours">00</span>
                      <span class="time-label">GIỜ</span>
                    </div>
                    <div class="time-box">
                      <span class="time-value minutes">00</span>
                      <span class="time-label">PHÚT</span>
                    </div>
                    <div class="time-box">
                      <span class="time-value seconds">00</span>
                      <span class="time-label">GIÂY</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </c:if>

          <div class="row justify-content-evenly">
            <div class="col-md-5">
              <div class="book-detail-images">
                <div class="product-image mb-3 position-relative">
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
                    <a href="${ctx}/home?action=view-shop&shopId=${shop.shopId}" class="shop-view-button-compact">
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
                  <c:choose>
                    <c:when test="${flashSaleInfo.isFlashSale}">
                      <img src="${ctx}/assets/images/branding/flash-sale-title.gif" alt="Flash Sale"
                        class="flash-sale-title-badge" />
                    </c:when>
                    <c:otherwise>
                      <img src="${ctx}/assets/images/branding/books.gif" alt="Books" class="flash-sale-title-badge"
                        style="width: 12%; top: -55%;" />
                    </c:otherwise>
                  </c:choose>
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

                <c:choose>
                  <c:when test="${flashSaleInfo.isFlashSale}">
                    <div class="mb-3">
                      <img src="https://em-content.zobj.net/source/animated-noto-color-emoji/427/fire_1f525.gif"
                        alt="Fire" class="fire-icon" />
                      <span class="old-price-faded">
                        <fmt:formatNumber value="${product.originalPrice}" type="currency" currencySymbol="đ"
                          groupingUsed="true" />
                      </span>
                    </div>
                  </c:when>

                  <c:otherwise>
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
                  </c:otherwise>
                </c:choose>
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
          <div class="row mt-4" id="reviews">
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

                <input type="hidden" id="product-id" value="${product.productId}">
                <input type="hidden" id="context-path" value="${empty ctx ? '' : ctx}">

                <div class="col-md-9 d-flex align-items-center" style="margin-bottom: 35px">
                  <div class="comment-filter">
                    <button type="button" data-rating="all"
                      class="button-outline ${empty selectedRating || selectedRating == 'all' ? 'active' : ''}">
                      Tất Cả
                    </button>
                    <button type="button" data-rating="5"
                      class="button-outline ${selectedRating == '5' ? 'active' : ''}">
                      5 Sao
                    </button>
                    <button type="button" data-rating="4"
                      class="button-outline ${selectedRating == '4' ? 'active' : ''}">
                      4 Sao
                    </button>
                    <button type="button" data-rating="3"
                      class="button-outline ${selectedRating == '3' ? 'active' : ''}">
                      3 Sao
                    </button>
                    <button type="button" data-rating="2"
                      class="button-outline ${selectedRating == '2' ? 'active' : ''}">
                      2 Sao
                    </button>
                    <button type="button" data-rating="1"
                      class="button-outline ${selectedRating == '1' ? 'active' : ''}">
                      1 Sao
                    </button>
                    <button type="button" data-filter="comment"
                      class="button-outline ${selectedFilter == 'comment' ? 'active' : ''}">
                      Có Bình Luận
                    </button>
                    <button type="button" data-filter="image"
                      class="button-outline ${selectedFilter == 'image' ? 'active' : ''}">
                      Có Hình Ảnh
                    </button>
                  </div>
                </div>
              </div>

              <div id="reviews-container">
                <c:choose>
                  <c:when test="${not empty reviews}">
                    <c:forEach var="review" items="${reviews}">
                      <div class="row comment-body">
                        <div class="col-auto comment-image">
                          <c:choose>
                            <c:when test="${not empty review.user.avatarUrl}">
                              <img src="${ctx}/assets/images/avatars/${review.user.avatarUrl}" alt="avatar"
                                style="width: 50px; height: 50px; border-radius: 50%; object-fit: cover;">
                            </c:when>
                            <c:otherwise>
                              <img src="${ctx}/assets/images/common/avatar.png" alt="avatar"
                                style="width: 50px; height: 50px; border-radius: 50%; object-fit: cover;">
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
                                <c:if
                                  test="${not empty sessionScope.AUTH_USER && sessionScope.AUTH_USER.id == review.user.id}">
                                  <li>
                                    <a class="dropdown-item btn-open-edit-review" href="#" data-bs-toggle="modal"
                                      data-bs-target="#editReviewModal" data-review-id="${review.reviewId}"
                                      data-rating="${review.rating}" data-comment="${review.comment}">
                                      Sửa
                                    </a>
                                  </li>
                                </c:if>
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

                          <!-- Hình ảnh review -->
                          <c:if test="${not empty review.images}">
                            <div class="d-flex gap-2 comment-review">
                              <c:forEach var="img" items="${review.images}" varStatus="status">
                                <img src="${ctx}/assets/images/reviews/${img.url}" alt="ảnh review"
                                  style="width: 80px; height: 80px; object-fit: cover; border-radius: 4px; cursor: pointer;"
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

              <!-- Pagination Container -->
              <div id="pagination-container">
                <c:if test="${totalReviews > 10}">
                  <nav class="mt-4" aria-label="Review pagination">
                    <ul class="pagination justify-content-center">
                      <!-- Previous Button -->
                      <c:if test="${currentPage > 1}">
                        <li class="page-item">
                          <a class="page-link" href="#" data-page="${currentPage - 1}" aria-label="Previous">‹</a>
                        </li>
                      </c:if>

                      <!-- Page numbers -->
                      <c:choose>
                        <c:when test="${totalPages <= 7}">
                          <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                              <a class="page-link" href="#" data-page="${i}">${i}</a>
                            </li>
                          </c:forEach>
                        </c:when>
                        <c:otherwise>
                          <!-- First page -->
                          <li class="page-item ${currentPage == 1 ? 'active' : ''}">
                            <a class="page-link" href="#" data-page="1">1</a>
                          </li>

                          <!-- Left ellipsis -->
                          <c:if test="${currentPage > 3}">
                            <li class="page-item disabled">
                              <span class="page-link">...</span>
                            </li>
                          </c:if>

                          <!-- Current page group -->
                          <c:forEach begin="${currentPage - 1}" end="${currentPage + 1}" var="i">
                            <c:if test="${i > 1 && i < totalPages}">
                              <li class="page-item ${i == currentPage ? 'active' : ''}">
                                <a class="page-link" href="#" data-page="${i}">${i}</a>
                              </li>
                            </c:if>
                          </c:forEach>

                          <!-- Right ellipsis -->
                          <c:if test="${currentPage < totalPages - 2}">
                            <li class="page-item disabled">
                              <span class="page-link">...</span>
                            </li>
                          </c:if>

                          <!-- Last page -->
                          <li class="page-item ${currentPage == totalPages ? 'active' : ''}">
                            <a class="page-link" href="#" data-page="${totalPages}">${totalPages}</a>
                          </li>
                        </c:otherwise>
                      </c:choose>

                      <!-- Next Button -->
                      <c:if test="${currentPage < totalPages}">
                        <li class="page-item">
                          <a class="page-link" href="#" data-page="${currentPage + 1}" aria-label="Next">›</a>
                        </li>
                      </c:if>
                    </ul>
                  </nav>
                </c:if>
              </div>
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

        <div class="modal fade" id="flashSaleModal" tabindex="-1" aria-labelledby="flashSaleModalLabel"
          aria-hidden="true">
          <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title" id="flashSaleModalLabel">Vượt số lượng Flash Sale</h5>
              </div>
              <div class="modal-body">
                <p id="flashSaleMessage"></p>
              </div>
              <div class="modal-footer">
                <button type="button" class="button-four w-100" data-bs-dismiss="modal">OK</button>
              </div>
            </div>
          </div>
        </div>

        <!-- Modal edit review -->
        <div class="modal fade" id="editReviewModal" tabindex="-1" aria-labelledby="editReviewModalLabel"
          aria-hidden="true">
          <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content rounded-3 shadow">
              <form id="editReviewForm" enctype="multipart/form-data">
                <div class="modal-header">
                  <h5 class="modal-title fw-bold" id="editReviewModalLabel">Chỉnh sửa Đánh Giá</h5>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                </div>

                <div class="modal-body">
                  <input type="hidden" name="reviewId" id="editReviewId" value="">
                  <div class="mb-3">
                    <label class="fw-semibold">Chất lượng sản phẩm</label>
                    <div class="rating-stars-edit">
                      <input type="radio" name="rating" id="edit-rating-5" value="5" required>
                      <label for="edit-rating-5"><i class="bi bi-star-fill"></i></label>

                      <input type="radio" name="rating" id="edit-rating-4" value="4">
                      <label for="edit-rating-4"><i class="bi bi-star-fill"></i></label>

                      <input type="radio" name="rating" id="edit-rating-3" value="3">
                      <label for="edit-rating-3"><i class="bi bi-star-fill"></i></label>

                      <input type="radio" name="rating" id="edit-rating-2" value="2">
                      <label for="edit-rating-2"><i class="bi bi-star-fill"></i></label>

                      <input type="radio" name="rating" id="edit-rating-1" value="1">
                      <label for="edit-rating-1"><i class="bi bi-star-fill"></i></label>
                    </div>
                  </div>
                  <div class="mb-3">
                    <label class="fw-semibold" for="editReviewComment">Nội dung đánh giá</label>
                    <textarea class="form-control" name="comment" id="editReviewComment" rows="3"
                      placeholder="Hãy chia sẻ trải nghiệm của bạn..."></textarea>
                  </div>

                  <div class="mb-3">
                    <label for="editReviewImages" class="button-four me-2">
                      <i class="bi bi-camera"></i> Thay thế Ảnh (Tối đa 5 ảnh)
                    </label>
                    <small class="text-muted">(Nếu không chọn, ảnh cũ sẽ được giữ lại)</small>
                    <input type="file" id="editReviewImages" name="reviewImages" style="display: none;" multiple
                      accept="image/png, image/jpeg, image/gif, image/webp">
                    <div id="editPreviewImages" class="d-flex flex-wrap mt-2 gap-2">
                    </div>
                  </div>
                </div>

                <div class="modal-footer">
                  <button type="button" class="button-five" data-bs-dismiss="modal">Trở lại</button>
                  <button type="submit" class="button-four" id="submitEditReviewBtn">Lưu thay đổi</button>
                </div>
              </form>
            </div>
          </div>
        </div>

        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
        <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

        <!-- JS riêng của trang -->
        <script src="${ctx}/assets/js/catalog/book_detail.js?v=1.0.2"></script>
        <script src="${ctx}/assets/js/catalog/review-filter-ajax.js?v=1.0.2"></script>
        <script src="${ctx}/assets/js/catalog/comment.js?v=1.0.2"></script>
        <script src="${ctx}/assets/js/catalog/flash_sale_countdown.js?v=1.0.2"></script>
      </body>

      </html>