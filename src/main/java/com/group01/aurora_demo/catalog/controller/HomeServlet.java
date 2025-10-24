package com.group01.aurora_demo.catalog.controller;

import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.dao.ReviewDAO;
import com.group01.aurora_demo.catalog.model.Category;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.catalog.model.Review;
import com.group01.aurora_demo.catalog.model.ReviewImage;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import com.group01.aurora_demo.shop.model.Shop;
import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.CartItemDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private UserDAO userDAO = new UserDAO();
    private ShopDAO shopDAO = new ShopDAO();
    private static final int LIMIT = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setFilterAttributes(request);
        request.setCharacterEncoding("UTF-8");

        String savedEmail = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("remember_account".equals(c.getName())) {
                    savedEmail = c.getValue();
                    break;
                }
            }
        }

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null && savedEmail != null && !savedEmail.isEmpty()) {
            user = userDAO.findByEmailAndProvider(savedEmail, "LOCAL");
            if (user != null) {
                session.setAttribute("AUTH_USER", user);
                session.setMaxInactiveInterval(60 * 60 * 2);

                try {
                    CartItemDAO cartItemDAO = new CartItemDAO();
                    int cartCount = cartItemDAO.getDistinctItemCount(user.getId());
                    session.setAttribute("cartCount", cartCount);
                } catch (Exception e) {
                    session.setAttribute("cartCount", 0);
                }
            }
        }

        String action = request.getParameter("action") != null ? request.getParameter("action") : "home";

        switch (action) {
            case "home":
                List<Product> suggestedProducts = (user != null)
                        ? productDAO.getSuggestedProductsForCustomer(user.getId())
                        : productDAO.getSuggestedProductsForGuest();
                if (suggestedProducts.isEmpty())
                    suggestedProducts = productDAO.getSuggestedProductsForGuest();

                List<Product> latestProducts = productDAO.getLatestProducts(36);

                request.setAttribute("suggestedProducts", suggestedProducts);
                request.setAttribute("latestProducts", latestProducts);
                request.getRequestDispatcher("/WEB-INF/views/home/home.jsp").forward(request, response);
                break;

            case "bookstore":
                request.setCharacterEncoding("UTF-8");

                int soldProducts = productDAO.countProductsWithSold();
                String defaultSort = soldProducts > 0 ? "best" : "newest";
                String sort = request.getParameter("sort");
                if (sort == null || sort.isEmpty())
                    sort = defaultSort;

                int page = getPage(request);
                int offset = (page - 1) * LIMIT;

                List<Product> products = productDAO.getAllProducts(offset, LIMIT, sort);
                int totalProducts = productDAO.countAllProducts();
                int totalPages = (int) Math.ceil((double) totalProducts / LIMIT);

                if (products.isEmpty()) {
                    request.setAttribute("noProductsMessage", "Chưa có sản phẩm nào trong cửa hàng.");
                    request.setAttribute("showSort", false);
                } else {
                    request.setAttribute("products", products);
                    request.setAttribute("page", page);
                    request.setAttribute("totalPages", totalPages);
                    request.setAttribute("showSort", true);
                }
                request.setAttribute("title", "Nhà sách");
                request.getRequestDispatcher("/WEB-INF/views/catalog/books/bookstore.jsp").forward(request, response);
                break;

            case "detail":
                try {
                    long id = Long.parseLong(request.getParameter("id"));
                    Product product = productDAO.getProductById(id);
                    if (product != null) {
                        // Load shop info
                        Long shopId = shopDAO.getShopIdByProductId(id);
                        if (shopId != null) {
                            Shop shop = shopDAO.getShopByIdWithStats(shopId);
                            System.out.println(shop.getAvatarUrl());
                            request.setAttribute("shop", shop);
                        }

                        // Load reviews với pagination
                        int reviewPage = 1;
                        String reviewPageParam = request.getParameter("reviewPage");
                        if (reviewPageParam != null) {
                            try {
                                reviewPage = Integer.parseInt(reviewPageParam);
                            } catch (NumberFormatException e) {
                                reviewPage = 1;
                            }
                        }

                        int reviewsPerPage = 10;
                        int reviewOffset = (reviewPage - 1) * reviewsPerPage;

                        ReviewDAO reviewDAO = new ReviewDAO();
                        List<Review> reviews = reviewDAO.getReviewsByProductId(id, reviewOffset, reviewsPerPage);
                        if (reviews.isEmpty()) {
                            reviews = createMockReviews();
                            request.setAttribute("reviews", reviews);
                            request.setAttribute("currentPage", 1);
                            request.setAttribute("totalPages", 1);
                        } else {
                            int totalReviews = reviewDAO.countReviewsByProductId(id);
                            int totalReviewPages = (int) Math.ceil((double) totalReviews / reviewsPerPage);

                            request.setAttribute("reviews", reviews);
                            request.setAttribute("currentPage", reviewPage);
                            request.setAttribute("totalReviews", totalReviews);
                            request.setAttribute("totalPages", totalReviewPages);
                        }

                        List<Long> categoryIds = new ArrayList<>();
                        if (product.getCategories() != null && !product.getCategories().isEmpty()) {
                            for (Category cat : product.getCategories()) {
                                categoryIds.add(cat.getCategoryId());
                            }
                        }

                        List<Product> relatedProducts = productDAO.getRelatedProducts(id, categoryIds, 36);
                        request.setAttribute("suggestions", relatedProducts);
                    }

                    int reviewCount = productDAO.countReviewsByProductId(id);
                    request.setAttribute("title", product.getTitle());
                    request.setAttribute("product", product);
                    request.setAttribute("reviewCount", reviewCount);
                    request.getRequestDispatcher("/WEB-INF/views/catalog/books/book_detail.jsp").forward(request,
                            response);
                } catch (NumberFormatException e) {
                    System.out.println("Error in \"detail\" of HomeServlet: " + e.getMessage());
                }
                break;

            case "search":
                handleSearch(request, response);
                break;

            case "filter":
                handleFilter(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void setFilterAttributes(HttpServletRequest request) {
        request.setAttribute("categories", productDAO.getCategories());
        request.setAttribute("authors", productDAO.getAuthors());
        request.setAttribute("publishers", productDAO.getPublishers());
        request.setAttribute("languages", productDAO.getLanguages());
    }

    private int getPage(HttpServletRequest request) {
        String pageParam = request.getParameter("page");
        int page = 1;
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                page = 1;
            }
        }
        return page;
    }

    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        if (keyword == null)
            keyword = "";

        int page = getPage(request);
        int offset = (page - 1) * LIMIT;

        List<Product> products = productDAO.getAllProductsByKeyword(keyword, offset, LIMIT);
        int totalProducts = productDAO.countSearchResultsByKeyword(keyword);
        int totalPages = (int) Math.ceil((double) totalProducts / LIMIT);

        if (products.isEmpty()) {
            request.setAttribute("noProductsMessage",
                    "Không tìm thấy sản phẩm nào phù hợp với từ khóa \"" + keyword + "\".");
        } else {
            request.setAttribute("products", products);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
        }
        request.setAttribute("title", "Kết quả tìm kiếm cho: \"" + keyword + "\"");
        request.setAttribute("keyword", keyword);
        request.setAttribute("showSort", false);
        request.getRequestDispatcher("/WEB-INF/views/catalog/books/bookstore.jsp").forward(request, response);
    }

    private void handleFilter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String category = request.getParameter("category");
        String author = request.getParameter("author");
        String publisher = request.getParameter("publisher");
        String language = request.getParameter("language");

        Double minPrice = null, maxPrice = null;
        try {
            if (request.getParameter("minPrice") != null)
                minPrice = Double.parseDouble(request.getParameter("minPrice"));
            if (request.getParameter("maxPrice") != null)
                maxPrice = Double.parseDouble(request.getParameter("maxPrice"));
        } catch (NumberFormatException e) {
        }

        int page = getPage(request);
        int offset = (page - 1) * LIMIT;

        List<Product> products = productDAO.getAllProductsByFilter(offset, LIMIT, category, author, publisher, language,
                minPrice, maxPrice);
        int totalProducts = productDAO.countProductsByFilter(category, author, publisher, language, minPrice, maxPrice);
        int totalPages = (int) Math.ceil((double) totalProducts / LIMIT);

        if (products.isEmpty()) {
            request.setAttribute("noProductsMessage", "Không có sản phẩm nào phù hợp với bộ lọc đã chọn.");
        } else {
            request.setAttribute("products", products);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
        }
        request.setAttribute("title", "Nhà sách");
        request.setAttribute("showSort", false);
        request.getRequestDispatcher("/WEB-INF/views/catalog/books/bookstore.jsp").forward(request, response);
    }

    private List<Review> createMockReviews() {
        List<Review> mockReviews = new ArrayList<>();

        // Review 1
        Review r1 = new Review();
        r1.setReviewId(1L);
        r1.setRating(5);
        r1.setComment("Sách rất hay, nội dung bổ ích và hấp dẫn. Giao hàng nhanh, đóng gói cẩn thận!");
        r1.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusDays(5)));

        User u1 = new User();
        u1.setFullName("Nguyễn Văn A");
        u1.setAvatarUrl(null);
        r1.setUser(u1);

        List<ReviewImage> imgs1 = new ArrayList<>();
        ReviewImage img1 = new ReviewImage();
        img1.setUrl("review-example-1.jpg");
        imgs1.add(img1);

        ReviewImage img2 = new ReviewImage();
        img2.setUrl("review-example-2.jpg");
        imgs1.add(img2);

        ReviewImage img3 = new ReviewImage();
        img3.setUrl("review-example-3.jpg");
        imgs1.add(img3);
        r1.setImages(imgs1);

        mockReviews.add(r1);

        // Review 2
        Review r2 = new Review();
        r2.setReviewId(2L);
        r2.setRating(4);
        r2.setComment("Chất lượng sách tốt, giá hợp lý. Nhưng hơi lâu mới nhận được hàng.");
        r2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusDays(3)));

        User u2 = new User();
        u2.setFullName("Trần Thị B");
        u2.setAvatarUrl(null);
        r2.setUser(u2);
        r2.setImages(new ArrayList<>());

        mockReviews.add(r2);

        // Review 3
        Review r3 = new Review();
        r3.setReviewId(3L);
        r3.setRating(5);
        r3.setComment("Tuyệt vời! Đây là cuốn sách hay nhất tôi từng đọc. Cảm ơn shop!");
        r3.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusDays(2)));

        User u3 = new User();
        u3.setFullName("Lê Văn C");
        u3.setAvatarUrl(null);
        r3.setUser(u3);
        r3.setImages(new ArrayList<>());

        mockReviews.add(r3);

        return mockReviews;
    }
}
