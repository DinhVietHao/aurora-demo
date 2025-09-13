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
import java.io.PrintWriter;
import org.json.JSONObject;

/**
 * Servlet cho chức năng "Thêm vào giỏ hàng"
 * Trả về response JSON cho phía client (AJAX)
 * 
 * @author Lê Minh Kha
 */
@WebServlet(name = "AddToCartServlet", urlPatterns = { "/add-to-cart" })
public class AddToCartServlet extends HttpServlet {

    /**
     * Flow thêm sản phẩm vào giỏ hàng:
     * 1) Kiểm tra user đăng nhập từ session ("AUTH_USER").
     * - Nếu chưa đăng nhập -> trả về JSON { success: false, message: "..."}.
     * 2) Nếu đã đăng nhập:
     * - Lấy productId từ request.
     * - Lấy giỏ hàng theo userId, nếu chưa có thì tạo mới.
     * 3) Kiểm tra sản phẩm đã tồn tại trong giỏ chưa:
     * - Nếu đã có:
     * + Tăng số lượng lên 1
     * + Cập nhật subtotal (quantity * unitPrice)
     * + Update DB bằng CartItemDAO.updateQuantity(...)
     * + Trả về message "Đã tăng số lượng sản phẩm trong giỏ hàng."
     * - Nếu chưa có:
     * + Tạo mới CartItem với quantity = 1
     * + Lấy đơn giá từ DB (CartItemDAO.getUnitPriceByProductId)
     * + Set unitPrice + subtotal
     * + Lưu vào DB bằng CartItemDAO.addCartItem(...)
     * + Trả về message "Đã thêm sản phẩm vào giỏ hàng."
     * 4) Lấy lại tổng số sản phẩm khác nhau trong giỏ (distinct count).
     * - Update vào session attribute "cartCount"
     * - Trả về JSON gồm cartCount để cập nhật UI client.
     * 5) Nếu xảy ra Exception -> log ra console và trả về JSON báo lỗi.
     *
     * @param request  HttpServletRequest chứa dữ liệu từ client (productId,
     *                 session...)
     * @param response HttpServletResponse trả về JSON cho client
     * @throws ServletException
     * @throws IOException
     */

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("AUTH_USER");

            // Nếu chưa đăng nhập thì trả về thông báo lỗi
            if (user == null) {
                json.put("success", false);
                json.put("message", "Bạn cần đăng nhập để thêm sản phẩm vào giỏ.");
                out.print(json.toString());
                return;
            }

            // Lấy productId từ request
            long productId = Long.parseLong(request.getParameter("productId"));

            // Lấy giỏ hàng của user, nếu chưa có thì tạo mới
            CartDAO cartDAO = new CartDAO();
            Cart cart = cartDAO.getCartByUserId(user.getId());
            if (cart == null) {
                cart = cartDAO.createCart(user.getId());
            }

            CartItemDAO cartItemDAO = new CartItemDAO();
            // Kiểm tra xem sản phẩm đã có trong giỏ chưa
            CartItem existingItem = cartItemDAO.getCartItem(cart.getCartId(), productId);

            if (existingItem != null) {
                // Nếu có rồi -> tăng số lượng và cập nhật subtotal
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                double subtotal = existingItem.getQuantity() * existingItem.getUnitPrice();
                existingItem.setSubtotal(subtotal);
                cartItemDAO.updateQuantity(existingItem);
                json.put("success", true);
                json.put("message", "Đã tăng số lượng sản phẩm trong giỏ hàng.");

            } else {
                // Nếu chưa có -> tạo mới CartItem
                CartItem newItem = new CartItem();
                newItem.setCartId(cart.getCartId());
                newItem.setProductId(productId);
                newItem.setQuantity(1);

                // Lấy giá sản phẩm và set subtotal
                double unitPrice = cartItemDAO.getUnitPriceByProductId(productId);
                newItem.setUnitPrice(unitPrice);
                newItem.setSubtotal(unitPrice);
                cartItemDAO.addCartItem(newItem);
                json.put("success", true);
                json.put("message", "Đã thêm sản phẩm vào giỏ hàng.");
            }

            // Cập nhật số lượng item khác nhau trong giỏ và lưu vào session
            int cartCount = cartItemDAO.getDistinctItemCount(cart.getCartId());
            session.setAttribute("cartCount", cartCount);
            json.put("cartCount", cartCount);

        } catch (NumberFormatException e) {
            // Xử lý lỗi chung
            System.out.println(e.getMessage());
            json.put("success", false);
            json.put("message", "Có lỗi xảy ra, vui lòng thử lại.");
        }

        // Trả JSON response về client
        out.print(json.toString());
    }

}