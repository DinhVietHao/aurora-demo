package com.group01.aurora_demo.cart.controller;

import com.group01.aurora_demo.cart.dao.CartItemDAO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

/**
 * Servlet cho chức năng "Chọn/Bỏ chọn tất cả sản phẩm trong giỏ hàng"
 * - Nhận cartId và trạng thái checked từ request
 * - Gọi DAO để update cột "checked" cho toàn bộ cartItem
 * - Trả về JSON cho client để cập nhật UI
 * 
 * author Lê Minh Kha
 */
@WebServlet(name = "UpdateCartAllCheckedServlet", urlPatterns = { "/update-cart-all-checked" })
public class UpdateCartAllCheckedServlet extends HttpServlet {

    /**
     * Flow xử lý khi client gửi request POST /update-cart-all-checked:
     * 1) Lấy cartId và checked từ request parameter.
     * 2) Gọi CartItemDAO.updateAllChecked(cartId, checked) để update DB.
     * 3) Nếu thành công -> trả JSON { success: true }.
     * 4) Nếu thất bại -> trả JSON { success: false, message: "..."}.
     * 5) Nếu có Exception -> log ra console và trả JSON báo lỗi.
     *
     * @param request  HttpServletRequest (chứa cartId, checked)
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
            // Lấy cartId và trạng thái checked từ request
            long cartId = Long.parseLong(request.getParameter("cartId"));
            boolean isChecked = Boolean.parseBoolean(request.getParameter("checked"));

            CartItemDAO cartItemDAO = new CartItemDAO();
            // Update trạng thái checked cho tất cả item trong giỏ
            boolean updateAllIsChecked = cartItemDAO.updateAllChecked(cartId, isChecked);
            if (updateAllIsChecked) {
                json.put("success", true);
            } else {
                json.put("success", false);
                json.put("message", "Cập nhật checked không thành công");
            }

        } catch (Exception e) {
            // Xử lý lỗi chung
            System.out.println(e.getMessage());
            json.put("success", false);
            json.put("message", "Có lỗi xảy ra");
        }

        // Trả JSON response về client
        out.print(json.toString());
    }

}