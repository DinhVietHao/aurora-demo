package com.group01.aurora_demo.cart.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.group01.aurora_demo.cart.dao.OrderShopDAO;
import com.group01.aurora_demo.cart.dao.dto.OrderShopDTO;
import com.group01.aurora_demo.catalog.controller.NotificationServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/email")
public class OrderEmailServlet extends NotificationServlet {

    private OrderShopDAO orderShopDAO;

    public OrderEmailServlet() {

        this.orderShopDAO = new OrderShopDAO();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<OrderShopDTO> orderShops = this.orderShopDAO.getOrderShopsByPaymentId(1);
        Map<Long, List<OrderShopDTO>> grouped = orderShops.stream()
                .collect(Collectors.groupingBy(orderShop -> orderShop.getOrderShopId(),
                        LinkedHashMap::new,
                        Collectors.toList()));
        double totalItems = 0, totalShipping = 0, totalSystemDiscount = 0, totalShopDiscount = 0,
                totalShipDiscount = 0;
        for (Map.Entry<Long, List<OrderShopDTO>> entry : grouped.entrySet()) {
            List<OrderShopDTO> shopItems = entry.getValue();
            if (shopItems.isEmpty())
                continue;

            OrderShopDTO shop = shopItems.get(0);

            totalItems += shop.getSubtotal();
            totalShipping += shop.getShopShippingFee();
            totalSystemDiscount += shop.getSystemDiscount();
            totalShopDiscount += shop.getShopDiscount();
            totalShipDiscount += shop.getSystemShippingDiscount();
        }

        double totalVoucherDiscount = totalSystemDiscount + totalShopDiscount;
        double grandTotal = totalItems + totalShipping - totalVoucherDiscount - totalShipDiscount;

        req.setAttribute("orderShops", orderShops);
        req.setAttribute("totalItems", totalItems);
        req.setAttribute("totalShipping", totalShipping);
        req.setAttribute("totalVoucherDiscount", totalVoucherDiscount);
        req.setAttribute("totalShipDiscount", totalShipDiscount);
        req.setAttribute("grandTotal", grandTotal);
        req.getRequestDispatcher("/WEB-INF/views/customer/order/order_confirmation.jsp").forward(req, resp);

    }
}