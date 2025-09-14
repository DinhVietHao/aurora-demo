package com.group01.aurora_demo.catalog.controller;

import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.model.Product;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * ListBooksServlet handles requests to display paginated book listings.
 * It fetches products from the database and forwards them to the JSP view.
 */
@WebServlet("/books") // URL: /aurora/books
public class ListBooksServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int page = 1; // default page number
        int pageSize = 20; // number of products per page

        // Read "page" parameter from request (if provided)
        String pageParam = req.getParameter("page");
        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }

        // Count total number of products for pagination
        int totalProducts = productDAO.countProducts();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        // Fetch products for the current page
        List<Product> products = productDAO.getProductsByPage(page, pageSize);

        // Set attributes for JSP to render
        req.setAttribute("products", products);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);

        // Forward request to the JSP view
        req.getRequestDispatcher("/WEB-INF/views/catalog/books/list.jsp").forward(req, resp);
    }
}