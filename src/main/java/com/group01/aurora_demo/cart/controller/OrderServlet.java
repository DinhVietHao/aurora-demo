package com.group01.aurora_demo.cart.controller;

<<<<<<< HEAD
public class OrderServlet {

=======
import java.io.IOException;

import com.group01.aurora_demo.auth.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/order/*")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("AUTH_USER");
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/customer/profile/profile.jsp").forward(request, response);
    }
>>>>>>> 6a13786814f123593cf52f52fe60d13c593aa470
}
