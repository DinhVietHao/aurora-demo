package com.group01.aurora_demo.cart.controller;

import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.model.CartItem;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import org.json.JSONException;
import java.io.PrintWriter;
import java.io.IOException;
import org.json.JSONObject;

/**
 * Servlet xử lý cập nhật số lượng của một CartItem.
 * URL: /update-cart-item-quantity
 * Method: POST
 * Trả về JSON thông báo success hoặc failure
 * 
 * @author Lê Minh Kha
 */
@WebServlet(name = "UpdateCartItemServlet", urlPatterns = { "/update-cart-item-quantity" })
public class UpdateCartItemQuantityServlet extends HttpServlet {

    /**
     * Flow xử lý khi client gửi request POST /update-cart-item-quantity:
     * 1) Lấy cartItemId và quantity từ request parameter.
     * 2) Lấy CartItem từ DB qua CartItemDAO.getCartItemById().
     * 3) Nếu không tồn tại -> trả JSON { success: false, message: "..."}.
     * 4) Nếu tồn tại -> cập nhật quantity, subtotal và gọi
     * CartItemDAO.updateQuantity().
     * 5) Trả về JSON { success: true, message: "..." } nếu thành công.
     * 6) Nếu có Exception -> log ra console và trả JSON báo lỗi.
     *
     * @param request  HttpServletRequest (chứa cartItemId, quantity)
     * @param response HttpServletResponse (trả về JSON cho client)
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Thiết lập kiểu dữ liệu trả về JSON với charset UTF-8
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        try {
            // Lấy dữ liệu từ request
            long cartItemId = Long.parseLong(request.getParameter("cartItemId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            // Lấy CartItem từ DB
            CartItemDAO cartItemDAO = new CartItemDAO();
            CartItem cartItem = cartItemDAO.getCartItemById(cartItemId);

            if (cartItem == null) {
                // Trả về JSON nếu sản phẩm không tồn tại
                json.put("success", false);
                json.put("message", "Sản phẩm không tồn tại trong giỏ hàng");
            } else {
                // Cập nhật số lượng và subtotal
                cartItem.setQuantity(quantity);
                cartItem.setSubtotal(quantity * cartItem.getUnitPrice());

                // Lưu cập nhật vào DB
                cartItemDAO.updateQuantity(cartItem);

                // Trả về JSON thông báo thành công
                json.put("success", true);
                json.put("message", "Cập nhật số lượng thành công");
            }
        } catch (NumberFormatException | JSONException e) {
            // Bắt và log lỗi nếu có
            System.out.println(e.getMessage());
            json.put("success", false);
            json.put("message", "Có lỗi xảy ra");
        }
        // Trả về JSON response
        out.print(json.toString());
    }

}