package com.group01.aurora_demo.shop.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
import com.group01.aurora_demo.shop.model.Voucher;

@WebServlet("/shop/voucher")
public class VoucherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            response.sendRedirect("/home");
            return;
        }

        String action = request.getParameter("action");
        if (action == null)
            action = "view";

        try {
            VoucherDAO voucherDAO = new VoucherDAO();
            ShopDAO shopDAO = new ShopDAO();
            switch (action) {
                case "view":
                    long shopId = shopDAO.getShopIdByUserId(user.getId());

                    Map<String, Integer> stats = voucherDAO.getVoucherStatsByShop(shopId);
                    List<Voucher> listVoucher = voucherDAO.getVouchersByShopId(shopId);

                    request.setAttribute("stats", stats);
                    request.setAttribute("listVoucher", listVoucher);
                    request.getRequestDispatcher("/WEB-INF/views/shop/voucherManage.jsp").forward(request, response);
                    break;
                case "detail":
                    String voucherCode = request.getParameter("voucherCode");
                    Voucher voucher = voucherDAO.getVoucherByVoucherCode(voucherCode);
                    request.setAttribute("voucher", voucher);
                    request.getRequestDispatcher("/WEB-INF/views/shop/voucherDetail.jsp").forward(request, response);
                    break;
                case "create":
                    request.getRequestDispatcher("/WEB-INF/views/shop/createVoucher.jsp").forward(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action: " + action);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            response.sendRedirect("/home");
            return;
        }

        String action = request.getParameter("action");
        if (action == null)
            action = "view";
            
        try {

            switch (action) {
                case "checkVoucherCode":
                    String Shopid = 
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action: " + action);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
