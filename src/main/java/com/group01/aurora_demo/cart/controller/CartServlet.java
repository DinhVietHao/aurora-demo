package com.group01.aurora_demo.cart.controller;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dao.dto.ShopCartDTO;

@WebServlet("/cart/*")
public class CartServlet extends HttpServlet {
    private CartItemDAO cartItemDAO;
    private VoucherDAO voucherDAO;

    public CartServlet() {
        this.cartItemDAO = new CartItemDAO();
        this.voucherDAO = new VoucherDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getPathInfo();

        if (path == null || path.equals("/") || path.equals("/view")) {
            List<com.group01.aurora_demo.cart.model.CartItem> cartItems = cartItemDAO
                    .getCartItemsByUserId(user.getId());
            if (cartItems.isEmpty()) {
                req.setAttribute("shopCarts", null);
            } else {
                Map<Long, List<CartItem>> grouped = cartItems.stream()
                        .collect(Collectors.groupingBy(
                                ci -> ci.getProduct().getShop().getShopId(),
                                LinkedHashMap::new,
                                Collectors.toList()));

                List<ShopCartDTO> shopCarts = grouped.entrySet().stream().map(entry -> {
                    ShopCartDTO shopCartDTO = new ShopCartDTO();
                    shopCartDTO.setShop(entry.getValue().get(0).getProduct().getShop());
                    shopCartDTO.setItems(entry.getValue());
                    shopCartDTO.setVouchers(voucherDAO.getActiveVouchersByShopId(entry.getKey()));
                    return shopCartDTO;
                }).toList();

                req.setAttribute("shopCarts", shopCarts);
                req.setAttribute("systemVouchers", voucherDAO.getActiveSystemVouchers());
            }
            req.getRequestDispatcher("/WEB-INF/views/customer/cart/cart.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JSONObject json = new JSONObject();

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getPathInfo();
        switch (path) {
            case "/add": {
                try {
                    long productId = Long.parseLong(req.getParameter("productId"));

                    CartItem existingItem = cartItemDAO.getCartItem(user.getId(), productId);

                    if (existingItem != null) {
                        existingItem.setQuantity(existingItem.getQuantity() + 1);
                        cartItemDAO.updateQuantity(existingItem);
                        json.put("success", true);
                        json.put("message", "Đã tăng số lượng sản phẩm trong giỏ hàng.");
                    } else {
                        CartItem newItem = new CartItem();
                        newItem.setUserId(user.getId());
                        newItem.setProductId(productId);
                        newItem.setQuantity(1);

                        double unitPrice = cartItemDAO.getUnitPriceByProductId(productId);
                        newItem.setUnitPrice(unitPrice);

                        cartItemDAO.addCartItem(newItem);
                        json.put("success", true);
                        json.put("message", "Đã thêm sản phẩm vào giỏ hàng.");
                    }

                    int cartCount = cartItemDAO.getDistinctItemCount(user.getId());
                    session.setAttribute("cartCount", cartCount);
                    json.put("cartCount", cartCount);

                } catch (Exception e) {
                    json.put("success", false);
                    json.put("message", "Có lỗi xảy ra, vui lòng thử lại.");
                }
                out.print(json.toString());
                break;
            }

            case "/delete": {
                try {
                    long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
                    boolean deleteCartItem = cartItemDAO.deleteCartItem(cartItemId);

                    if (deleteCartItem) {
                        json.put("success", true);
                        int cartCount = cartItemDAO.getDistinctItemCount(user.getId());
                        session.setAttribute("cartCount", cartCount);
                        json.put("cartCount", cartCount);
                    } else {
                        json.put("success", false);
                        json.put("message", "Xóa sản phẩm thất bại.");
                    }
                } catch (Exception e) {
                    json.put("success", false);
                    json.put("message", "Có lỗi xảy ra.");
                }
                out.print(json.toString());
                break;
            }

            case "/update-quantity": {
                try {
                    long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
                    int quantity = Integer.parseInt(req.getParameter("quantity"));

                    CartItem cartItem = cartItemDAO.getCartItemById(cartItemId);

                    if (cartItem == null) {
                        json.put("success", false);
                        json.put("message", "Sản phẩm không tồn tại trong giỏ hàng");
                    } else {
                        cartItem.setQuantity(quantity);
                        cartItem.setSubtotal(quantity * cartItem.getUnitPrice());
                        cartItemDAO.updateQuantity(cartItem);

                        json.put("success", true);
                        json.put("message", "Cập nhật số lượng thành công");
                    }
                } catch (Exception e) {
                    json.put("success", false);
                    json.put("message", "Có lỗi xảy ra");
                }
                out.print(json.toString());
                break;
            }

            case "/update-check": {
                try {
                    long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
                    boolean isChecked = Boolean.parseBoolean(req.getParameter("checked"));

                    boolean updateIsChecked = cartItemDAO.updateIsChecked(cartItemId, isChecked);

                    if (updateIsChecked) {
                        json.put("success", true);
                    } else {
                        json.put("success", false);
                        json.put("message", "Cập nhật checked không thành công");
                    }
                } catch (Exception e) {
                    json.put("success", false);
                    json.put("message", "Có lỗi xảy ra");
                }
                out.print(json.toString());
                break;
            }

            case "/check-all": {
                try {
                    long userId = user.getId();
                    boolean isChecked = Boolean.parseBoolean(req.getParameter("checked"));

                    boolean updateAllIsChecked = cartItemDAO.updateAllChecked(userId, isChecked);

                    if (updateAllIsChecked) {
                        json.put("success", true);
                    } else {
                        json.put("success", false);
                        json.put("message", "Cập nhật checked không thành công");
                    }
                } catch (Exception e) {
                    json.put("success", false);
                    json.put("message", "Có lỗi xảy ra");
                }
                out.print(json.toString());
                break;
            }

            default:
                json.put("success", false);
                json.put("message", "API không hợp lệ.");
                out.print(json.toString());
                break;
        }

    }
}