package com.group01.aurora_demo.shop.controller;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import com.group01.aurora_demo.catalog.dao.AuthorDAO;
import com.group01.aurora_demo.catalog.dao.BookDetailDAO;
import com.group01.aurora_demo.catalog.dao.CategoryDAO;
import com.group01.aurora_demo.catalog.dao.FlashSaleDAO;
import com.group01.aurora_demo.catalog.dao.ImageDAO;
import com.group01.aurora_demo.catalog.model.Author;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.catalog.model.ProductImage;
import com.group01.aurora_demo.catalog.model.Category;
import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.model.BookDetail;
import com.group01.aurora_demo.catalog.dao.PublisherDAO;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;

@WebServlet("/shop/product")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 20)
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            response.sendRedirect("/home");
            return;
        }

        String action = request.getParameter("action");
        String productStatus = request.getParameter("productStatus");
        if(productStatus == null){
            productStatus = "all";
        }
        if (action == null)
            action = "view";

        String message = request.getParameter("message");
        String error = request.getParameter("error");

        if ("delete_success".equals(message)) {
            request.setAttribute("successMessage", "Đã xóa sản phẩm thành công!");
        }
        if ("delete_failed".equals(error)) {
            request.setAttribute("errorMessage",
                    "Không thể xóa sản phẩm vì đang trong Flash Sale, đang trong đơn hàng,đang hoạt động hoặc đang ngừng bán.");
        }
        if ("create_success".equals(message)) {
            request.setAttribute("successMessage",
                    "Đã thêm sản phẩm thành công.");
        }
        if ("load_failed".equals(error)) {
            request.setAttribute("errorMessage",
                    "Load xem chi tiết bị lỗi gián đoạn");
        }
        if ("update_success".equals(message)) {
            request.setAttribute("successMessage",
                    "Thay đổi sản phẩm thành công.");
        }
        if ("change_status_success".equals(message)) {
            request.setAttribute("successMessage",
                    "Chuyển trạng thái ngừng kinh doanh sản phẩm thành công.");
        }
        if ("change_status_failed".equals(error)) {
            request.setAttribute("errorMessage",
                    "Chuyển trạng thái ngừng kinh doanh sản phẩm thất bại.");
        }
        if ("change_status_error".equals(error)) {
            request.setAttribute("errorMessage",
                    "Không thể chuyển trạng thái ngừng bán, vì sản phẩm đang nằm trong flash sale hoặc đang trong đơn hàng");
        }
        ShopDAO shopDAO = new ShopDAO();
        ProductDAO productDAO = new ProductDAO();
        FlashSaleDAO flashSaleDAO = new FlashSaleDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        try {
            switch (action) {
                case "view":
                    long shopId = shopDAO.getShopIdByUserId(user.getId());

                    List<Product> listProduct = productDAO.getProductsByShopId(shopId);
                    List<Category> listCategoryShop = categoryDAO.getCategoriesByShopId(shopId);

                    request.setAttribute("listCategoryShop", listCategoryShop);
                    request.setAttribute("listProduct", listProduct);
                    request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request, response);
                    break;
                case "getProduct":
                    try {
                        long productId = Long.parseLong(request.getParameter("id"));
                        Product product = productDAO.getProductById(productId);

                        if (product == null) {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            response.getWriter().write("{\"error\": \"Sản phẩm không tồn tại\"}");
                            return;
                        }
                        String updateMode = "NONE";
                        boolean isInOrder = productDAO.existsProductInActiveOrders(productId);
                        boolean isInFlashSale = flashSaleDAO.isProductInCurrentFlashSale(productId,
                                LocalDateTime.now());

                        if ("PENDING".equalsIgnoreCase(product.getStatus())
                                || "INACTIVE".equalsIgnoreCase(product.getStatus())) {
                            updateMode = "FULL";
                        } else if ("ACTIVE".equalsIgnoreCase(product.getStatus())) {
                            if (!isInFlashSale) {
                                updateMode = "PARTIAL";
                            } else {
                                updateMode = "NONE";
                            }
                        }
                        Map<String, Object> responseData = new HashMap<>();
                        responseData.put("product", product);
                        responseData.put("updateMode", updateMode);
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                                    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

                                    @Override
                                    public void write(JsonWriter out, LocalDateTime value) throws IOException {
                                        out.value(value != null ? value.format(formatter) : null);
                                    }

                                    @Override
                                    public LocalDateTime read(JsonReader in) throws IOException {
                                        return LocalDateTime.parse(in.nextString(), formatter);
                                    }
                                })
                                .create();

                        String json = gson.toJson(responseData);

                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(json);
                    } catch (Exception e) {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().write("{\"error\": \"Lỗi server\"}");
                    }
                    break;
                case "detail":
                    try {
                        long productId = Long.parseLong(request.getParameter("productId"));
                        Product product = productDAO.getProductById(productId);
                        if (product == null) {
                            request.setAttribute("errorMessage", "Không tìm thấy sản phẩm có ID: " + productId);
                            request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp")
                                    .forward(request, response);
                            return;
                        }
                        request.setAttribute("product", product);
                        request.getRequestDispatcher("/WEB-INF/views/shop/productDetail.jsp")
                                .forward(request, response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "Load xem chi tiết bị lỗi " + e.getMessage());
                        request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request,
                                response);
                    }
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action: " + action);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            response.sendRedirect("/home");
            return;
        }

        String action = request.getParameter("action");
        ShopDAO shopDAO = new ShopDAO();
        ProductDAO productDAO = new ProductDAO();
        ImageDAO imageDAO = new ImageDAO();
        AuthorDAO authorDAO = new AuthorDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        BookDetailDAO bookDetailDAO = new BookDetailDAO();
        FlashSaleDAO flashSaleDAO = new FlashSaleDAO();
        switch (action) {
            case "create":

                try {

                    long shopId = shopDAO.getShopIdByUserId(user.getId());
                    String title = request.getParameter("Title");
                    String description = request.getParameter("Description");
                    double originalPrice = Double.parseDouble(request.getParameter("OriginalPrice"));
                    double salePrice = Double.parseDouble(request.getParameter("SalePrice"));
                    int quantity = Integer.parseInt(request.getParameter("Quantity"));
                    double weight = Double.parseDouble(request.getParameter("Weight"));
                    String translator = request.getParameter("Translator");
                    String version = request.getParameter("Version");
                    String coverType = request.getParameter("CoverType");
                    int pages = Integer.parseInt(request.getParameter("Pages"));
                    String size = request.getParameter("Size");
                    String languageCode = request.getParameter("LanguageCode");
                    String isbn = request.getParameter("ISBN");

                    String[] authorNames = request.getParameterValues("authors");
                    String[] categoryIdParams = request.getParameterValues("CategoryIDs");

                    String publisherName = request.getParameter("PublisherName");
                    String publishedDateStr = request.getParameter("PublishedDate");
                    Date publishedDate = (publishedDateStr != null && !publishedDateStr.isEmpty())
                            ? Date.valueOf(publishedDateStr)
                            : null;

                    if (salePrice > originalPrice) {
                        throw new ServletException("Giá bán phải nhỏ hơn hoặc bằng giá gốc.");
                    }

                    PublisherDAO publisherDAO = new PublisherDAO();
                    Long publisherId = null;
                    if (publisherName != null && !publisherName.isBlank()) {
                        publisherId = publisherDAO.findOrCreatePublisher(publisherName.trim());
                    }

                    // ===== Xử lý upload ảnh =====
                    List<String> imagePaths = imageDAO.handleImageUpload(request);

                    // ===== Tạo Product =====
                    Product product = new Product();
                    product.setShopId(shopId);
                    product.setTitle(title);
                    product.setDescription(description);
                    product.setOriginalPrice(originalPrice);
                    product.setSalePrice(salePrice);
                    product.setQuantity(quantity);
                    product.setWeight(weight);
                    product.setPublisherId(publisherId);
                    product.setPublishedDate(publishedDate);
                    product.setImageUrls(imagePaths);
                    product.setPrimaryImageUrl(imagePaths.get(0));

                    // ===== Tạo BookDetail =====
                    BookDetail bookDetail = new BookDetail();
                    bookDetail.setTranslator(translator);
                    bookDetail.setVersion(version);
                    bookDetail.setCoverType(coverType);
                    bookDetail.setPages(pages);
                    bookDetail.setSize(size);
                    bookDetail.setLanguageCode(languageCode);
                    bookDetail.setIsbn(isbn);
                    product.setBookDetail(bookDetail);

                    // ===== Tác giả =====
                    List<Author> authors = new ArrayList<>();
                    if (authorNames != null) {
                        for (String raw : authorNames) {
                            if (raw == null)
                                continue;
                            String name = raw.trim();
                            if (name.isEmpty())
                                continue;
                            Long id = productDAO.findAuthorIdByName(name);
                            if (id == null) {
                                id = productDAO.insertAuthor(name);
                            }
                            Author a = new Author();
                            a.setAuthorId(id);
                            a.setAuthorName(name);
                            authors.add(a);
                        }
                    }
                    product.setAuthors(authors);

                    // ===== Thể loại =====
                    List<Category> categoryList = new ArrayList<>();
                    if (categoryIdParams != null) {
                        for (String cid : categoryIdParams) {
                            if (cid != null && !cid.isBlank()) {
                                Category c = new Category();
                                c.setCategoryId(Long.parseLong(cid));
                                categoryList.add(c);
                            }
                        }
                    }
                    product.setCategories(categoryList);
                    if (productDAO.insertProduct(product) != 0) {
                        response.sendRedirect(
                                request.getContextPath() + "/shop/product?action=view&message=create_success");
                    } else {
                        request.setAttribute("errorMessage", "Không thể thêm sản phẩm?");
                        request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request,
                                response);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Không thể thêm sản phẩm: " + e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request, response);
                }

                break;
            case "delete":
                try {
                    long productId = Long.parseLong(request.getParameter("productId"));

                    boolean success = productDAO.deleteProduct(productId);

                    if (success) {
                        response.sendRedirect(
                                request.getContextPath() + "/shop/product?action=view&message=delete_success");
                    } else {
                        response.sendRedirect(
                                request.getContextPath() + "/shop/product?action=view&error=delete_failed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình xóa sản phẩm.");
                    request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request, response);
                }
                break;
            case "toggleStatus":
                try {
                    long productId = Long.parseLong(request.getParameter("productId"));
                    String newStatus = request.getParameter("newStatus");
                    if (newStatus.equalsIgnoreCase("INACTIVE")) {
                        boolean isInOrder = productDAO.existsProductInActiveOrders(productId);
                        boolean isInFlashSale = flashSaleDAO.isProductInCurrentFlashSale(productId,
                                LocalDateTime.now());
                        if (!isInFlashSale && !isInOrder) {
                            boolean updateStatus = productDAO.updateProductStatus(productId, newStatus);
                            if (updateStatus) {
                                response.sendRedirect(
                                        request.getContextPath()
                                                + "/shop/product?action=view&message=change_status_success");
                            } else {
                                response.sendRedirect(
                                        request.getContextPath()
                                                + "/shop/product?action=view&error=change_status_failed");
                            }
                        } else {
                            response.sendRedirect(
                                    request.getContextPath()
                                            + "/shop/product?action=view&error=change_status_error");
                        }
                    } else if ("PENDING".equalsIgnoreCase(newStatus)) {
                        boolean updateStatus = productDAO.updateProductStatus(productId, newStatus);
                        if (updateStatus) {
                            response.sendRedirect(
                                    request.getContextPath()
                                            + "/shop/product?action=view&message=change_status_success");
                        } else {
                            response.sendRedirect(
                                    request.getContextPath()
                                            + "/shop/product?action=view&error=change_status_failed");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình chuyển trạng thái sản phẩm.");
                    request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request, response);
                }
                break;
            case "update":
                try {
                    long productId = Long.parseLong(request.getParameter("productId"));

                    String updateMode = request.getParameter("updateMode");
                    if (updateMode == null || updateMode.isBlank()) {
                        updateMode = "FULL";
                    }
                    Product product = productDAO.getProductById(productId);
                    if ("FULL".equals(updateMode)) {
                        String title = request.getParameter("Title");
                        String description = request.getParameter("Description");
                        double originalPrice = Double.parseDouble(request.getParameter("OriginalPrice"));
                        double salePrice = Double.parseDouble(request.getParameter("SalePrice"));
                        int quantity = Integer.parseInt(request.getParameter("Quantity"));
                        double weight = Double.parseDouble(request.getParameter("Weight"));
                        String publisherName = request.getParameter("PublisherName");
                        String publishedDateStr = request.getParameter("PublishedDate");
                        Date publishedDate = (publishedDateStr != null && !publishedDateStr.isEmpty())
                                ? Date.valueOf(publishedDateStr)
                                : null;

                        String translator = request.getParameter("Translator");
                        String version = request.getParameter("Version");
                        String coverType = request.getParameter("CoverType");
                        int pages = Integer.parseInt(request.getParameter("Pages"));
                        String size = request.getParameter("Size");
                        String languageCode = request.getParameter("LanguageCode");
                        String isbn = request.getParameter("ISBN");

                        String[] authorNames = request.getParameterValues("authorsUpdate");
                        String[] categoryIds = request.getParameterValues("CategoryIDs");

                        // --- 2️⃣ Lấy đối tượng product hiện tại

                        if (product == null) {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            request.setAttribute("errorMessage", "Không tìm thấy sản phẩm để cập nhật.");
                            request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request,
                                    response);
                            return;
                        }

                        PublisherDAO publisherDAO = new PublisherDAO();
                        Long publisherId = null;
                        if (publisherName != null && !publisherName.isBlank()) {
                            publisherId = publisherDAO.findOrCreatePublisher(publisherName.trim());
                        }

                        product.setTitle(title);
                        product.setDescription(description);
                        product.setOriginalPrice(originalPrice);
                        product.setSalePrice(salePrice);
                        product.setQuantity(quantity);
                        product.setWeight(weight);
                        product.setPublisherId(publisherId);
                        product.setPublishedDate(publishedDate);

                        BookDetail bookDetail = new BookDetail();
                        bookDetail.setProductId(productId);
                        bookDetail.setTranslator(translator);
                        bookDetail.setVersion(version);
                        bookDetail.setCoverType(coverType);
                        bookDetail.setPages(pages);
                        bookDetail.setSize(size);
                        bookDetail.setLanguageCode(languageCode);
                        bookDetail.setIsbn(isbn);
                        product.setBookDetail(bookDetail);

                        authorDAO.deleteAuthorsByProductId(productId);
                        if (authorNames != null) {
                            for (String raw : authorNames) {
                                if (raw != null && !raw.trim().isEmpty()) {
                                    String name = raw.trim();
                                    Long id = productDAO.findAuthorIdByName(name);
                                    if (id == null)
                                        id = productDAO.insertAuthor(name);
                                    authorDAO.addAuthorToProduct(productId, id);
                                }
                            }
                        }

                        categoryDAO.deleteCategoriesByProductId(productId);
                        if (categoryIds != null) {
                            for (String cid : categoryIds) {
                                if (cid != null && !cid.isBlank()) {
                                    categoryDAO.addCategoryToProduct(productId, Long.parseLong(cid));
                                }
                            }
                        }
                    } else if ("PARTIAL".equals(updateMode)) {
                        String description = request.getParameter("Description");
                        double originalPrice = Double.parseDouble(request.getParameter("OriginalPrice"));
                        double salePrice = Double.parseDouble(request.getParameter("SalePrice"));
                        int quantity = Integer.parseInt(request.getParameter("Quantity"));
                        String version = request.getParameter("Version");
                        if (product == null) {
                            request.setAttribute("errorMessage", "Không tìm thấy sản phẩm để cập nhật.");
                            request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request,
                                    response);
                            return;
                        }
                        product.setDescription(description);
                        product.setOriginalPrice(originalPrice);
                        product.setSalePrice(salePrice);
                        product.setQuantity(quantity);

                        BookDetail bookDetail = bookDetailDAO.getBookDetailByProductId(productId);
                        bookDetail.setVersion(version);

                        product.setBookDetail(bookDetail);
                    } else {
                        request.setAttribute("errorMessage",
                                "Không thể xóa sản phẩm vì đang trong Flash Sale, đang trong đơn hàng hoặc đang hoạt động.");
                        request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request,
                                response);
                        return;
                    }
                    // --- 6️⃣ Xử lý ảnh:

                    // Load existing images (to map ids -> urls)
                    List<ProductImage> existingImages = imageDAO.getImagesByProductId(productId);
                    // Map id->url for existing
                    Map<String, String> existingIdToUrl = existingImages.stream()
                            .collect(Collectors.toMap(i -> String.valueOf(i.getImageId()), ProductImage::getUrl));

                    // Collect submitted existing image ids from form (hidden inputs created by JS)
                    String[] existingIdsFromForm = request.getParameterValues("existingImageIds");
                    Set<String> keptExistingIds = new HashSet<>();
                    if (existingIdsFromForm != null) {
                        keptExistingIds.addAll(Arrays.asList(existingIdsFromForm));
                    }

                    // 6.2: Upload new images (accept both ProductImages and productImagesUpdate
                    // names)
                    List<String> newUploadedUrls = imageDAO.handleImageUpload(request);

                    // Insert new images (non-primary for now) and collect their urls
                    List<String> insertedNewUrls = new ArrayList<>();
                    for (String newUrl : newUploadedUrls) {
                        imageDAO.insertImage(productId, newUrl, false);
                        insertedNewUrls.add(newUrl);
                    }

                    // 6.3: Removed images param may contain ids (from JS removedImageIds hidden) or
                    // urls separated by comma
                    String removedImagesParam = request.getParameter("removedImageIds");
                    if (removedImagesParam == null || removedImagesParam.isBlank()) {
                        // older naming fallback
                        removedImagesParam = request.getParameter("RemovedImages");
                    }
                    if (removedImagesParam != null && !removedImagesParam.isBlank()) {
                        String[] removedArray = removedImagesParam.split(",");
                        for (String token : removedArray) {
                            token = token.trim();
                            if (token.isEmpty())
                                continue;
                            // try parse as id
                            try {
                                long imgId = Long.parseLong(token);
                                // delete by id (new method)
                                imageDAO.deleteImageById(imgId);
                            } catch (NumberFormatException nfe) {
                                // not an id, try delete by url
                                imageDAO.deleteImageByUrl(token);
                            }
                        }
                    }

                    // 6.4: Any existing images not present in keptExistingIds should be deleted
                    for (ProductImage pi : existingImages) {
                        String sid = String.valueOf(pi.getImageId());
                        if (!keptExistingIds.contains(sid)) {
                            // if it wasn't explicitly kept, delete it
                            imageDAO.deleteImageById(pi.getImageId());
                        }
                    }

                    // 6.5: Handle primary image setting with proper order
                    // First reset all primary flags
                    imageDAO.resetAllPrimaryImages(productId);

                    // Track if primary has been set
                    boolean primarySet = false;

                    // 1. Check primaryImageUpdate first (highest priority - from explicit "Set as
                    // primary" action)
                    String primaryImageUpdate = request.getParameter("primaryImageUpdate");
                    if (primaryImageUpdate != null && !primaryImageUpdate.isEmpty()) {
                        if (primaryImageUpdate.startsWith("new:")) {
                            // Format: new:index:1
                            String[] parts = primaryImageUpdate.split(":");
                            if (parts.length == 3) {
                                try {
                                    int newPrimaryIndex = Integer.parseInt(parts[1]);
                                    if (newPrimaryIndex >= 0 && newPrimaryIndex < newUploadedUrls.size()) {
                                        String newPrimaryUrl = newUploadedUrls.get(newPrimaryIndex);
                                        imageDAO.updatePrimaryImage(productId, newPrimaryUrl);
                                        product.setPrimaryImageUrl(newPrimaryUrl);
                                        primarySet = true;
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            // Format: imageId:1
                            String[] parts = primaryImageUpdate.split(":");
                            if (parts.length == 2) {
                                try {
                                    long imageId = Long.parseLong(parts[0]);
                                    // Get URL from the existing image map
                                    String url = existingIdToUrl.get(String.valueOf(imageId));
                                    if (url != null) {
                                        imageDAO.updatePrimaryImage(productId, url);
                                        product.setPrimaryImageUrl(url);
                                        primarySet = true;
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    // 2. If no primary set yet, check newPrimaryIndex for newly uploaded images
                    if (!primarySet) {
                        String newPrimaryIndexStr = request.getParameter("newPrimaryIndex");
                        if (newPrimaryIndexStr != null && !newPrimaryIndexStr.isEmpty()) {
                            try {
                                int newIdx = Integer.parseInt(newPrimaryIndexStr);
                                if (newIdx >= 0 && newIdx < insertedNewUrls.size()) {
                                    String newUrl = insertedNewUrls.get(newIdx);
                                    imageDAO.updatePrimaryImage(productId, newUrl);
                                    product.setPrimaryImageUrl(newUrl);
                                    primarySet = true;
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // 3. If still no primary, check legacy PrimaryImage parameter
                    if (!primarySet) {
                        String primaryExisting = request.getParameter("PrimaryImage");
                        if (primaryExisting != null && !primaryExisting.isEmpty()) {
                            try {
                                long pid = Long.parseLong(primaryExisting);
                                String url = existingIdToUrl.get(String.valueOf(pid));
                                if (url != null) {
                                    imageDAO.updatePrimaryImage(productId, url);
                                    product.setPrimaryImageUrl(url);
                                    primarySet = true;
                                }
                            } catch (NumberFormatException nfe) {
                                // If not a number, treat as URL
                                imageDAO.updatePrimaryImage(productId, primaryExisting);
                                product.setPrimaryImageUrl(primaryExisting);
                                primarySet = true;
                            }
                        }
                    }

                    // 4. Final fallback: if no primary set and there are images, use first
                    // available
                    if (!primarySet) {
                        // First check kept existing images
                        if (!keptExistingIds.isEmpty()) {
                            String firstExistingId = keptExistingIds.iterator().next();
                            String url = existingIdToUrl.get(firstExistingId);
                            if (url != null) {
                                imageDAO.updatePrimaryImage(productId, url);
                                product.setPrimaryImageUrl(url);
                                primarySet = true;
                            }
                        }
                        // If still no primary, check new uploads
                        if (!primarySet && !insertedNewUrls.isEmpty()) {
                            String firstNewUrl = insertedNewUrls.get(0);
                            imageDAO.updatePrimaryImage(productId, firstNewUrl);
                            product.setPrimaryImageUrl(firstNewUrl);
                        }
                    }
                    // --- 7️⃣ Cập nhật thông tin sản phẩm
                    boolean updated = productDAO.updateProduct(product);

                    if (updated) {
                        response.sendRedirect(
                                request.getContextPath() + "/shop/product?action=view&message=update_success");
                    } else {
                        request.setAttribute("errorMessage", "Không thể cập nhật sản phẩm.");
                        request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request,
                                response);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request, response);
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action: " + action);
                break;
        }
    }
}