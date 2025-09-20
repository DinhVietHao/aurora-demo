package com.group01.aurora_demo.catalog.controller;

import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.model.Product;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/book")
public class BookDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idRaw = req.getParameter("id");
        ProductDAO productDAO = new ProductDAO();
        try {
            long id = Long.parseLong(idRaw);
            Product product = productDAO.getProductById(id);
            int percentDiscount = (int) Math.round((1 - (double) product.getSalePrice() / product.getOriginalPrice()) * 100);
            req.setAttribute("product", product);
            req.setAttribute("percentDiscount", percentDiscount);
            req.getRequestDispatcher("/WEB-INF/views/catalog/books/detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}