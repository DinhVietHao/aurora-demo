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

    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // List<Product> suggestedProducts = productDAO.getSuggestedProducts();

        // req.setAttribute("suggestedProducts", suggestedProducts);

        req.getRequestDispatcher("/WEB-INF/views/home/home.jsp").forward(req, resp);

    }

}