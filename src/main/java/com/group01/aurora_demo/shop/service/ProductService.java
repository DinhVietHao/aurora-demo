package com.group01.aurora_demo.shop.service;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.shop.dao.ProductDAO;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import com.group01.aurora_demo.shop.model.Author;
import com.group01.aurora_demo.shop.model.BookDetail;
import com.group01.aurora_demo.shop.model.Category;
import com.group01.aurora_demo.shop.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class ProductService {

    private ShopDAO shopDAO = new ShopDAO();
    private ProductDAO productDAO = new ProductDAO();

    public void createProduct(HttpServletRequest request, User user) throws Exception {

        long shopId = shopDAO.getShopIdByUserId(user.getId());

        // ===== Lấy dữ liệu từ form =====
        String title = request.getParameter("Title");
        String description = request.getParameter("Description");
        double originalPrice = Double.parseDouble(request.getParameter("OriginalPrice"));
        double salePrice = Double.parseDouble(request.getParameter("SalePrice"));
        int stock = Integer.parseInt(request.getParameter("Stock"));
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

        String publisherId = request.getParameter("PublisherID");
        String publishedDateStr = request.getParameter("PublishedDate");
        Date publishedDate = (publishedDateStr != null && !publishedDateStr.isEmpty())
                ? Date.valueOf(publishedDateStr)
                : null;

        if (salePrice > originalPrice) {
            throw new ServletException("Giá bán phải nhỏ hơn hoặc bằng giá gốc.");
        }

        // ===== Xử lý upload ảnh =====
        List<String> imagePaths = handleImageUpload(request);

        // ===== Tạo Product =====
        Product product = new Product();
        product.setShopId(shopId);
        product.setTitle(title);
        product.setDescription(description);
        product.setOriginalPrice(originalPrice);
        product.setSalePrice(salePrice);
        product.setStock(stock);
        product.setWeight(weight);
        product.setPublisherId(publisherId != null && !publisherId.isEmpty() ? Long.parseLong(publisherId) : null);
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
                a.setName(name);
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

        // ===== Lưu vào DB =====
        productDAO.insertProduct(product);
    }

    // ===== Hàm xử lý upload ảnh =====
    private List<String> handleImageUpload(HttpServletRequest request) throws Exception {
        Collection<Part> parts = request.getParts();
        List<String> imageNames = new ArrayList<>();

        String uploadDir = request.getServletContext().getRealPath("/assets/images/catalog/products");
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists())
            uploadDirFile.mkdirs();

        for (Part part : parts) {
            if (part.getName().equals("ProductImages") && part.getSize() > 0) {
                if (part.getSize() > 5 * 1024 * 1024) {
                    throw new ServletException("Ảnh '" + part.getSubmittedFileName() + "' vượt 5MB.");
                }
                String fileName = System.currentTimeMillis() + "_" + part.getSubmittedFileName();
                String fullPath = uploadDir + File.separator + fileName;
                part.write(fullPath);
                imageNames.add(fileName);
            }
        }

        if (imageNames.size() < 2 || imageNames.size() > 20) {
            throw new ServletException("Cần tải lên từ 2 đến 20 ảnh sản phẩm.");
        }

        return imageNames;
    }

}
