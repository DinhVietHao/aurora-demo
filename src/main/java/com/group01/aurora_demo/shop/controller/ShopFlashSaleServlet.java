package com.group01.aurora_demo.shop.controller;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.dao.FlashSaleDAO;
import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.dao.dto.ProductDTO;
import com.group01.aurora_demo.catalog.model.FlashSale;
import com.group01.aurora_demo.shop.dao.ShopDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/shop/flashSale")
public class ShopFlashSaleServlet extends HttpServlet {

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
            action = "viewListFlashsale";

        ProductDAO productDAO = new ProductDAO();
        ShopDAO shopDAO = new ShopDAO();
        switch (action) {
            case "viewListFlashsale":
                FlashSaleDAO dao = new FlashSaleDAO();
                List<FlashSale> flashSales = dao.getAllFlashSales();
                request.setAttribute("flashSales", flashSales);
                request.getRequestDispatcher("/WEB-INF/views/shop/flashSale.jsp").forward(request, response);
                break;
            case "getActiveProducts":
                try {
                    Long shopId = shopDAO.getShopIdByUserId(user.getUserID());
                    List<ProductDTO> products = productDAO.getActiveProductsByShop(shopId);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    new Gson().toJson(products, response.getWriter());
                } catch (Exception e) {
                    request.setAttribute("errorMessage", "lỗi tải list Product");
                    request.getRequestDispatcher("/WEB-INF/views/shop/flashSale.jsp").forward(request, response);
                }
                break;
            default:
                request.setAttribute("errorMessage", "lỗi tải list Flashsale");
                request.getRequestDispatcher("/WEB-INF/views/shop/flashSale.jsp").forward(request, response);
                break;
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
            action = "registerFlashSale";

        ProductDAO productDAO = new ProductDAO();
        ShopDAO shopDAO = new ShopDAO();
        
        switch (action) {
            case "registerFlashSale":

                break;

            default:
                break;
        }
    }

}
