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

        <!-- CSS của thông báo Toast -->
        <link rel="stylesheet" href="${ctx}/assets/css/common/toast.css" />
      </head>

      <body>
        <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

        <div class="container book-detail mt-3">
          <div class="row justify-content-evenly">
            <!-- HÌNH ẢNH & NÚT MUA -->
            <div class="col-md-5">
              <div class="book-detail-images">
                <div class="product-image mb-3">
                  <img id="mainImage" src="${ctx}/assets/images/catalog/thumbnails/${product.images[0].url}" alt="Sách"
                    class="img-fluid border" />
                </div>

                <div class="row g-2 mb-3">
                  <c:forEach var="img" items="${product.images}" varStatus="s">
                    <c:if test="${!s.last}">
                      <div class="col-3">
                        <img src="${ctx}/assets/images/catalog/thumbnails/${img.url}"
                          class="img-fluid border thumbnail ${s.first ? 'active' : ''}" alt="" />
                      </div>
                    </c:if>
                  </c:forEach>
                </div>

                <div class="row gap-2 justify-content-evenly">
                  <button class="button-two col-lg-5" id="add-to-cart" data-product-id="${product.productId}">
                    <i class="bi bi-cart3"></i> Thêm vào giỏ hàng
                  </button>
                  <button class="button-three col-lg-5">Mua Ngay</button>
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
                  const addToCartBtn = document.getElementById("add-to-cart");
                  addToCartBtn.addEventListener("click", () => {
                    const productId = addToCartBtn.dataset.productId;
                    fetch("/cart/add", {
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