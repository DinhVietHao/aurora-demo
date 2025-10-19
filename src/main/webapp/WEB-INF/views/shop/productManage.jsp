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
                    <link rel="stylesheet" href="${ctx}/assets/css/admin/adminPage.css?v=1.0.1" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/product.css">
                </head>

                <body class="sb-nav-fixed">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />

                        <div id="layoutSidenav_content">
                            <main>
                                <c:if test="${not empty successMessage}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        ${fn:escapeXml(successMessage)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>

                                <c:if test="${not empty errorMessage}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        ${fn:escapeXml(errorMessage)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>
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
                                                                    <img src="http://localhost:8080/assets/images/catalog/products/${p.primaryImageUrl}"
                                                                        alt="${p.title}" class="product-thumb me-3">
                                                                    <div>
                                                                        <div class="fw-bold">${p.title}</div>
                                                                        <small class="text-muted">
                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${fn:length(p.authors) == 1}">
                                                                                    ${p.authors[0].authorName}
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${fn:length(p.authors) > 1}">
                                                                                    ${p.authors[0].authorName},...
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
                                                            <td>${p.quantity}</td>

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
                                                                    <c:when test="${p.status eq 'PENDING'}">
                                                                        <span class="badge bg-info text-dark">Chờ
                                                                            Duyệt</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="badge bg-dark">Không xác
                                                                            định</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <!-- Thao tác -->
                                                            <td>
                                                                <!-- Xem chi tiết -->
                                                                <button class="btn btn-sm btn-outline-info me-1"
                                                                    title="Xem chi tiết" data-bs-toggle="modal"
                                                                    data-bs-target="#viewProductModal"
                                                                    data-product-id="${p.productId}">
                                                                    <i class="bi bi-eye"></i>
                                                                </button>

                                                                <!-- Chỉnh sửa -->
                                                                <button
                                                                    class="btn btn-sm btn-outline-primary me-1 btn-update"
                                                                    data-bs-toggle="modal"
                                                                    data-bs-target="#updateProductModal"
                                                                    data-product-id="${p.productId}"
                                                                    data-product-title="${p.title}"
                                                                    data-product-description="${p.description}"
                                                                    data-product-originalprice="${p.originalPrice}"
                                                                    data-product-saleprice="${p.salePrice}"
                                                                    data-product-quantity="${p.quantity}"
                                                                    data-product-weight="${p.weight}"
                                                                    data-product-publisherid="${p.publisherId}"
                                                                    data-product-publisheddate="${p.publishedDate}"
                                                                    data-product-translator="${p.bookDetail.translator}"
                                                                    data-product-version="${p.bookDetail.version}"
                                                                    data-product-covertype="${p.bookDetail.coverType}"
                                                                    data-product-pages="${p.bookDetail.pages}"
                                                                    data-product-size="${p.bookDetail.size}"
                                                                    data-product-languagecode="${p.bookDetail.languageCode}"
                                                                    data-product-isbn="${p.bookDetail.isbn}"
                                                                    data-product-authors="<c:forEach var='a'
                                                                        items='${p.authors}' varStatus='st'>${a.authorName}
                                                                        <c:if test='${!st.last}'>|</c:if>
                                                                    </c:forEach>" data-product-categories="<c:forEach var='c'
                                                                        items='${p.categories}' varStatus='st'>
                                                                        ${c.categoryId}<c:if test='${!st.last}'>,</c:if>
                                                                    </c:forEach>" data-product-images="<c:forEach var='img'
                                                                        items='${p.imageUrls}' varStatus='st'>
                                                                        ${img}*${img == p.primaryImageUrl ? '1':'0'}
                                                                        <c:if test='${!st.last}'>|</c:if>
                                                                    </c:forEach>">
                                                                    <i class="bi bi-pencil"></i>
                                                                </button>

                                                                <!-- Xóa -->
                                                                <button class="btn btn-sm btn-outline-danger btn-delete"
                                                                    title="Xóa" data-product-id="${p.productId}"
                                                                    data-product-title="${p.title}"
                                                                    data-bs-toggle="modal"
                                                                    data-bs-target="#confirmDeleteModal">
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


                    <!-- Modal xác nhận xóa -->
                    <div class="modal fade" id="confirmDeleteModal" tabindex="-1"
                        aria-labelledby="confirmDeleteModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header custom-delete">
                                    <h5 class="modal-title" id="confirmDeleteModalLabel">Xác nhận xóa sản phẩm</h5>
                                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                        aria-label="Đóng"></button>
                                </div>

                                <div class="modal-body">
                                    <p id="deleteMessage">Bạn có chắc chắn muốn xóa sản phẩm này không?</p>
                                </div>

                                <div class="modal-footer">
                                    <!-- Nút Hủy: chỉ đóng modal -->
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>

                                    <!-- Nút Xóa -->
                                    <form id="deleteForm" action="/shop/product?action=delete" method="post">
                                        <input type="hidden" name="productId" id="deleteProductId">
                                        <button type="submit" class="btn btn-confirm-delete">Xóa</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
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
                                    <form id="addProductForm" action="/shop/product?action=create" method="POST"
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
                                                <label for="productOriginalPrice" class="form-label">
                                                    Giá gốc <span class="text-danger">*</span>
                                                </label>
                                                <input type="number" min="1" step="0.01" class="form-control"
                                                    id="productOriginalPrice" name="OriginalPrice" placeholder="140000"
                                                    required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="productSalePrice" class="form-label">
                                                    Giá bán <span class="text-danger">*</span>
                                                </label>
                                                <input type="number" min="1" step="0.01" class="form-control"
                                                    id="productSalePrice" name="SalePrice" placeholder="122000"
                                                    required>

                                            </div>
                                            <div class="col-md-3">
                                                <label for="productQuantity" class="form-label">
                                                    Số lượng <span class="text-danger">*</span>
                                                </label>
                                                <input type="number" min="1" class="form-control" id="productQuantity"
                                                    name="Quantity" placeholder="0" required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="weight" class="form-label">
                                                    Khối lượng (gram) <span class="text-danger">*</span>
                                                </label>
                                                <input type="number" min="1" step="0.01" class="form-control"
                                                    id="weight" name="Weight" placeholder="500" required>
                                            </div>
                                            <span id="priceError" class="text-danger mt-1"
                                                style="display:none; font-size: 0.9rem;"></span>
                                        </div>

                                        <!-- Nhà xuất bản & Phát hành -->
                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Nhà xuất bản & Phát hành</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="publisherName" class="form-label">Nhà xuất bản</label>
                                                <input list="publisherList" class="form-control" id="publisherName"
                                                    name="PublisherName" placeholder="Nhập tên nhà xuất bản...">
                                            </div>
                                            <datalist id="publisherList">
                                                <option value="Nhà xuất bản Giáo dục Việt Nam">
                                                <option value="Nhà xuất bản Trẻ">
                                                <option value="Nhà xuất bản Kim Đồng">
                                                <option value="Nhà xuất bản Tổng hợp Thành phố Hồ Chí Minh">
                                                <option value="Nhà Xuất Bản TP.HCM">
                                                <option value="Nhà xuất bản Hội Nhà văn">
                                                <option value="Nhà xuất bản Chính trị Quốc gia Sự thật">
                                                <option value="Nhà xuất bản Phụ nữ Việt Nam">
                                                <option value="Nhà xuất bản Lao Động">
                                                <option value="Nhà xuất bản Hồng Đức">
                                                <option value="Nhà xuất bản Dân Trí">
                                                <option value="Nhà xuất bản Tư pháp">
                                                <option value="Nhà xuất bản Khoa học Xã hội">
                                                <option value="Nhà xuất bản Khoa học và Kỹ thuật">
                                                <option value="Nhà xuất bản Y học">
                                                <option value="Nhà xuất bản Đại học Quốc gia Hà Nội">
                                                <option value="Nhà xuất bản Đại học Quốc gia Thành phố Hồ Chí Minh">
                                                <option value="Nhà xuất bản Xây dựng">
                                                <option value="Nhà xuất bản Thông tin & Truyền thông">
                                                <option value="Nhà xuất bản Tri thức">
                                                <option value="Nhà xuất bản Hà Nội">
                                            </datalist>
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

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="isbn" class="form-label">Mã ISBN <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="isbn" name="isbn"
                                                    placeholder="VD: 9786042109443" required>
                                                <div class="form-text">Mã số của sách.</div>
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
                                                    onclick="addAuthorCreate()">+ Thêm tác giả</button>
                                            </div>
                                        </div>

                                        <div class="row mt-3">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Thể loại</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-12">
                                                <label class="form-label">
                                                    Chọn thể loại <span class="text-danger">*</span>
                                                </label>

                                                <div class="border rounded p-3"
                                                    style="max-height: 220px; overflow-y: auto;">
                                                    <!-- Cột chia nhóm checkbox -->
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="1" id="cat1">
                                                                <label class="form-check-label" for="cat1">Tiểu
                                                                    thuyết</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="2" id="cat2">
                                                                <label class="form-check-label" for="cat2">Truyện
                                                                    ngắn</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="3" id="cat3">
                                                                <label class="form-check-label" for="cat3">Thơ
                                                                    ca</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="4" id="cat4">
                                                                <label class="form-check-label" for="cat4">Văn
                                                                    học</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="5" id="cat5">
                                                                <label class="form-check-label" for="cat5">Truyện
                                                                    tranh</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="6" id="cat6">
                                                                <label class="form-check-label" for="cat6">Light
                                                                    Novel</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="7" id="cat7">
                                                                <label class="form-check-label" for="cat7">Sách giáo
                                                                    khoa</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="8" id="cat8">
                                                                <label class="form-check-label" for="cat8">Sách tham
                                                                    khảo</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="9" id="cat9">
                                                                <label class="form-check-label" for="cat9">Kinh
                                                                    tế</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="10" id="cat10">
                                                                <label class="form-check-label" for="cat10">Tài
                                                                    chính</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="11" id="cat11">
                                                                <label class="form-check-label" for="cat11">Phát triển
                                                                    bản thân</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="12" id="cat12">
                                                                <label class="form-check-label" for="cat12">Lịch
                                                                    sử</label>
                                                            </div>
                                                        </div>

                                                        <div class="col-md-6">
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="13" id="cat13">
                                                                <label class="form-check-label" for="cat13">Chính
                                                                    trị</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="14" id="cat14">
                                                                <label class="form-check-label" for="cat14">Pháp
                                                                    luật</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="15" id="cat15">
                                                                <label class="form-check-label" for="cat15">Khoa
                                                                    học</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="16" id="cat16">
                                                                <label class="form-check-label" for="cat16">Tâm
                                                                    lý</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="17" id="cat17">
                                                                <label class="form-check-label" for="cat17">Y
                                                                    học</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="18" id="cat18">
                                                                <label class="form-check-label" for="cat18">Ẩm
                                                                    thực</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="19" id="cat19">
                                                                <label class="form-check-label" for="cat19">Nuôi dạy
                                                                    con</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="20" id="cat20">
                                                                <label class="form-check-label" for="cat20">Du
                                                                    lịch</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="21" id="cat21">
                                                                <label class="form-check-label" for="cat21">Thời
                                                                    trang</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="22" id="cat22">
                                                                <label class="form-check-label" for="cat22">Nhà
                                                                    cửa</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="23" id="cat23">
                                                                <label class="form-check-label" for="cat23">Nghệ
                                                                    thuật</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="24" id="cat24">
                                                                <label class="form-check-label" for="cat24">Tôn
                                                                    giáo</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="25" id="cat25">
                                                                <label class="form-check-label" for="cat25">Trinh
                                                                    Thám</label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="form-text">
                                                    Chọn một hoặc nhiều thể loại phù hợp với sản phẩm.
                                                </div>
                                            </div>
                                        </div>


                                        <div class="row">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Hình ảnh sản phẩm</h6>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="productImages" class="form-label">Chọn hình ảnh</label>
                                            <input type="file" class="form-control" id="productImages"
                                                name="ProductImages" multiple accept="image/*">
                                            <div class="form-text text-secondary">
                                                Có thể đăng từ <strong>2 đến 20 ảnh</strong>. Mỗi ảnh tối đa <strong>5
                                                    MB</strong>.
                                            </div>
                                            <div id="imageError" class="text-danger mt-1" style="display:none;"></div>
                                        </div>

                                        <!-- Preview -->
                                        <div id="imagePreview" class="row mb-3 g-2"></div>

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
                    <div class="modal fade" id="updateProductModal" tabindex="-1"
                        aria-labelledby="updateProductModalLabel" aria-hidden="true">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="updateProductModalLabel">Update sản phẩm mới</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form id="updateProductForm" action="/products/create?action=create" method="POST"
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
                                                <input type="text" class="form-control" id="productTitleUpdate"
                                                    name="Title" required>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="productDescription" class="form-label">Mô tả sách</label>
                                            <textarea class="form-control" id="productDescriptionUpdate"
                                                name="Description" rows="4"
                                                placeholder="Mô tả chi tiết về nội dung sách..."></textarea>
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
                                                    id="productOriginalPriceUpdate" name="OriginalPrice"
                                                    placeholder="140000" required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="productSalePrice" class="form-label">Giá bán <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" step="0.01" class="form-control"
                                                    id="productSalePriceUpdate" name="SalePrice" placeholder="122000"
                                                    required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="productQuantity" class="form-label">Số lượng tồn kho <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" class="form-control" id="productQuantityUpdate"
                                                    name="Quantity" placeholder="0" required>
                                            </div>
                                            <div class="col-md-3">
                                                <label for="weight" class="form-label">Khối lượng (gram) <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" step="0.01" class="form-control" id="weightUpdate"
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
                                                <select class="form-select" id="publisherIdUpdate" name="PublisherID">
                                                    <option value="">Chọn NXB</option>
                                                    <!-- render danh sách Publisher từ DB -->
                                                    <option value="1">NXB Trẻ</option>
                                                    <option value="2">NXB Giáo dục</option>
                                                </select>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="publishedDate" class="form-label">Ngày phát hành</label>
                                                <input type="date" class="form-control" id="publishedDateUpdate"
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
                                                <input type="text" class="form-control" id="translatorUpdate"
                                                    name="Translator" placeholder="Tên dịch giả (nếu có)">
                                            </div>
                                            <div class="col-md-6">
                                                <label for="version" class="form-label">Phiên bản <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="versionUpdate"
                                                    name="Version" placeholder="Tái bản lần 1" required>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="coverType" class="form-label">Loại bìa <span
                                                        class="text-danger">*</span></label>
                                                <select class="form-select" id="coverTypeUpdate" name="CoverType"
                                                    required>
                                                    <option value="Bìa mềm">Bìa mềm</option>
                                                    <option value="Bìa cứng">Bìa cứng</option>
                                                </select>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="pages" class="form-label">Số trang <span
                                                        class="text-danger">*</span></label>
                                                <input type="number" class="form-control" id="pagesUpdate" name="Pages"
                                                    placeholder="250" required>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="size" class="form-label">Kích thước (Size) <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="sizeUpdate" name="Size"
                                                    placeholder="14x20 cm" required>
                                            </div>

                                            <div class="col-md-6">
                                                <label for="languageCode" class="form-label">Ngôn ngữ <span
                                                        class="text-danger">*</span></label>
                                                <select class="form-select" id="languageCodeUpdate" name="LanguageCode"
                                                    required>
                                                    <option value="vi">Tiếng Việt</option>
                                                    <option value="en">Tiếng Anh</option>
                                                </select>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="isbn" class="form-label">Mã ISBN <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="isbnUpdate" name="isbn"
                                                    placeholder="VD: 9786042109443" required>
                                                <div class="form-text">Mã số của sách.</div>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="authors" class="form-label">
                                                    Tác giả <span class="text-danger">*</span>
                                                </label>

                                                <!-- Vùng chứa các ô nhập tác giả -->
                                                <div id="authors-containerUpdate">
                                                    <div class="input-group mb-2">
                                                        <input type="text" class="form-control" name="authorsUpdate"
                                                            placeholder="Tên tác giả" required>
                                                        <button type="button" class="btn btn-outline-danger"
                                                            onclick="removeAuthor(this)">🗑</button>
                                                    </div>
                                                </div>

                                                <!-- Nút thêm ô nhập -->
                                                <button type="button" class="btn btn-outline-primary btn-sm mt-2"
                                                    onclick="addAuthorUpdate()">+ Thêm tác giả</button>
                                            </div>
                                        </div>

                                        <div class="row mt-3">
                                            <div class="col-12">
                                                <h6 class="text-muted mb-3">Thể loại</h6>
                                            </div>
                                        </div>

                                        <div class="row mb-3">
                                            <div class="col-md-12">
                                                <label class="form-label">
                                                    Chọn thể loại <span class="text-danger">*</span>
                                                </label>

                                                <div class="border rounded p-3"
                                                    style="max-height: 220px; overflow-y: auto;">
                                                    <!-- Cột chia nhóm checkbox -->
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="1" id="cat1">
                                                                <label class="form-check-label" for="cat1">Tiểu
                                                                    thuyết</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="2" id="cat2">
                                                                <label class="form-check-label" for="cat2">Truyện
                                                                    ngắn</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="3" id="cat3">
                                                                <label class="form-check-label" for="cat3">Thơ
                                                                    ca</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="4" id="cat4">
                                                                <label class="form-check-label" for="cat4">Văn
                                                                    học</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="5" id="cat5">
                                                                <label class="form-check-label" for="cat5">Truyện
                                                                    tranh</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="6" id="cat6">
                                                                <label class="form-check-label" for="cat6">Light
                                                                    Novel</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="7" id="cat7">
                                                                <label class="form-check-label" for="cat7">Sách giáo
                                                                    khoa</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="8" id="cat8">
                                                                <label class="form-check-label" for="cat8">Sách tham
                                                                    khảo</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="9" id="cat9">
                                                                <label class="form-check-label" for="cat9">Kinh
                                                                    tế</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="10" id="cat10">
                                                                <label class="form-check-label" for="cat10">Tài
                                                                    chính</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="11" id="cat11">
                                                                <label class="form-check-label" for="cat11">Phát triển
                                                                    bản thân</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="12" id="cat12">
                                                                <label class="form-check-label" for="cat12">Lịch
                                                                    sử</label>
                                                            </div>
                                                        </div>

                                                        <div class="col-md-6">
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="13" id="cat13">
                                                                <label class="form-check-label" for="cat13">Chính
                                                                    trị</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="14" id="cat14">
                                                                <label class="form-check-label" for="cat14">Pháp
                                                                    luật</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="15" id="cat15">
                                                                <label class="form-check-label" for="cat15">Khoa
                                                                    học</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="16" id="cat16">
                                                                <label class="form-check-label" for="cat16">Tâm
                                                                    lý</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="17" id="cat17">
                                                                <label class="form-check-label" for="cat17">Y
                                                                    học</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="18" id="cat18">
                                                                <label class="form-check-label" for="cat18">Ẩm
                                                                    thực</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="19" id="cat19">
                                                                <label class="form-check-label" for="cat19">Nuôi dạy
                                                                    con</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="20" id="cat20">
                                                                <label class="form-check-label" for="cat20">Du
                                                                    lịch</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="21" id="cat21">
                                                                <label class="form-check-label" for="cat21">Thời
                                                                    trang</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="22" id="cat22">
                                                                <label class="form-check-label" for="cat22">Nhà
                                                                    cửa</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="23" id="cat23">
                                                                <label class="form-check-label" for="cat23">Nghệ
                                                                    thuật</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="24" id="cat24">
                                                                <label class="form-check-label" for="cat24">Tôn
                                                                    giáo</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    name="CategoryIDs" value="25" id="cat25">
                                                                <label class="form-check-label" for="cat25">Trinh
                                                                    Thám</label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="form-text">
                                                    Chọn một hoặc nhiều thể loại phù hợp với sản phẩm.
                                                </div>
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
                                            <input type="file" class="form-control" id="productImagesUpdate"
                                                name="ProductImagesUpdate" multiple accept="image/*">
                                            <div class="form-text">Chọn tối đa 5 hình ảnh. Kích thước tối đa mỗi file:
                                                2MB</div>
                                            <div id="imageErrorUpdate" class="text-danger mt-1" style="display:none;">
                                            </div>
                                        </div>
                                        <div id="imagePreviewUpdate" class="row mb-3"></div>

                                        <!-- Nút submit -->
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary"
                                                data-bs-dismiss="modal">Hủy</button>
                                            <button type="submit" form="updateProductForm" class="btn btn-success">
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
                    <script src="${ctx}/assets/js/shop/productManagement.js?v=1.0.1"></script>
                </body>

                </html>