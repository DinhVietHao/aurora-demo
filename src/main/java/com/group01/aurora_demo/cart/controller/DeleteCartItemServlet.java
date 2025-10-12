package com.group01.aurora_demo.cart.controller;

import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dao.CartDAO;
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
 * Servlet cho chức năng "Xóa sản phẩm khỏi giỏ hàng"
 * - Nhận cartItemId, cartId từ request
 * - Xóa sản phẩm trong DB
 * - Nếu giỏ rỗng -> xóa luôn giỏ hàng
 * - Trả JSON response về client để cập nhật giao diện
 * 
 * @author Lê Minh Kha
 */
@WebServlet(name = "DeleteCartItemServlet", urlPatterns = { "/delete-cart-item" })
public class DeleteCartItemServlet extends HttpServlet {

    /**
     * Flow xử lý khi client gửi request POST /delete-cart-item:
     * 1) Lấy cartItemId và cartId từ request parameter.
     * 2) Gọi CartItemDAO.deleteCartItem(cartItemId) để xóa item trong DB.
     * 3) Nếu xóa thành công:
     * - Lấy lại số lượng item khác nhau trong giỏ (cartCount).
     * - Nếu cartCount = 0 -> gọi CartDAO.deleteCart(cartId) để xóa giỏ hàng trống.
     * - Cập nhật lại "cartCount" trong session.
     * - Trả JSON { success: true, cartCount: ... } về client.
     * 4) Nếu xóa thất bại -> trả JSON { success: false, message: "..."}.
     * 5) Nếu có Exception -> log ra console và trả JSON báo lỗi.
     *
     * @param request  HttpServletRequest (chứa cartItemId, cartId)
     * @param response HttpServletResponse (trả về JSON cho client)
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

            // Lấy cartItemId và cartId từ request parameter
            long cartItemId = Long.parseLong(request.getParameter("cartItemId"));
            long cartId = Long.parseLong(request.getParameter("cartId"));

            CartItemDAO cartItemDAO = new CartItemDAO();

            // Xóa item trong DB
            boolean deleteCartItem = cartItemDAO.deleteCartItem(cartItemId);

            if (deleteCartItem) {
                // Nếu còn item -> update lại cartCount, nếu giỏ rỗng -> xóa giỏ
                int cartCount = cartItemDAO.getDistinctItemCount(cartId);
                if (cartCount == 0) {
                    CartDAO cartDAO = new CartDAO();
                    cartDAO.deleteCart(cartId);
                }

                json.put("success", true);
                session.setAttribute("cartCount", cartCount);
                json.put("cartCount", cartCount);

            } else {
                // Xóa thất bại
                json.put("success", false);
                json.put("message", "Xóa sản phẩm thất bại.");
            }
        } catch (Exception e) {
            // Xử lý lỗi chung
            System.out.println(e.getMessage());
            json.put("success", false);
            json.put("message", "Có lỗi xảy ra.");
        }
        // Trả JSON response về client
        out.print(json.toString());
    }

}