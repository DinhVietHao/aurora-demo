package com.group01.aurora_demo.common.filter;

import com.group01.aurora_demo.shop.dao.ShopDAO;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import jakarta.servlet.*;
import java.util.*;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private ShopDAO shopDAO;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.shopDAO = new ShopDAO();
        System.out.println("[FILTER] AuthenticationFilter initialized - Silent mode");
    }

    // Paths that don't require authentication
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/home",
            "/auth",
            "/api/chat",
            "/email",
            "/api/address",
            "/api/reviews");

    // Paths that require ADMIN role only
    private static final Set<String> ADMIN_ONLY_PATHS = Set.of(
            "/admin");

    // Paths that require SELLER role (with shop ownership validation)
    private static final Set<String> SELLER_PATHS = Set.of(
            "/shop/product",
            "/shop/orders",
            "/shop/voucher",
            "/shop/flashSale");

    // Paths that require any authenticated user
    private static final Set<String> AUTH_REQUIRED_PATHS = Set.of(
            "/profile",
            "/address",
            "/checkout",
            "/order",
            "/review");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String path = getPathWithoutContext(req);

        // Allow static resources
        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Allow public paths
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Get authenticated user
        User user = (session != null) ? (User) session.getAttribute("AUTH_USER") : null;

        // Check ADMIN-only paths
        if (isAdminOnlyPath(path)) {
            if (user == null || !hasRole(user, "ADMIN")) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // Check SELLER paths (with shop ownership validation)
        if (isSellerPath(path)) {
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }

            // ADMIN has full access to all shops
            if (hasRole(user, "ADMIN")) {
                chain.doFilter(request, response);
                return;
            }

            // SELLER must own the shop
            if (!hasRole(user, "SELLER")) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }

            // Validate shop ownership for SELLER
            if (!validateShopOwnership(req, user)) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }

            chain.doFilter(request, response);
            return;
        }

        // Check /shop base path (register, check-status, dashboard)
        if (path.startsWith("/shop")) {
            handleShopAccess(req, resp, chain, user, path);
            return;
        }

        // Check auth-required paths
        if (requiresAuthentication(path)) {
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
        }

        // All checks passed
        chain.doFilter(request, response);
    }

    /**
     * Validate shop ownership for SELLER
     */
    private boolean validateShopOwnership(HttpServletRequest req, User user) {
        try {
            Long userShopId = shopDAO.getShopIdByUserId(user.getUserID());

            if (userShopId == null || userShopId <= 0) {
                return false;
            }

            Long requestShopId = extractShopIdFromRequest(req);

            if (requestShopId == null) {
                return true;
            }

            return userShopId.equals(requestShopId);

        } catch (Exception e) {
            System.err.println("[FILTER] Error validating shop ownership: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract shopId from various request sources
     */
    private Long extractShopIdFromRequest(HttpServletRequest req) {
        // 1. Direct shopId parameter
        String shopIdParam = req.getParameter("shopId");
        if (shopIdParam != null && !shopIdParam.isEmpty()) {
            try {
                return Long.parseLong(shopIdParam);
            } catch (NumberFormatException e) {
                // Continue
            }
        }

        // 2. Generic id parameter
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                return Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                // Continue
            }
        }

        // 3. Product ID → Shop ID
        String productIdParam = req.getParameter("productId");
        if (productIdParam != null && !productIdParam.isEmpty()) {
            try {
                long productId = Long.parseLong(productIdParam);
                return shopDAO.getShopIdByProductId(productId);
            } catch (Exception e) {
                // Continue
            }
        }

        // 4. Order Shop ID → Shop ID
        String orderShopIdParam = req.getParameter("orderShopId");
        if (orderShopIdParam != null && !orderShopIdParam.isEmpty()) {
            try {
                long orderShopId = Long.parseLong(orderShopIdParam);
                return shopDAO.getShopIdByOrderShopId(orderShopId);
            } catch (Exception e) {
                // Continue
            }
        }

        // 5. Voucher ID → Shop ID
        String voucherIdParam = req.getParameter("voucherID");
        if (voucherIdParam == null) {
            voucherIdParam = req.getParameter("voucherId");
        }
        if (voucherIdParam != null && !voucherIdParam.isEmpty()) {
            try {
                long voucherId = Long.parseLong(voucherIdParam);
                return shopDAO.getShopIdByVoucherId(voucherId);
            } catch (Exception e) {
                // Continue
            }
        }

        // 6. Flash Sale Item ID → Shop ID
        String itemIdParam = req.getParameter("itemId");
        if (itemIdParam != null && !itemIdParam.isEmpty()) {
            try {
                long itemId = Long.parseLong(itemIdParam);
                return shopDAO.getShopIdByFlashSaleItemId(itemId);
            } catch (Exception e) {
                // Continue
            }
        }

        return null;
    }

    /**
     * Handle shop access with special logic
     */
    private void handleShopAccess(HttpServletRequest req, HttpServletResponse resp,
            FilterChain chain, User user, String path)
            throws IOException, ServletException {

        String action = req.getParameter("action");

        // Allow authenticated users to register shop
        if ("register".equals(action)) {
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            chain.doFilter(req, resp);
            return;
        }

        // Allow authenticated users to check shop status
        if ("check-status".equals(action)) {
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            chain.doFilter(req, resp);
            return;
        }

        // For other shop actions
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        // ADMIN has full access
        if (hasRole(user, "ADMIN")) {
            chain.doFilter(req, resp);
            return;
        }

        // SELLER must have shop ownership
        if (!hasRole(user, "SELLER")) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        // Validate shop ownership for SELLER
        if (!validateShopOwnership(req, user)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        chain.doFilter(req, resp);
    }

    /**
     * Get path without context
     */
    private String getPathWithoutContext(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();
        return requestURI.substring(contextPath.length());
    }

    /**
     * Check if path is static resource
     */
    private boolean isStaticResource(String path) {
        return path.startsWith("/assets/") ||
                path.startsWith("/WEB-INF/") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".jpg") ||
                path.endsWith(".png") ||
                path.endsWith(".gif") ||
                path.endsWith(".ico") ||
                path.endsWith(".woff") ||
                path.endsWith(".woff2") ||
                path.endsWith(".ttf");
    }

    /**
     * Check if path is public
     */
    private boolean isPublicPath(String path) {
        if (path.equals("/") || path.isEmpty()) {
            return true;
        }

        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) || path.startsWith(publicPath + "/")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if path is admin-only
     */
    private boolean isAdminOnlyPath(String path) {
        for (String adminPath : ADMIN_ONLY_PATHS) {
            if (path.equals(adminPath) || path.startsWith(adminPath + "/")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if path requires SELLER role
     */
    private boolean isSellerPath(String path) {
        for (String sellerPath : SELLER_PATHS) {
            if (path.equals(sellerPath) || path.startsWith(sellerPath + "/")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if path requires authentication
     */
    private boolean requiresAuthentication(String path) {
        for (String authPath : AUTH_REQUIRED_PATHS) {
            if (path.equals(authPath) || path.startsWith(authPath + "/")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user has specific role
     */
    private boolean hasRole(User user, String role) {
        return user != null
                && user.getRoles() != null
                && user.getRoles().contains(role);
    }

    @Override
    public void destroy() {
        System.out.println("[FILTER] AuthenticationFilter destroyed");
    }
}