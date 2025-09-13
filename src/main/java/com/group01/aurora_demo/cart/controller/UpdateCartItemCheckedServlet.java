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
 * Servlet xử lý việc cập nhật trạng thái "checked" của một CartItem.
 * URL: /update-cart-item-checked
 * Method: POST
 * Trả về JSON thông báo success hoặc failure
 * 
 * @author Lê Minh Kha
 */
@WebServlet(name = "UpdateCartItemCheckedServlet", urlPatterns = { "/update-cart-item-checked" })
public class UpdateCartItemCheckedServlet extends HttpServlet {
    /**
     * Flow xử lý khi client gửi request POST /update-cart-item-checked:
     * 1) Lấy cartItemId và checked từ request parameter.
     * 2) Gọi CartItemDAO.updateIsChecked(cartItemId, isChecked) để update DB.
     * 3) Nếu thành công -> trả JSON { success: true }.
     * 4) Nếu thất bại -> trả JSON { success: false, message: "..."}.
     * 5) Nếu có Exception -> log ra console và trả JSON báo lỗi.
     *
     * @param request  HttpServletRequest (chứa cartItemId, checked)
     * @param response HttpServletResponse (trả về JSON cho client)
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Thiết lập kiểu dữ liệu trả về JSON với charset UTF-8
        response.setContentType("application/json:charset=UFT-8"); // chú ý: "UFT-8" có thể là typo, chuẩn là "UTF-8"
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        try {
            // Lấy dữ liệu từ request
            long cartItemId = Long.parseLong(request.getParameter("cartItemId"));
            boolean isChecked = Boolean.parseBoolean(request.getParameter("checked"));

            // Cập nhật trạng thái checked cho CartItem
            CartItemDAO cartItemDAO = new CartItemDAO();
            boolean updateIsChecked = cartItemDAO.updateIsChecked(cartItemId, isChecked);

            if (updateIsChecked) {
                json.put("success", true);
            } else {
                // Trường hợp cập nhật thất bại
                json.put("success", false);
                json.put("message", "Cập nhật checked không thành công");
            }

        } catch (Exception e) {
            // Bắt và log lỗi nếu có
            System.out.println(e.getMessage());
            json.put("success", false);
            json.put("message", "Có lỗi xảy ra");
        }

        // Trả về JSON response
        out.print(json.toString());
    }

}