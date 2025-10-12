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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        String action = request.getParameter("action") != null ? request.getParameter("action") : "home";

        switch (action) {
            case "home":
                List<Product> suggestedProducts;
                if (user != null) {
                    suggestedProducts = productDAO.getSuggestedProductsForCustomer(user.getId());
                    if (suggestedProducts.isEmpty())
                        suggestedProducts = productDAO.getSuggestedProductsForGuest();
                } else {
                    suggestedProducts = productDAO.getSuggestedProductsForGuest();
                }
                request.setAttribute("suggestedProducts", suggestedProducts);
                request.getRequestDispatcher("/WEB-INF/views/home/home.jsp").forward(request, response);
                break;
            case "bookstore":
                request.setCharacterEncoding("UTF-8");

                // Detect nếu có sản phẩm bán được
                int soldProducts = productDAO.countProductsWithSold();
                String defaultSort = soldProducts > 0 ? "best" : "newest";
                String sort = request.getParameter("sort");
                if (sort == null || sort.isEmpty())
                    sort = defaultSort;

                int limit = 12;
                int page = 1;
                String pageParam = request.getParameter("page");
                if (pageParam != null) {
                    try {
                        page = Integer.parseInt(pageParam);
                    } catch (Exception e) {
                        page = 1;
                    }
                }
                int offset = (page - 1) * limit;

                List<Product> products = productDAO.getAllProducts(offset, limit, sort);
                int totalProducts = productDAO.countAllProducts();
                int totalPages = (int) Math.ceil((double) totalProducts / limit);

                request.setAttribute("products", products);
                request.setAttribute("page", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("title", "Nhà sách");

                request.getRequestDispatcher("/WEB-INF/views/catalog/books/bookstore.jsp").forward(request, response);
                break;
            case "detail":
                String idRaw = request.getParameter("id");
                ProductDAO productDAO = new ProductDAO();
                try {
                    long id = Long.parseLong(idRaw);
                    Product product = productDAO.getProductById(id);
                    request.setAttribute("title", product.getTitle());
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("/WEB-INF/views/catalog/books/book_detail.jsp").forward(request,
                            response);
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                }
                break;
            default:
                break;
        }
    }

}