package com.group01.aurora_demo.shop.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import com.group01.aurora_demo.catalog.dao.AuthorDAO;
import com.group01.aurora_demo.catalog.dao.CategoryDAO;
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
import jakarta.servlet.http.Part;

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
        if (action == null)
            action = "view";

        String message = request.getParameter("message");
        String error = request.getParameter("error");

        if ("delete_success".equals(message)) {
            request.setAttribute("successMessage", "Đã xóa sản phẩm thành công!");
        }
        if ("delete_failed".equals(error)) {
            request.setAttribute("errorMessage",
                    "Không thể xóa sản phẩm vì đang trong Flash Sale hoặc đang được giao hàng.");
        }
        if ("create_success".equals(message)) {
            request.setAttribute("successMessage",
                    "Đã thêm sản phẩm thành công.");
        }
        if ("load_failed".equals(error)) {
            request.setAttribute("errorMessage",
                    "Load xem chi tiết bị lỗi gián đoạn");
        }
        ShopDAO shopDAO = new ShopDAO();
        ProductDAO productDAO = new ProductDAO();
        try {
            switch (action) {
                case "view":
                    long shopId = shopDAO.getShopIdByUserId(user.getId());
                    int page = 1;
                    int limit = 15;
                    String pageParam = request.getParameter("page");

                    if (pageParam != null) {
                        try {
                            page = Integer.parseInt(pageParam);
                        } catch (NumberFormatException e) {
                            page = 1;
                        }
                    }

                    int offset = (page - 1) * limit;

                    List<Product> listProduct = productDAO.getProductsByShopId(shopId, offset, limit);
                    int totalProducts = productDAO.countProductsByShopId(shopId);
                    int totalPages = (int) Math.ceil((double) totalProducts / limit);

                    request.setAttribute("listProduct", listProduct);
                    request.setAttribute("page", page);
                    request.setAttribute("totalPages", totalPages);
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
                        String json = gson.toJson(product);

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
                                request.getContextPath() + "/shop/product?action=view&message=delete_success");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình xóa sản phẩm.");
                    request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request, response);
                }
                break;
            case "update":
                try {
                    long productId = Long.parseLong(request.getParameter("productId"));

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
                    Product product = productDAO.getProductById(productId);
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

                    // --- 6️⃣ Xử lý ảnh:

                    List<ProductImage> existingImages = imageDAO.getImagesByProductId(productId);
                    Set<String> existingUrls = existingImages.stream()
                            .map(ProductImage::getUrl)
                            .collect(Collectors.toSet());

                    Set<String> submittedUrls = new HashSet<>();

                    // 6.1: Ảnh cũ còn lại (JS thêm input hidden: name="existingImageUrls")
                    String[] existingFromForm = request.getParameterValues("existingImageUrls");
                    if (existingFromForm != null) {
                        submittedUrls.addAll(Arrays.asList(existingFromForm));
                    }

                    // 6.2: Upload ảnh mới
                    List<String> newUploadedUrls = imageDAO.handleImageUpload(request);
                    submittedUrls.addAll(newUploadedUrls);

                    // 6.3: Xóa ảnh bị remove (JS thêm input hidden: name="RemovedImages")
                    String removedImagesParam = request.getParameter("RemovedImages");
                    if (removedImagesParam != null && !removedImagesParam.isBlank()) {
                        String[] removedArray = removedImagesParam.split(",");
                        for (String url : removedArray) {
                            url = url.trim();
                            if (!url.isEmpty()) {
                                imageDAO.deleteImageByUrl(url); // xóa logic, không xóa file
                                existingUrls.remove(url);
                            }
                        }
                    }

                    // 6.4: Xóa những ảnh cũ không còn trong submittedUrls
                    Set<String> toDelete = new HashSet<>(existingUrls);
                    toDelete.removeAll(submittedUrls);
                    for (String url : toDelete) {
                        imageDAO.deleteImageByUrl(url);
                    }

                    // 6.5: Thêm ảnh mới vào DB
                    for (String newUrl : newUploadedUrls) {
                        imageDAO.insertImage(productId, newUrl, false);
                    }

                    // 6.6: Cập nhật ảnh chính
                    String primaryUrl = request.getParameter("PrimaryImage");
                    if (primaryUrl != null && !primaryUrl.isBlank()) {
                        imageDAO.updatePrimaryImage(productId, primaryUrl);
                        product.setPrimaryImageUrl(primaryUrl);
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
