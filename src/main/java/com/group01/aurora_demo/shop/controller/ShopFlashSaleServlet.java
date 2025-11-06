package com.group01.aurora_demo.shop.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.dao.FlashSaleDAO;
import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.dao.dto.ProductDTO;
import com.group01.aurora_demo.catalog.model.FlashSale;
import com.group01.aurora_demo.catalog.model.FlashSaleItem;
import com.group01.aurora_demo.shop.dao.ShopDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/shop/flashSale")
public class ShopFlashSaleServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private ShopDAO shopDAO = new ShopDAO();
    private FlashSaleDAO flashSaleDAO = new FlashSaleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            response.sendRedirect("/home");
            return;
        }
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String action = request.getParameter("action");
        if (action == null)
            action = "viewListFlashsale";

        if ("register_success".equals(message)) {
            request.setAttribute("successMessage",
                    "Đã đăng kí falsh Sale thành công.");
        }
        if ("register_failed".equals(error)) {
            request.setAttribute("errorMessage",
                    "Đăng kí falsh Sale thất bại.");
        }
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

                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put("shopId", shopId);
                    responseData.put("products", products);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    new Gson().toJson(responseData, response.getWriter());
                } catch (Exception e) {
                    request.setAttribute("errorMessage", "lỗi tải list Product");
                    request.getRequestDispatcher("/WEB-INF/views/shop/flashSale.jsp").forward(request, response);
                }
                break;
            case "checkProductInFlashSale":
                try {
                    long flashSaleId = Long.parseLong(request.getParameter("flashSaleId"));
                    long productId = Long.parseLong(request.getParameter("productId"));

                    boolean exists = flashSaleDAO.isProductInThisFlashSale(flashSaleId, productId);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    if (exists) {
                        response.getWriter().write(
                                "{\"exists\": true, \"message\": \"Sản phẩm này đã được đăng ký trong Flash Sale này.\"}");
                    } else {
                        response.getWriter().write("{\"exists\": false}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.getWriter().write("{\"exists\": false, \"error\": true}");
                }
                break;
            case "getFlashsaleItem":
                try {
                    long shopId = shopDAO.getShopIdByUserId(user.getUserID());
                    long flashSaleId = Long.parseLong(request.getParameter("flashSaleId"));
                    List<FlashSaleItem> items = flashSaleDAO.getFlashSaleItemsByFlashSaleIdAndShopId(flashSaleId,
                            shopId);
                    request.setAttribute("items", items);
                    request.setAttribute("flashSaleId", flashSaleId);
                    request.getRequestDispatcher("/WEB-INF/views/shop/flashSaleItem.jsp")
                            .forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Lỗi tải list Product: " + e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/views/shop/flashSaleItem.jsp")
                            .forward(request, response);
                }
                break;
            case "getFlashsaleItemDetail":
                try {
                    long itemId = Long.parseLong(request.getParameter("itemId"));

                    FlashSaleItem item = flashSaleDAO.getFlashSaleItemDetail(itemId);
                    if (item == null) {
                        request.setAttribute("errorMessage", "Không tìm thấy sản phẩm Flash Sale.");
                        request.getRequestDispatcher("/WEB-INF/views/shop/flashSaleItemDetail.jsp")
                                .forward(request,
                                        response);
                        return;
                    }

                    List<String> revenueLabels = new ArrayList<>();
                    List<Double> revenueValues = new ArrayList<>();

                    Timestamp start = item.getStartAt();
                    Timestamp end = "APPROVED".equalsIgnoreCase(item.getApprovalStatus())
                            ? new Timestamp(System.currentTimeMillis())
                            : item.getEndAt();
                    if (start == null || end == null) {
                        request.setAttribute("errorMessage", "Thời gian Flash Sale không hợp lệ.");
                        request.getRequestDispatcher("/WEB-INF/views/shop/flashSaleItemDetail.jsp")
                                .forward(request,
                                        response);
                        return;
                    }
                    Map<LocalDate, Double> revenueByDate = flashSaleDAO.getRevenueByFlashSaleItem(itemId, start, end);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
                    for (Map.Entry<LocalDate, Double> entry : revenueByDate.entrySet()) {
                        revenueLabels.add(entry.getKey().format(formatter));
                        revenueValues.add(entry.getValue());
                    }
                    System.out.println("hihiihih---------------------------------------" + revenueByDate);

                    Gson gson = new Gson();
                    request.setAttribute("revenueLabelsJson", gson.toJson(revenueLabels));
                    request.setAttribute("revenueValuesJson", gson.toJson(revenueValues));
                    request.setAttribute("item", item);

                    request.getRequestDispatcher("/WEB-INF/views/shop/flashSaleItemDetail.jsp").forward(request,
                            response);

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage",
                            "Đã xảy ra lỗi khi tải chi tiết Flash Sale Item: " + e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/views/shop/flashSaleItemDetail.jsp")
                            .forward(request,
                                    response);
                }
                break;
            default:
                request.setAttribute("errorMessage", "lỗi tải list Flashsale");
                request.getRequestDispatcher("/WEB-INF/views/shop/flashSaleItemDetail.jsp")
                        .forward(request, response);
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
        FlashSaleDAO flashSaleDAO = new FlashSaleDAO();

        switch (action) {
            case "registerFlashSale":
                try {
                    long flashSaleId = Long.parseLong(request.getParameter("flashSaleId"));
                    long productId = Long.parseLong(request.getParameter("flashsaleProductSelect"));
                    long shopId = Long.parseLong(request.getParameter("flashsaleShopId"));
                    int fsStock = Integer.parseInt(request.getParameter("flashsaleQuantityInput"));
                    double flashPrice = Double.parseDouble(request.getParameter("flashsalePriceInput"));
                    String perUserLimitStr = request.getParameter("flashsaleLimitInput");
                    int perUserLimit = Integer.parseInt(perUserLimitStr);

                    boolean reduceQuantity = productDAO.reduceQuantityProduct(productId, fsStock);
                    if (reduceQuantity) {
                        boolean success = flashSaleDAO.insertFlashSaleItem(
                                flashSaleId, shopId, productId, flashPrice, fsStock, perUserLimit, "PENDING");
                        if (success) {
                            response.sendRedirect(
                                    request.getContextPath()
                                            + "/shop/flashSale?action=viewListFlashsale&message=register_success");
                        } else {
                            response.sendRedirect(
                                    request.getContextPath()
                                            + "/shop/flashSale?action=viewListFlashsale&error=register_failed");
                        }
                    } else {
                        response.sendRedirect(
                                request.getContextPath()
                                        + "/shop/flashSale?action=viewListFlashsale&error=register_failed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Lỗi hệ thống khi tải voucher." + e);
                    request.getRequestDispatcher("/WEB-INF/views/shop/flashSale.jsp")
                            .forward(request, response);
                }
                break;

            default:
                break;
        }
    }

}
