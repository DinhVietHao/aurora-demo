<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Đăng ký mở shop" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">

<head>
  <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
</head>

<body>
  <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

  <main>
    <div class="container my-5">
      <div class="row justify-content-center">
        <div class="col-lg-8">
          <div class="card shadow">
            <div class="card-header bg-primary text-white">
              <h4 class="mb-0"><i class="bi bi-shop"></i> Đăng ký mở shop</h4>
            </div>
            <div class="card-body">
              <p class="text-muted mb-4">
                Điền thông tin bên dưới để đăng ký mở shop của bạn trên Aurora Bookstore
              </p>
              
              <form id="registerShopForm" method="post" action="${ctx}/shop/register">
                <!-- Thông tin cơ bản -->
                <h5 class="mb-3"><i class="bi bi-info-circle"></i> Thông tin cơ bản</h5>
                
                <div class="row mb-3">
                  <div class="col-md-6">
                    <label for="shopName" class="form-label">Tên Shop <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="shopName" name="shopName" required />
                    <div class="invalid-feedback">Vui lòng nhập tên shop</div>
                  </div>
                  <div class="col-md-6">
                    <label for="shopPhone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                    <input type="tel" class="form-control" id="shopPhone" name="shopPhone" required />
                    <div class="invalid-feedback">Vui lòng nhập số điện thoại</div>
                  </div>
                </div>

                <div class="row mb-3">
                  <div class="col-md-6">
                    <label for="shopEmail" class="form-label">Email Shop <span class="text-danger">*</span></label>
                    <input type="email" class="form-control" id="shopEmail" name="shopEmail" required />
                    <div class="invalid-feedback">Vui lòng nhập email hợp lệ</div>
                  </div>
                  <div class="col-md-6">
                    <label for="shopCategory" class="form-label">Danh mục kinh doanh <span class="text-danger">*</span></label>
                    <select class="form-select" id="shopCategory" name="shopCategory" required>
                      <option value="">-- Chọn danh mục --</option>
                      <option value="books">Sách</option>
                      <option value="stationery">Văn phòng phẩm</option>
                      <option value="mixed">Hỗn hợp</option>
                    </select>
                    <div class="invalid-feedback">Vui lòng chọn danh mục</div>
                  </div>
                </div>

                <div class="mb-3">
                  <label for="shopDescription" class="form-label">Mô tả Shop</label>
                  <textarea class="form-control" id="shopDescription" name="shopDescription" rows="3" placeholder="Giới thiệu về shop của bạn..."></textarea>
                </div>

                <!-- Địa chỉ -->
                <h5 class="mb-3 mt-4"><i class="bi bi-geo-alt"></i> Địa chỉ</h5>

                <div class="row mb-3">
                  <div class="col-md-4">
                    <label for="provinceSelect" class="form-label">Tỉnh/Thành phố <span class="text-danger">*</span></label>
                    <select id="provinceSelect" name="province" class="form-select" required>
                      <option value="">-- Chọn Tỉnh/Thành phố --</option>
                    </select>
                    <div class="invalid-feedback">Vui lòng chọn tỉnh/thành phố</div>
                  </div>
                  <div class="col-md-4">
                    <label for="districtSelect" class="form-label">Quận/Huyện <span class="text-danger">*</span></label>
                    <select id="districtSelect" name="district" class="form-select" required>
                      <option value="">-- Chọn Quận/Huyện --</option>
                    </select>
                    <div class="invalid-feedback">Vui lòng chọn quận/huyện</div>
                  </div>
                  <div class="col-md-4">
                    <label for="wardSelect" class="form-label">Phường/Xã <span class="text-danger">*</span></label>
                    <select id="wardSelect" name="ward" class="form-select" required>
                      <option value="">-- Chọn Phường/Xã --</option>
                    </select>
                    <div class="invalid-feedback">Vui lòng chọn phường/xã</div>
                  </div>
                </div>

                <div class="mb-3">
                  <label for="shopAddress" class="form-label">Địa chỉ chi tiết <span class="text-danger">*</span></label>
                  <input type="text" class="form-control" id="shopAddress" name="shopAddress" placeholder="Số nhà, tên đường..." required />
                  <div class="invalid-feedback">Vui lòng nhập địa chỉ chi tiết</div>
                </div>

                <!-- Logo Shop -->
                <h5 class="mb-3 mt-4"><i class="bi bi-image"></i> Logo Shop</h5>
                
                <div class="row mb-3">
                  <div class="col-md-8">
                    <label for="shopLogo" class="form-label">Chọn logo cho shop</label>
                    <input type="file" class="form-control" id="shopLogo" name="shopLogo" accept="image/*" />
                    <div class="form-text">
                      Kích thước tối đa: 2MB. Định dạng: JPG, PNG, GIF
                    </div>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label d-block">Xem trước</label>
                    <div class="border rounded p-2 text-center" style="height: 120px;">
                      <img id="logoPreview" src="${ctx}/assets/images/catalog/products/product-1.png" 
                           alt="Logo preview" class="img-fluid" style="max-height: 100px;" />
                    </div>
                  </div>
                </div>

                <!-- Điều khoản -->
                <div class="mb-3 form-check">
                  <input type="checkbox" class="form-check-input" id="agreeTerms" name="agreeTerms" required />
                  <label class="form-check-label" for="agreeTerms">
                    Tôi đồng ý với <a href="${ctx}/terms" target="_blank">điều khoản và điều kiện</a> của Aurora Bookstore
                    <span class="text-danger">*</span>
                  </label>
                  <div class="invalid-feedback">Bạn phải đồng ý với điều khoản</div>
                </div>

                <!-- Nút submit -->
                <div class="d-flex justify-content-between mt-4">
                  <a href="${ctx}/" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Quay lại
                  </a>
                  <button type="submit" class="btn btn-primary">
                    <i class="bi bi-check-circle"></i> Đăng ký Shop
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>

  <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />

  <%-- Scripts --%>
  <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
  
  <%-- Additional scripts for shop registration --%>
  <script>
    // Preview logo when file is selected
    document.getElementById('shopLogo')?.addEventListener('change', function(e) {
      const file = e.target.files[0];
      if (file) {
        // Validate file size
        if (file.size > 2 * 1024 * 1024) {
          alert('Kích thước file không được vượt quá 2MB');
          this.value = '';
          return;
        }
        
        // Validate file type
        if (!file.type.startsWith('image/')) {
          alert('Vui lòng chọn file hình ảnh');
          this.value = '';
          return;
        }
        
        // Preview image
        const reader = new FileReader();
        reader.onload = function(e) {
          document.getElementById('logoPreview').src = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    });

    // Form validation
    (function() {
      'use strict';
      const form = document.getElementById('registerShopForm');
      
      if (form) {
        form.addEventListener('submit', function(event) {
          if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
          }
          form.classList.add('was-validated');
        }, false);
      }
    })();
  </script>
</body>

</html>
