package com.group01.aurora_demo.auth.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = { "/auth/logout" })
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/home");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}