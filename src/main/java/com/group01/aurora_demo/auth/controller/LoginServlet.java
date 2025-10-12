package com.group01.aurora_demo.auth.controller;

import com.group01.aurora_demo.auth.service.GoogleLogin;
import com.group01.aurora_demo.auth.model.GoogleAccount;
import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dao.CartDAO;
import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.common.util.Json;
import com.group01.aurora_demo.auth.model.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;

import com.group01.aurora_demo.common.filter.AuthRequiredFilter;
import com.group01.aurora_demo.auth.service.RememberMeService;
import com.group01.aurora_demo.auth.dao.RememberMeTokenDAO;
import com.group01.aurora_demo.cart.model.Cart;

import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "LoginServlet", urlPatterns = { "/auth/login" })
public class LoginServlet extends HttpServlet {

    private static final String PROVIDER_LOCAL = "LOCAL";
    private static final String PROVIDER_GOOGLE = "GOOGLE";

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private void redirectHome(HttpServletRequest req, HttpServletResponse resp) {
        try {
            resp.sendRedirect(req.getContextPath() + "/home");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void loginAndRedirect(User user, HttpServletRequest req, HttpServletResponse resp,
            String provider, boolean issueRemember) {
        HttpSession session = req.getSession(true);
        session.setAttribute("AUTH_USER", user);
        session.setMaxInactiveInterval(60 * 60 * 2);

        try {
            CartItemDAO cartItemDAO = new CartItemDAO();
            CartDAO cartDAO = new CartDAO();
            Cart cart = cartDAO.getCartByUserId(user.getId());
            int cartCount = cartItemDAO.getDistinctItemCount(cart.getCartId());
            session.setAttribute("cartCount", cartCount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            session.setAttribute("cartCount", 0);

        }

        if (PROVIDER_LOCAL.equals(provider) && issueRemember) {
            try {
                RememberMeService svc = new RememberMeService(new RememberMeTokenDAO(), new UserDAO(), req::isSecure);
                svc.issueToken(user, resp);
            } catch (Exception ignore) {
                System.out.println(ignore.getMessage());
            }
        }

        String redirect = (String) session.getAttribute(AuthRequiredFilter.LOGIN_REDIRECT);
        if (redirect != null) {
            session.removeAttribute(AuthRequiredFilter.LOGIN_REDIRECT);

            if (redirect.startsWith("http://") || redirect.startsWith("https://")) {
                try {
                    java.net.URI u = java.net.URI.create(redirect);
                    String path = (u.getRawPath() != null) ? u.getRawPath() : "";
                    String q = (u.getRawQuery() != null) ? ("?" + u.getRawQuery()) : "";
                    redirect = path + q;
                } catch (IllegalArgumentException ignore) {
                    redirect = null;
                }
            }
        }
        if (redirect == null || redirect.isBlank()) {
            redirect = req.getContextPath() + "/home";
        }

        if (PROVIDER_LOCAL.equals(provider)) {
            Json.sendOk(resp, java.util.Map.of("ok", true, "redirect", redirect));
        } else {
            redirectHome(req, resp);
        }
    }

    private void handleGoogleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        try {
            String accessToken = GoogleLogin.getToken(code);
            GoogleAccount ga = GoogleLogin.getUserInfo(accessToken);
            String email = normalizeEmail(ga.getEmail());
            User user = userDAO.findByEmailAndProvider(email, PROVIDER_GOOGLE);

            if (user == null) {
                user = new User();
                user.setEmail(email);
                String name = (ga.getName() != null && !ga.getName().isBlank())
                        ? ga.getName().trim()
                        : ((ga.getGivenName() != null ? ga.getGivenName() : "") + " "
                                + (ga.getFamilyName() != null ? ga.getFamilyName() : "")).trim();
                user.setFullName(name.isBlank() ? email : name);
                String randomPw = UUID.randomUUID().toString();
                String hash = BCrypt.hashpw(randomPw, BCrypt.gensalt(10));
                user.setPasswordHash(hash);
                user.setAuthProvider(PROVIDER_GOOGLE);

                boolean created = userDAO.createAccount(user);
                if (!created) {
                    redirectHome(request, response);
                    return;
                }
            }

            loginAndRedirect(user, request, response, PROVIDER_GOOGLE, true);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            redirectHome(request, response);
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private void handleLocalLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            var body = request.getReader().lines().collect(java.util.stream.Collectors.joining());
            var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);

            String email = trim(json.path("email").asText(null));
            String password = trim(json.path("password").asText(null));
            boolean remember = json.path("rememberMe").asBoolean(false);

            User user = userDAO.findByEmailAndProvider(email, PROVIDER_LOCAL);
            if (user == null 
            // || !BCrypt.checkpw(password, user.getPasswordHash())
            ) {
                Json.sendOk(response, java.util.Map.of(
                        "ok", false,
                        "message", "Email hoặc mật khẩu không đúng."));
                return;
            }

            loginAndRedirect(user, request, response, PROVIDER_LOCAL, remember);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            Json.sendOk(response, java.util.Map.of(
                    "ok", false,
                    "message", "Không thể đăng nhập lúc này. Vui lòng thử lại."));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        if (request.getParameter("code") != null) {
            try {
                handleGoogleLogin(request, response);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            redirectHome(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        handleLocalLogin(request, response);
    }
}