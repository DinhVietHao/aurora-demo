package com.group01.aurora_demo.customer.controller;

import java.io.IOException;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.customer.service.AddressService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/account/*")
public class AccountController extends HttpServlet {
    private final AddressService addressService;

    public AccountController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();
        String view = "";

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        switch (path) {
            case "/address":
                req.setAttribute("addresses", addressService.getAddressesByUserId(user.getId()));
                view = "/WEB-INF/views/customer/address/address.jsp";
                break;

            case "/order":
                view = "/WEB-INF/views/customer/order/order.jsp";
                break;

            case "profile":
            default:
                view = "/WEB-INF/views/customer/profile/profile.jsp";
                break;
        }

        req.getRequestDispatcher(view).forward(req, resp);
    }
}