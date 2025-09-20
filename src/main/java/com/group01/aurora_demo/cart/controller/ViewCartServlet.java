package com.group01.aurora_demo.cart.controller;

import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.cart.dao.CartDAO;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.model.Cart;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

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

        CartDAO cartDAO = new CartDAO();
        CartItemDAO cartItemDAO = new CartItemDAO();

        // Lấy giỏ hàng của user theo userId
        Cart cart = cartDAO.getCartByUserId(user.getId());
        if (cart != null) {
            // Nếu có giỏ hàng -> lấy danh sách sản phẩm trong giỏ
            List<CartItem> cartItems = cartItemDAO.getCartItemsByUserId(user.getId());
            System.out.println(">>>>>>>>>>> Checked cartItems" + cartItems);
            req.setAttribute("cartItems", cartItems);
            req.getRequestDispatcher("/WEB-INF/views/cart/cart.jsp").forward(req, resp);
        } else {
            // Nếu chưa có giỏ hàng -> set null để JSP xử lý giỏ trống
            req.setAttribute("cartItems", null);
            req.getRequestDispatcher("/WEB-INF/views/cart/cart.jsp").forward(req, resp);

        }
    }
}