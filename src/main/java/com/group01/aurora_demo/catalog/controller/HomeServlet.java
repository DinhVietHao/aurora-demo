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

        List<Product> suggestedProducts;
        if (user != null) {
            suggestedProducts = productDAO.getSuggestedProductsForCustomer(user.getId());
        } else {
            suggestedProducts = productDAO.getSuggestedProductsForGuest();
        }

        request.setAttribute("suggestedProducts", suggestedProducts);
        request.getRequestDispatcher("/WEB-INF/views/home/home.jsp").forward(request, response);
    }

}