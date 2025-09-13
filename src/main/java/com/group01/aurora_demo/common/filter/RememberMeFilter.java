package com.group01.aurora_demo.common.filter;

import com.group01.aurora_demo.auth.service.RememberMeService;
import com.group01.aurora_demo.auth.dao.RememberMeTokenDAO;
import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.*;

public class RememberMeFilter implements Filter {
    private UserDAO userDAO;

    @Override
    public void init(FilterConfig filterConfig) {
        this.userDAO = new UserDAO();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse w = (HttpServletResponse) resp;

        HttpSession s = r.getSession(false);
        boolean loggedIn = (s != null && s.getAttribute("AUTH_USER") != null);

        if (!loggedIn) {
            RememberMeService svc = new RememberMeService(
                    new RememberMeTokenDAO(),
                    this.userDAO,
                    r::isSecure);
            Optional<User> auto = svc.tryAutoLogin(r, w);
            auto.ifPresent(u -> r.getSession(true).setAttribute("AUTH_USER", u));
        }

        chain.doFilter(req, resp);
    }
}