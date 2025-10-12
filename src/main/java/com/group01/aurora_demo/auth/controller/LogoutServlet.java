package com.group01.aurora_demo.auth.controller;

import com.group01.aurora_demo.auth.service.RememberMeService;
import com.group01.aurora_demo.auth.dao.RememberMeTokenDAO;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = { "/auth/logout" })
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            User u = (User) req.getSession().getAttribute("AUTH_USER");
            if (u != null) {
                RememberMeService svc = new RememberMeService(new RememberMeTokenDAO(), null, req::isSecure);
                svc.clearAllForUser(u.getId(), resp);
            }
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/home");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}