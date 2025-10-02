package com.group01.aurora_demo.cart.controller;

import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dto.ShopCartDTO;
import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.cart.model.Shop;
import com.group01.aurora_demo.auth.model.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Servlet cho chức năng "Xem giỏ hàng"
 * - Kiểm tra đăng nhập
 * - Lấy giỏ hàng + danh sách sản phẩm từ DB
 * - Forward dữ liệu sang JSP để render
 * 
 * @author Lê Minh Kha
 */
@WebServlet(name = "ViewCartServlet", urlPatterns = { "/cart" })
public class ViewCartServlet extends HttpServlet {

    /**
     * Flow xử lý khi người dùng truy cập /cart:
     * 1) Lấy session và kiểm tra user đăng nhập ("AUTH_USER").
     * - Nếu chưa đăng nhập -> redirect sang /login.
     * 2) Nếu đã đăng nhập:
     * - Lấy giỏ hàng theo userId.
     * - Nếu có giỏ hàng:
     * + Lấy danh sách CartItem theo userId
     * + Set vào request attribute "cartItems"
     * + Forward sang /WEB-INF/views/cart.jsp để hiển thị
     * - Nếu chưa có giỏ hàng:
     * + Set "cartItems" = null
     * + Forward sang cart.jsp (hiển thị giỏ trống)
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        // Nếu chưa đăng nhập thì chuyển hướng sang trang login
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        CartItemDAO cartItemDAO = new CartItemDAO();

        // Nếu có giỏ hàng -> lấy danh sách sản phẩm trong giỏ
        List<CartItem> cartItems = cartItemDAO.getCartItemsByUserId(user.getId());

        if (cartItems.isEmpty()) {
            req.setAttribute("shopCarts", null);
        } else {
            Map<Long, List<CartItem>> grouped = cartItems.stream()
                    .collect(
                            Collectors.groupingBy(ci -> ci.getProduct().getShop().getShopId(),
                                    LinkedHashMap::new,
                                    Collectors.toList()));
            List<ShopCartDTO> shopCarts = grouped.entrySet().stream().map(entry -> {
                ShopCartDTO shopCartDTO = new ShopCartDTO();
                shopCartDTO.setShop(entry.getValue().get(0).getProduct().getShop());
                shopCartDTO.setItems(entry.getValue());
                return shopCartDTO;
            }).toList();
            req.setAttribute("shopCarts", shopCarts);
        }
        req.getRequestDispatcher("/WEB-INF/views/cart/cart.jsp").forward(req, resp);
    }
}