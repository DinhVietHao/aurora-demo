package com.group01.aurora_demo.catalog.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.group01.aurora_demo.shop.dao.ProductDAO;
import com.group01.aurora_demo.shop.model.Product;

/**
 * ListBooksServlet handles requests to display paginated book listings.
 * It fetches products from the database and forwards them to the JSP view.
 */
@WebServlet("/books") // URL: /aurora/books
public class ListBooksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = 1;
        int limit = 20;
        String pageParam = request.getParameter("page");

        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        ProductDAO productDAO = new ProductDAO();
        int offset = (page - 1) * limit;
        // Fetch products for the current page
        List<Product> products = productDAO.getProductsByPage(offset, limit);
        
        int totalProducts = productDAO.countProducts();
        int totalPages = (int) Math.ceil((double) totalProducts / limit);

        // Set attributes for JSP to render
        request.setAttribute("products", products);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);

        // Forward request to the JSP view
        request.getRequestDispatcher("/WEB-INF/views/catalog/books/list.jsp").forward(request, response);
    }
}