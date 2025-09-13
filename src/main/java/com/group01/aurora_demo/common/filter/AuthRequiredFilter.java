package com.group01.aurora_demo.common.filter;

import jakarta.servlet.http.*;
import java.io.IOException;
import jakarta.servlet.*;

public class AuthRequiredFilter implements Filter {
    public static final String LOGIN_REDIRECT = "LOGIN_REDIRECT";

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {
        try {
            HttpServletRequest r = (HttpServletRequest) req;
            HttpServletResponse w = (HttpServletResponse) resp;

            HttpSession s = r.getSession(false);
            boolean loggedIn = (s != null && s.getAttribute("AUTH_USER") != null);

            if (!loggedIn) {
                String qs = r.getQueryString();
                String target = r.getRequestURI() + ((qs != null && !qs.isBlank()) ? ("?" + qs) : "");
                r.getSession(true).setAttribute(LOGIN_REDIRECT, target);

                w.sendRedirect(r.getContextPath() + "/home?login=1");
                return;
            }

            chain.doFilter(req, resp);
        } catch (IOException | ServletException ex) {
            System.out.println(ex.getMessage());
        }
    }
}