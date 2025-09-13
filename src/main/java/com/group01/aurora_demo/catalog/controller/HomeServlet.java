package com.group01.aurora_demo.catalog.controller;

import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.model.Product;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * HomeServlet handles requests to the home page.
 * It loads product data and forwards it to the JSP view.
 */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    // DAO instance used to interact with the Products table
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Retrieve all products from the database
        List<Product> products = productDAO.getAllProducts();

        // Add products list to the request scope for JSP access
        req.setAttribute("products", products);

        // Forward request to home.jsp view
        req.getRequestDispatcher("/WEB-INF/views/home/home.jsp").forward(req, resp);

        // Temporary: Forward to index.jsp for testing purposes
        // req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

}