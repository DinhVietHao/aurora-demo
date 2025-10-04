<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <c:set var="pageTitle" value="Aurora" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Quản lý Sản phẩm - Aurora Bookstore</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                        rel="stylesheet">
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
                    <link rel="stylesheet"
                        href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
                    <link rel="stylesheet" href="https://cdn.jsdelivr.net/simple-datatables@7.1.2/dist/style.min.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/common/globals.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/catalog/home.css?v=1.0.1" />
                    <link rel="stylesheet" href="${ctx}/assets/css/admin/adminPage.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/product.css?v=1.0.1">
                </head>

                <body class="sb-nav-fixed">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />

                        <div id="layoutSidenav_content">
                            <main>
                                <div class="container-fluid px-4">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h1 class="mt-4 product-management-title">Quản lý Sản phẩm</h1>
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item"><a href="home.html">Trang chủ</a></li>
                                                <li class="breadcrumb-item"><a href="adminDashboard.html">Dashboard</a>
                                                </li>
                                                <li class="breadcrumb-item active" aria-current="page">Sản phẩm</li>
                                            </ol>
                                        </nav>
                                    </div>

                                    <!-- Filter and Add Product Section -->
                                    <div class="row mt-4">
                                        <div class="col-12">
                                            <button type="button" class="btn btn-success float-end"
                                                data-bs-toggle="modal" data-bs-target="#addProductModal">
                                                <i class="bi bi-plus-circle me-1"></i>
                                                Thêm sản phẩm
                                            </button>
                                            <br /><br />
                                            <div class="card mb-4">
                                                <div
                                                    class="card-header d-flex justify-content-between align-items-center">
                                                    <div>
                                                        <i class="bi bi-funnel me-1"></i>
                                                        Bộ lọc sản phẩm
                                                    </div>
                                                </div>
                                                <div class="card-body">
                                                    <div class="row">
                                                        <div class="col-md-4">
                                                            <label for="categoryFilter" class="form-label">Tất cả danh
                                                                mục</label>
                                                            <select class="form-select" id="categoryFilter">
                                                                <option value="">Tất cả danh mục</option>
                                                                <option value="van-hoc">Văn học</option>
                                                                <option value="khoa-hoc">Khoa học</option>
                                                                <option value="thieu-nhi">Thiếu nhi</option>
                                                                <option value="ky-thuat">Kỹ thuật</option>
                                                            </select>
                                                        </div>
                                                        <div class="col-md-4">
                                                            <label for="statusFilter" class="form-label">Tất cả trạng
                                                                thái</label>
                                                            <select class="form-select" id="statusFilter">
                                                                <option value="">Tất cả trạng thái</option>
                                                                <option value="active">Đang bán</option>
                                                                <option value="inactive">Ngừng bán</option>
                                                                <option value="out-of-stock">Hết hàng</option>
                                                            </select>
                                                        </div>
                                                        <div class="col-md-4">
                                                            <label for="searchProduct" class="form-label">Tìm
                                                                kiếm</label>
                                                            <div class="input-group">
                                                                <input type="text" class="form-control"
                                                                    id="searchProduct"
                                                                    placeholder="Tìm theo tên sách...">
                                                                <button class="btn btn-outline-secondary" type="button">
                                                                    <i class="bi bi-search"></i>
                                                                </button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Products Table -->
                                    <div class="card mb-4">
                                        <div class="card-header">
                                            <i class="bi bi-table me-1"></i>
                                            Danh sách sản phẩm
                                        </div>
                                        <div class="card-body">
                                            <table id="datatablesSimple" class="table table-striped">
                                                <thead>
                                                    <tr>
                                                        <th>Sản phẩm</th>
                                                        <th>Thể loại</th>
                                                        <th>Giá bán</th>
                                                        <th>Số lượng</th>
                                                        <th>Trạng thái</th>
                                                        <th>Thao tác</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:if test="${empty listProduct}">
                                                        <tr>
                                                            <td colspan="6">
                                                                <div class="alert alert-warning mb-0">
                                                                    Chưa có sản phẩm để hiển thị.
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </c:if>

                                                    <c:forEach var="p" items="${listProduct}">
                                                        <tr>
                                                            <!-- Cột Sản phẩm -->
                                                            <td>
                                                                <div class="d-flex align-items-center">
                                                                    <img src="http://localhost:8080/assets/images/catalog/thumbnails/${p.primaryImageUrl}"
                                                                        alt="${p.title}" class="product-thumb me-3">
                                                                    <div>
                                                                        <div class="fw-bold">${p.title}</div>
                                                                        <small class="text-muted">
                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${fn:length(p.authors) == 1}">
                                                                                    ${p.authors[0].name}
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${fn:length(p.authors) > 1}">
                                                                                    ${p.authors[0].name},...
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    Không có tác giả
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </small>
                                                                    </div>
                                                                </div>
                                                            </td>

                                                            <!-- Cột Thể loại -->
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${fn:length(p.categories) == 1}">
                                                                        ${p.categories[0].name}
                                                                    </c:when>
                                                                    <c:when test="${fn:length(p.categories) > 1}">
                                                                        ${p.categories[0].name},...
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        Không rõ
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>

                                                            <!-- Giá bán -->
                                                            <td>
                                                                <span class="fw-bold text-danger">${p.salePrice}₫</span>
                                                                <c:if test="${p.salePrice lt p.originalPrice}">
                                                                    <span
                                                                        class="text-muted text-decoration-line-through me-1">
                                                                        ${p.originalPrice}₫
                                                                    </span>
                                                                </c:if>
                                                            </td>

                                                            <!-- Số lượng -->
                                                            <td>${p.stock}</td>

                                                            <!-- Trạng thái -->
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${p.status eq 'ACTIVE'}">
                                                                        <span class="badge bg-success">Đang bán</span>
                                                                    </c:when>
                                                                    <c:when test="${p.status eq 'INACTIVE'}">
                                                                        <span class="badge bg-secondary">Ngừng
                                                                            bán</span>
                                                                    </c:when>
                                                                    <c:when test="${p.status eq 'DRAFT'}">
                                                                        <span
                                                                            class="badge bg-info text-dark">Nháp</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="badge bg-dark">Không xác
                                                                            định</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <!-- Thao tác -->1<td>

                                                                <!-- Xem chi tiết -->
                                                                <button class="btn btn-sm btn-outline-info me-1"
                                                                    title="Xem chi tiết" data-bs-toggle="modal"
                                                                    data-bs-target="#viewProductModal"
                                                                    data-product-id="${p.productId}">
                                                                    <i class="bi bi-eye"></i>
                                                                </button>

                                                                <!-- Chỉnh sửa -->
                                                                <button class="btn btn-sm btn-outline-primary me-1"
                                                                    title="Chỉnh sửa" data-bs-toggle="modal"
                                                                    data-bs-target="#updateProductModal"
                                                                    data-product-id="${p.productId}">
                                                                    <i class="bi bi-pencil"></i>
                                                                </button>

                                                                <!-- Xóa -->
                                                                <button class="btn btn-sm btn-outline-danger"
                                                                    title="Xóa" data-product-id="${p.productId}">
                                                                    <i class="bi bi-trash"></i>
                                                                </button>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                        <jsp:include page="/WEB-INF/views/layouts/_pagination.jsp">
                                            <jsp:param name="page" value="${page}" />
                                            <jsp:param name="totalPages" value="${totalPages}" />
                                            <jsp:param name="baseUrl" value="${ctx}/shop/product" />
                                        </jsp:include>
                                    </div>
                                </div>
                            </main>
                        </div>
                    </div>
                    <jsp:include page="/WEB-INF/views/layouts/_footer.jsp?v=1.0.1" />
                    <!-- Add Product Modal -->
                    <div class="modal fade" id="addProductModal" tabindex="-1" aria-labelledby="addProductModalLabel"
                        aria-hidden="true">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="addProductModalLabel">Thêm sản phẩm mới</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form id="addProductForm" action="/products/add" method="POST"
                                        enctype="multipart/form-data">
                                        <!-- Thông tin cơ bản -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Thông tin cơ bản</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-12">
                                                <label for="productTitle" class="form-label">Tên sách <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="productTitle" name="Title"
                                                    placeholder="Nhập tên sách" required>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="productDescription" class="form-label">Mô tả sách</label>
                                            <textarea class="form-control" id="productDescription" name="Description"
                                                rows="4" placeholder="Mô tả chi tiết về nội dung sách..."></textarea>
                                        </div>

                                        <!-- Giá và tồn kho -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Giá và tồn kho</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-3">
                                                <label for="productOriginalPrice" class="form-label">Giá gốc <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" step="0.01" class="form-control"
                                                    id="productOriginalPrice" name="OriginalPrice" placeholder="140000"
                                                    required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="productSalePrice" class="form-label">Giá bán <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" step="0.01" class="form-control"
                                                    id="productSalePrice" name="SalePrice" placeholder="122000"
                                                    required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="productStock" class="form-label">Số lượng tồn kho <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" class="form-control" id="productStock" name="Stock"
                                                    placeholder="0" required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="weight" class="form-label">Khối lượng (gram) <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" step="0.01" class="form-control" id="weight"
                                                    name="Weight" placeholder="500" required>
                                            </div>
                                        </div>

                                        <!-- Nhà xuất bản & Phát hành -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Nhà xuất bản & Phát hành</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="publisherId" class="form-label">Nhà xuất bản
                                                    (PublisherID)</label>
                                                <select class="form-select" id="publisherId" name="PublisherID">
                                                    <option value="">Chọn NXB</option>
                                                    <!-- render danh sách Publisher từ DB -->
                                                    <option value="1">NXB Trẻ</option>
                                                    <option value="2">NXB Giáo dục</option>
                                                </select>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="publishedDate" class="form-label">Ngày phát hành</label>
                                                <input type="date" class="form-control" id="publishedDate"
                                                    name="PublishedDate">
                                            </div>
                                        </div>

                                        <!-- Chi tiết sách (BookDetails) -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Chi tiết sách</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="translator" class="form-label">Dịch giả</label>
                                                <input type="text" class="form-control" id="translator"
                                                    name="Translator" placeholder="Tên dịch giả (nếu có)">
                                            </div>
                                            <div class="col-md-6">
                                                <label for="version" class="form-label">Phiên bản <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="version" name="Version"
                                                    placeholder="Tái bản lần 1" required>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="coverType" class="form-label">Loại bìa <span
                                                        class="text-danger">*</span></label>
                                                <select class="form-select" id="coverType" name="CoverType" required>
                                                    <option value="Bìa mềm">Bìa mềm</option>
                                                    <option value="Bìa cứng">Bìa cứng</option>
                                                </select>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="pages" class="form-label">Số trang <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" class="form-control" id="pages" name="Pages"
                                                    placeholder="250" required>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="size" class="form-label">Kích thước (Size) <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="size" name="Size"
                                                    placeholder="14x20 cm" required>
                                            </div>

                                            <div class="col-md-6">
                                                <label for="languageCode" class="form-label">Ngôn ngữ <span
                                                        class="text-danger">*</span></label>
                                                <select class="form-select" id="languageCode" name="LanguageCode"
                                                    required>
                                                    <option value="vi">Tiếng Việt</option>
                                                    <option value="en">Tiếng Anh</option>
                                                    <option value="fr">Tiếng Pháp</option>
                                                    <option value="jp">Tiếng Nhật</option>
                                                </select>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="authors" class="form-label">
                                                    Tác giả <span class="text-danger">*</span>
                                                </label>

                                                <!-- Vùng chứa các ô nhập tác giả -->
                                                <div id="authors-container">
                                                    <div class="input-group mb-2">
                                                        <input type="text" class="form-control" name="authors"
                                                            placeholder="Tên tác giả" required>
                                                        <button type="button" class="btn btn-outline-danger"
                                                            onclick="removeAuthor(this)">🗑</button>
                                                    </div>
                                                </div>

                                                <!-- Nút thêm ô nhập -->
                                                <button type="button" class="btn btn-outline-primary btn-sm mt-2"
                                                    onclick="addAuthor()">+ Thêm tác giả</button>
                                            </div>
                                        </div>


                                        <!-- Hình ảnh sản phẩm -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Hình ảnh sản phẩm</h6>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="productImages" class="form-label">Chọn hình ảnh</label>
                                            <input type="file" class="form-control" id="productImages"
                                                name="ProductImages[]" multiple accept="image/*">
                                            <div class="form-text">Chọn tối đa 5 hình ảnh. Kích thước tối đa mỗi file:
                                                2MB</div>
                                        </div>
                                        <div id="imagePreview" class="row mb-3"></div>

                                        <!-- Nút submit -->
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary"
                                                data-bs-dismiss="modal">Hủy</button>
                                            <button type="submit" form="addProductForm" class="btn btn-success">
                                                <i class="bi bi-check-circle me-1"></i>
                                                Lưu sản phẩm
                                            </button>
                                        </div>
                                    </form>


                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Update Product Modal -->
                    <div class="modal fade" id="updateProductModal" tabindex="-1" aria-labelledby="addProductModalLabel"
                        aria-hidden="true">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="addProductModalLabel">Update sản phẩm mới</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form id="addProductForm" action="/products/add" method="POST"
                                        enctype="multipart/form-data">
                                        <!-- Thông tin cơ bản -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Thông tin cơ bản</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-12">
                                                <label for="productTitle" class="form-label">Tên sách <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="productTitle" name="Title"
                                                    placeholder="Nhập tên sách" required>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="productDescription" class="form-label">Mô tả sách</label>
                                            <textarea class="form-control" id="productDescription" name="Description"
                                                rows="4" placeholder="Mô tả chi tiết về nội dung sách..."></textarea>
                                        </div>

                                        <!-- Giá và tồn kho -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Giá và tồn kho</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-3">
                                                <label for="productOriginalPrice" class="form-label">Giá gốc <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" step="0.01" class="form-control"
                                                    id="productOriginalPrice" name="OriginalPrice" placeholder="140000"
                                                    required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="productSalePrice" class="form-label">Giá bán <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" step="0.01" class="form-control"
                                                    id="productSalePrice" name="SalePrice" placeholder="122000"
                                                    required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="productStock" class="form-label">Số lượng tồn kho <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" class="form-control" id="productStock" name="Stock"
                                                    placeholder="0" required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="weight" class="form-label">Khối lượng (gram) <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" step="0.01" class="form-control" id="weight"
                                                    name="Weight" placeholder="500" required>
                                            </div>
                                        </div>

                                        <!-- Nhà xuất bản & Phát hành -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Nhà xuất bản & Phát hành</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="publisherId" class="form-label">Nhà xuất bản
                                                    (PublisherID)</label>
                                                <select class="form-select" id="publisherId" name="PublisherID">
                                                    <option value="">Chọn NXB</option>
                                                    <!-- render danh sách Publisher từ DB -->
                                                    <option value="1">NXB Trẻ</option>
                                                    <option value="2">NXB Giáo dục</option>
                                                </select>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="publishedDate" class="form-label">Ngày phát hành</label>
                                                <input type="date" class="form-control" id="publishedDate"
                                                    name="PublishedDate">
                                            </div>
                                        </div>

                                        <!-- Chi tiết sách (BookDetails) -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Chi tiết sách</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="translator" class="form-label">Dịch giả</label>
                                                <input type="text" class="form-control" id="translator"
                                                    name="Translator" placeholder="Tên dịch giả (nếu có)">
                                            </div>
                                            <div class="col-md-6">
                                                <label for="version" class="form-label">Phiên bản <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="version" name="Version"
                                                    placeholder="Tái bản lần 1" required>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="coverType" class="form-label">Loại bìa <span
                                                        class="text-danger">*</span></label>
                                                <select class="form-select" id="coverType" name="CoverType" required>
                                                    <option value="Bìa mềm">Bìa mềm</option>
                                                    <option value="Bìa cứng">Bìa cứng</option>
                                                </select>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="pages" class="form-label">Số trang <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" class="form-control" id="pages" name="Pages"
                                                    placeholder="250" required>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="size" class="form-label">Kích thước (Size) <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="size" name="Size"
                                                    placeholder="14x20 cm" required>
                                            </div>

                                            <div class="col-md-6">
                                                <label for="languageCode" class="form-label">Ngôn ngữ <span
                                                        class="text-danger">*</span></label>
                                                <select class="form-select" id="languageCode" name="LanguageCode"
                                                    required>
                                                    <option value="vi">Tiếng Việt</option>
                                                    <option value="en">Tiếng Anh</option>
                                                </select>
                                            </div>
                                        </div>

                                        <!-- Hình ảnh sản phẩm -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Hình ảnh sản phẩm</h6>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="productImages" class="form-label">Chọn hình ảnh</label>
                                            <input type="file" class="form-control" id="productImages"
                                                name="ProductImages[]" multiple accept="image/*">
                                            <div class="form-text">Chọn tối đa 5 hình ảnh. Kích thước tối đa mỗi file:
                                                2MB</div>
                                        </div>
                                        <div id="imagePreview" class="row mb-3"></div>

                                        <!-- Nút submit -->
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary"
                                                data-bs-dismiss="modal">Hủy</button>
                                            <button type="submit" form="addProductForm" class="btn btn-success">
                                                <i class="bi bi-check-circle me-1"></i>
                                                Lưu sản phẩm
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                    <script
                        src="https://cdn.jsdelivr.net/simple-datatables@7.1.2/dist/umd/simple-datatables.min.js"></script>
                    <script src="${ctx}/assets/js/shop/scripts.js"></script>
                    <script src="${ctx}/assets/js/shop/datatables-simple-demo.js"></script>
                    <script src="${ctx}/assets/js/shop/productManagement.js"></script>
                </body>

                </html>