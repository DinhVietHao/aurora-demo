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

    <%-- CSS của thông báo Toast --%>
    <link rel="stylesheet" href="${ctx}/assets/css/common/toast.css?v=1.0.1" />
  </head>

  <body>
    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

    <div class="container book-detail mt-3">
      <div class="row justify-content-evenly">
        <%-- HÌNH ẢNH & NÚT MUA --%>
        <div class="col-md-5">
          <div class="book-detail-images">
            <div class="product-image mb-3">
              <img
                id="mainImage"
                src="${ctx}/assets/images/catalog/thumbnails/${product.images[0].imageUrl}"
                alt="Sách"
                class="img-fluid border"
              />
            </div>

            <div class="row g-2 mb-3">
              <c:forEach var="img" items="${product.images}" varStatus="s">
                <c:if test="${!s.last}">
                  <div class="col-3">
                    <img
                      src="${ctx}/assets/images/catalog/thumbnails/${img.imageUrl}"
                      class="img-fluid border thumbnail ${s.first ? 'active' : ''}"
                      alt=""
                    />
                  </div>
                </c:if>
              </c:forEach>
            </div>

            <div class="row gap-2 justify-content-evenly">
              <button
                class="button-two col-lg-5"
                id="add-to-cart"
                data-product-id="${product.productId}"
              >
                <i class="bi bi-cart3"></i> Thêm vào giỏ hàng
              </button>
              <button class="button-three col-lg-5">Mua Ngay</button>
            </div>
          </div>
        </div>

        <%-- THÔNG TIN CHÍNH --%>
        <div class="col-md-7">
          <div class="book-detail-header">
            <h6 class="author">Tác giả: <c:out value="${product.bookDetail.author}" /></h6>
            <h4 class="title"><c:out value="${product.title}" /></h4>

            <div class="mb-2 rating">
              <i class="bi bi-star-fill text-warning small"></i>
              <i class="bi bi-star-fill text-warning small"></i>
              <i class="bi bi-star-fill text-warning small"></i>
              <i class="bi bi-star-fill text-warning small"></i>
              <i class="bi bi-star-half text-warning small"></i>
              <span>
                4.5 (124) |
                <c:out value="${product.soldCount}" />
              </span>
            </div>

            <div class="mb-3">
              <span class="price"
                ><fmt:formatNumber
                  value="${product.salePrice}"
                  type="currency"
                  currencySymbol="đ"
                  groupingUsed="true"
              /></span>
              <c:if test="${percentDiscount != 0}"> 
                <span class="discount">- <c:out value="${percentDiscount}" />%</span>
                <span class="text-muted text-decoration-line-through">
                  <fmt:formatNumber
                    value="${product.originalPrice}"
                    type="currency"
                    currencySymbol="đ"
                    groupingUsed="true"
                  />
                </span>
              </c:if>
            </div>
          </div>

          <%-- THÔNG TIN CHI TIẾT (BookDetails) --%>
          <div class="book-information my-3">
            <div class="book-information-header">Thông tin chi tiết</div>
            <div class="book-information-body">
              <div class="row mb-1 book-information-box">
                <div class="col-5 text-muted">Phiên bản sách</div>
                <div class="col-7"><c:out value="${product.bookDetail.version}" /></div>
              </div>
              <div class="row mb-1 book-information-box">
                <div class="col-5 text-muted">Công ty phát hành</div>
                <div class="col-7"><c:out value="${product.publisherString}" /></div>
              </div>
              <div class="row mb-1 book-information-box">
                <div class="col-5 text-muted">Ngày xuất bản</div>
                <div class="col-7"><c:out value="${product.publishedDate}" /></div>
              </div>
              <div class="row mb-1 book-information-box">
                <div class="col-5 text-muted">Kích thước</div>
                <div class="col-7"><c:out value="${product.bookDetail.size}" /></div>
              </div>
              <div class="row mb-1 book-information-box">
                <div class="col-5 text-muted">Dịch giả</div>
                <div class="col-7"><c:out value="${product.bookDetail.translator}" /></div>
              </div>
              <div class="row mb-1 book-information-box">
                <div class="col-5 text-muted">Loại bìa</div>
                <div class="col-7"><c:out value="${product.bookDetail.coverType}" /></div>
              </div>
              <div class="row mb-1 book-information-box">
                <div class="col-5 text-muted">Số trang</div>
                <div class="col-7"><c:out value="${product.bookDetail.pages}" /></div>
              </div>
              <div class="row mb-1 book-information-box">
                <div class="col-5 text-muted">Nhà xuất bản</div>
                <div class="col-7"><c:out value="${product.publisherString}" /></div>
              </div>
            </div>
          </div>

          <%-- MÔ TẢ --%>
          <div class="mt-4">
            <div class="book-description">
              <div class="book-description-header">Mô tả sản phẩm</div>
              <div class="book-description-body">
                <p class="fw-medium"><c:out value="${product.title}" /></p>
                <p id="moreText">
                  <c:out value="${product.description}" />
                </p>
                <div class="gradient"></div>
              </div>
              <a
                class="d-block mt-2 text-primary text-center cursor-pointer"
                id="more"
              >
                Xem thêm
              </a>
            </div>
          </div>
        </div>
      </div>

      <%-- ĐÁNH GIÁ TỔNG QUAN (placeholder, sau sẽ bind từ DB) --%>
      <div class="row mt-4">
        <div class="col-12">
          <div class="book-review">
            <div class="book-review-header">Đánh giá sản phẩm</div>
            <div class="book-review-body">
              <div class="row align-items-center ">
                <div class="col-md-2">
                  <h2>0/5</h2>
                  <i class="bi bi-star"></i><i class="bi bi-star"></i><i class="bi bi-star"></i>
                  <i class="bi bi-star"></i><i class="bi bi-star"></i>
                  <p>(0 đánh giá)</p>
                </div>
                <div class="col-md-4"><%-- thanh phân bố sao: TODO bind dữ liệu --%></div>
                <div class="col-md-6 text-center">
                  <p class="text-muted m-0">Chỉ có thành viên mới có thể viết nhận xét.</p>
                  <p class="text-muted">
                    Vui lòng <a href="${ctx}/auth/login">đăng nhập</a> /
                    <a href="${ctx}/auth/register">đăng ký</a>.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <%-- CAROUSEL: AURORA GIỚI THIỆU (tái dùng thẻ sản phẩm) --%>
      <div class="book-introduction container">
        <h5 class="book-introduction-title">Aurora giới thiệu</h5>
        <jsp:include page="/WEB-INF/views/catalog/books/partials/_intro_carousel.jsp">
          <jsp:param name="carouselId" value="bookIntroduction" />
        </jsp:include>
      </div>
      
    </div>

    <%-- Toast notification Add To Cart --%>
    <div id="notify-toast"></div>

    <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

    <%-- JS của thông báo Toast --%>
    <script src="${ctx}/assets/js/common/toast.js?v=1.0.1"></script>

    <%-- JS riêng của trang --%>
    <script src="${ctx}/assets/js/catalog/book_detail.js?v=1.0.1"></script>

    <%-- Send AJAX to controller --%>
    <script>
      const addTocartBtn = document.getElementById("add-to-cart");
      addTocartBtn.addEventListener("click", () => {
        const productId = addTocartBtn.dataset.productId;
        fetch("add-to-cart", {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
          body: "productId=" + productId,
        })
          .then((res) => res.json())
          .then((data) => {
            if (data.success) {
              toast({
                title: "Thành công!",
                message: data.message,
                type: "success",
                duration: 3000,
              });
              const cartCountBadge = document.getElementById("cartCountBadge");
              if (cartCountBadge) {
                cartCountBadge.innerText = data.cartCount;
              }
            } else {
              toast({
                title: "Có lỗi xảy ra",
                message: data.message,
                type: "error",
                duration: 3000,
              });
            }
          });
      });
    </script>
  </body>
</html>
