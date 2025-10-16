package com.group01.aurora_demo.catalog.controller;

import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private static final int LIMIT = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setFilterAttributes(request);
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

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
                String idRaw = request.getParameter("id");
                try {
                    long id = Long.parseLong(idRaw);
                    Product product = productDAO.getProductById(id);
                    request.setAttribute("title", product.getTitle());
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("/WEB-INF/views/catalog/books/book_detail.jsp").forward(request,
                            response);
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
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
}
