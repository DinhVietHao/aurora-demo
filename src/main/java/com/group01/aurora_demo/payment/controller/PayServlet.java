package com.group01.aurora_demo.payment.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet hiển thị trang thanh toán.
 * 
 * @author Lê Minh Kha
 */
@WebServlet(name = "PayServlet", urlPatterns = { "/pay" })
public class PayServlet extends HttpServlet {
    // View model đơn giản; sau này thay bằng CartService/OrderService
    public static class ItemVM {
        public long productId;
        public String title;
        public String author;
        public String image; // /assets/images/...
        public long unitPrice; // VND
        public Long originalPrice; // nullable
        public int quantity;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Dữ liệu demo
        List<ItemVM> items = new ArrayList<>();
        ItemVM a = new ItemVM();
        a.productId = 101;
        a.title = "Đàn Ông Sao Hỏa Đàn Bà Sao Kim";
        a.author = "John Gray";
        a.image = "product-2.jpg";
        a.unitPrice = 142_000;
        a.originalPrice = 188_000L;
        a.quantity = 1;
        items.add(a);

        ItemVM b = new ItemVM();
        b.productId = 102;
        b.title = "Nghĩ Giàu Làm Giàu";
        b.author = "Napoleon Hill";
        b.image = "product-3.jpg";
        b.unitPrice = 142_000;
        b.originalPrice = 188_000L;
        b.quantity = 1;
        items.add(b);

        req.setAttribute("items", items);

        // Tổng tiền demo
        req.setAttribute("totalProductPrice", 188_000);
        req.setAttribute("shippingFee", 30_000);
        req.setAttribute("totalPayment", 142_000 * 2 + 30_000 - 0); // ví dụ

        // Địa chỉ demo
        req.setAttribute("shipName", "Lê Minh Kha");
        req.setAttribute("shipPhone", "0912345123");
        req.setAttribute("shipTag", "Nhà");
        req.setAttribute("shipAddress", "Đường số 1, Phường An Khánh, Quận Ninh Kiều, Cần Thơ");

        req.getRequestDispatcher("/WEB-INF/views/payment/pay.jsp").forward(req, resp);
    }
}