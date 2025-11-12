package com.group01.aurora_demo.catalog.controller;

import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.model.Category;
import com.group01.aurora_demo.catalog.dao.ReviewDAO;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.catalog.model.Review;
import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import com.group01.aurora_demo.shop.model.Shop;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/home")
public class HomeServlet extends NotificationServlet {

    private ProductDAO productDAO = new ProductDAO();
    private UserDAO userDAO = new UserDAO();
    private ShopDAO shopDAO = new ShopDAO();
    private static final int LIMIT = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        setFilterAttributes(request);
        autoLoginFromCookie(request);
        String action = request.getParameter("action") != null ? request.getParameter("action") : "home";
        switch (action) {
            case "home":
                handleHome(request, response);
                break;

            case "bookstore":
                handleBookstore(request, response);
                break;

            case "detail":
                handleProductDetail(request, response);
                break;

            case "view-shop":
                handleViewShop(request, response);
                break;

            case "search":
                handleSearch(request, response);
                break;

            case "filter":
                handleFilter(request, response);
                break;
        }
    }

    private void autoLoginFromCookie(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            String savedEmail = getCookieValue(request, "remember_account");
            if (savedEmail != null && !savedEmail.isEmpty()) {
                try {
                    UserDAO userDAO = new UserDAO();
                    user = userDAO.findByEmailAndProvider(savedEmail, "LOCAL");
                    if (user != null) {
                        session.setAttribute("AUTH_USER", user);
                        session.setMaxInactiveInterval(60 * 60 * 2);

                        CartItemDAO cartItemDAO = new CartItemDAO();
                        int cartCount = cartItemDAO.getDistinctItemCount(user.getUserID());
                        session.setAttribute("cartCount", cartCount);
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR] Auto-login failed: " + e.getMessage());
                    session.setAttribute("cartCount", 0);
                }
            }
        }
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (name.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
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

    private void handleHome(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("AUTH_USER");

            List<Product> suggestedProducts;
            if (user != null) {
                suggestedProducts = productDAO.getSuggestedProductsForCustomer(user.getUserID());
                if (suggestedProducts == null || suggestedProducts.isEmpty()) {
                    suggestedProducts = productDAO.getSuggestedProductsForGuest();
                }
            } else {
                suggestedProducts = productDAO.getSuggestedProductsForGuest();
            }

            List<Product> latestProducts = productDAO.getLatestProducts(36);

            Map<String, Object> flashSaleData = productDAO.getFlashSaleProducts();

            request.setAttribute("suggestedProducts", suggestedProducts != null ? suggestedProducts : List.of());
            request.setAttribute("latestProducts", latestProducts != null ? latestProducts : List.of());

            if (flashSaleData != null) {
                request.setAttribute("flashSaleProducts", flashSaleData.get("products"));
                request.setAttribute("flashSaleEndAt", flashSaleData.get("flashSaleEndAt"));
                request.setAttribute("currentServerTime", flashSaleData.get("currentServerTime"));
            } else {
                request.setAttribute("flashSaleProducts", List.of());
            }

            request.getRequestDispatcher("/WEB-INF/views/home/home.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("Error in \"handleHome\" function: " + e.getMessage());
        }
    }

    private void handleBookstore(HttpServletRequest request, HttpServletResponse response) {
        try {
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
        } catch (Exception e) {
            System.out.println("Error in \"handleProductDetail\" function: " + e.getMessage());
        }
    }

    private void handleProductDetail(HttpServletRequest request, HttpServletResponse response) {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            Product product = productDAO.getProductById(id);

            if (product != null) {
                // Load shop info
                Long shopId = shopDAO.getShopIdByProductId(id);
                if (shopId != null) {
                    Shop shop = shopDAO.getShopByIdWithStats(shopId);
                    request.setAttribute("shop", shop);
                }

                // Load Flash Sale info
                Map<String, Object> flashSaleInfo = productDAO.getFlashSaleInfoForProduct(id);
                request.setAttribute("flashSaleInfo", flashSaleInfo);

                // Load reviews
                int reviewsPerPage = 10;
                ReviewDAO reviewDAO = new ReviewDAO();

                List<Review> reviews = reviewDAO.getReviewsByProductIdWithFilter(id, 0, reviewsPerPage, null, null,
                        null);

                int totalReviews = reviewDAO.countReviewsByProductIdWithFilter(id, null, null, null);

                int totalReviewPages = (int) Math.ceil((double) totalReviews / reviewsPerPage);

                request.setAttribute("reviews", reviews);
                request.setAttribute("currentPage", 1);
                request.setAttribute("totalReviews", totalReviews);
                request.setAttribute("totalPages", totalReviewPages);
                request.setAttribute("selectedRating", "all");
                request.setAttribute("selectedFilter", "");

                // Load related products
                List<Long> categoryIds = new ArrayList<>();
                if (product.getCategories() != null && !product.getCategories().isEmpty()) {
                    for (Category cat : product.getCategories()) {
                        categoryIds.add(cat.getCategoryId());
                    }
                }

                List<Product> relatedProducts = productDAO.getRelatedProducts(id, categoryIds, 12);
                request.setAttribute("suggestions", relatedProducts);
            }

            int reviewCount = productDAO.countReviewsByProductId(id);
            request.setAttribute("title", product.getTitle());
            request.setAttribute("product", product);
            request.setAttribute("reviewCount", reviewCount);
            request.getRequestDispatcher("/WEB-INF/views/catalog/books/book_detail.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("Error in \"handleProductDetail\" function: " + e.getMessage());
        }
    }

    private void handleViewShop(HttpServletRequest request, HttpServletResponse response) {
        try {
            long shopId = Long.parseLong(request.getParameter("shopId"));
            Shop shop = shopDAO.getShopByIdWithStats(shopId);

            // Load all products of shop (12 latest products)
            int totalProducts = productDAO.countProductsByShopId(shopId);
            List<Product> allProducts = productDAO.getProductsByShopId(shopId, 0, totalProducts);

            // Load bestseller products (12 top-selling products)
            List<Product> bestsellerProducts = productDAO.getBestsellerByShopId(shopId, 12);

            request.setAttribute("shop", shop);
            request.setAttribute("allProducts", allProducts);
            request.setAttribute("bestsellerProducts", bestsellerProducts);
            request.setAttribute("title", shop.getName());
            request.getRequestDispatcher("/WEB-INF/views/catalog/shop/viewShop.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error in \"handleViewShop\" of HomeServlet: " + e.getMessage());
        }
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

}