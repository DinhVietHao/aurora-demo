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
import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dao.dto.ShopCartDTO;

@WebServlet("/cart/*")
public class CartServlet extends HttpServlet {
    private CartItemDAO cartItemDAO;
    private VoucherDAO voucherDAO;
    private ProductDAO productDAO;

    public CartServlet() {
        this.cartItemDAO = new CartItemDAO();
        this.voucherDAO = new VoucherDAO();
        this.productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String path = req.getPathInfo();

        if (path == null || path.equals("/") || path.equals("/view")) {
            List<CartItem> cartItems = cartItemDAO
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
            throws IOException, ServletException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JSONObject json = new JSONObject();

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            json.put("success", false);
            json.put("user", true);
            json.put("title", "Cảnh báo!");
            json.put("type", "warning");
            json.put("message", "Vui lòng đăng nhập trước khi mua hàng.");
            out.print(json.toString());
            return;
        }

        String path = req.getPathInfo();
        switch (path) {
            case "/add": {
                try {
                    long productId = Long.parseLong(req.getParameter("productId"));
                    int MAX_CART_ITEMS = 100;
                    int MAX_QUANTITY_PER_PRODUCT = 20;
                    Product product = productDAO.getBasicProductById(productId);
                    if (product == null) {
                        json.put("success", false);
                        json.put("type", "error");
                        json.put("title", "Lỗi hệ thống");
                        json.put("message", "Không tìm thấy sản phẩm.");
                        out.print(json.toString());
                        break;
                    }

                    if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Sản phẩm không khả dụng");
                        json.put("message", "Sản phẩm này hiện không được bán.");
                        out.print(json.toString());
                        break;
                    }

                    if (product.getQuantity() == null || product.getQuantity() <= 0) {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Hết hàng");
                        json.put("message", "Sản phẩm này hiện đã hết hàng.");
                        out.print(json.toString());
                        break;
                    }
                    CartItem existingItem = cartItemDAO.getCartItem(user.getId(), productId);

                    if (existingItem != null) {
                        int newQuantity = existingItem.getQuantity() + 1;
                        if (newQuantity > MAX_QUANTITY_PER_PRODUCT) {
                            json.put("success", false);
                            json.put("type", "warning");
                            json.put("title", "Quá giới hạn");
                            json.put("message", "Mỗi sản phẩm chỉ có thể thêm tối đa 20 cái vào giỏ hàng.");
                            out.print(json.toString());
                            break;
                        }
                        if (newQuantity > product.getQuantity()) {
                            json.put("success", false);
                            json.put("type", "warning");
                            json.put("title", "Không đủ số lượng");
                            json.put("message",
                                    "Sản phẩm '" + product.getTitle() + "' không đủ số lượng để thêm vào giỏ hàng.");
                            out.print(json.toString());
                            break;
                        }
                        existingItem.setQuantity(newQuantity);
                        cartItemDAO.updateQuantity(existingItem);
                        json.put("success", true);
                        json.put("type", "success");
                        json.put("title", "Đã cập nhật");
                        json.put("message", "Đã tăng số lượng sản phẩm trong giỏ hàng.");
                    } else {
                        int totalItems = cartItemDAO.getDistinctItemCount(user.getId());
                        if (totalItems >= MAX_CART_ITEMS) {
                            json.put("success", false);
                            json.put("type", "warning");
                            json.put("title", "Giỏ hàng đầy");
                            json.put("message",
                                    "Bạn chỉ có thể chứa tối đa " + MAX_CART_ITEMS + " sản phẩm trong giỏ hàng.");
                            out.print(json.toString());
                            break;
                        }
                        CartItem newItem = new CartItem();
                        newItem.setUserId(user.getId());
                        newItem.setProductId(productId);
                        newItem.setQuantity(1);
                        newItem.setUnitPrice(product.getSalePrice());
                        cartItemDAO.addCartItem(newItem);

                        json.put("success", true);
                        json.put("type", "success");
                        json.put("title", "Thành công");
                        json.put("message", "Đã thêm sản phẩm vào giỏ hàng.");
                    }

                    int cartCount = cartItemDAO.getDistinctItemCount(user.getId());
                    session.setAttribute("cartCount", cartCount);
                    json.put("cartCount", cartCount);

                } catch (Exception e) {
                    e.printStackTrace();
                    json.put("success", false);
                    json.put("type", "error");
                    json.put("title", "Lỗi hệ thống");
                    json.put("message", "Đã xảy ra lỗi, vui lòng thử lại sau.");
                }
                out.print(json.toString());
                break;
            }
            case "/buyNow": {
                try {
                    long productId = Long.parseLong(req.getParameter("productId"));
                    Product product = productDAO.getBasicProductById(productId);
                    if (product == null) {
                        json.put("success", false);
                        json.put("type", "error");
                        json.put("title", "Lỗi hệ thống");
                        json.put("message", "Không tìm thấy sản phẩm.");
                        out.print(json.toString());
                        break;
                    }
                    if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Sản phẩm không khả dụng");
                        json.put("message", "Sản phẩm này hiện không được bán.");
                        out.print(json.toString());
                        break;
                    }

                    if (product.getQuantity() == null || product.getQuantity() <= 0) {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Hết hàng");
                        json.put("message", "Sản phẩm này hiện đã hết hàng.");
                        out.print(json.toString());
                        break;
                    }

                    this.cartItemDAO.updateAllChecked(user.getId(), false);

                    CartItem existingItem = cartItemDAO.getCartItem(user.getId(), productId);
                    if (existingItem != null) {
                        existingItem.setQuantity(1);
                        cartItemDAO.updateQuantity(existingItem);
                        cartItemDAO.updateIsChecked(existingItem.getCartItemId(), true);
                    } else {
                        CartItem newItem = new CartItem();
                        newItem.setUserId(user.getId());
                        newItem.setProductId(productId);
                        newItem.setQuantity(1);
                        newItem.setUnitPrice(product.getSalePrice());
                        cartItemDAO.addCartItem(newItem);

                        CartItem added = cartItemDAO.getCartItem(user.getId(), productId);
                        if (added != null) {
                            cartItemDAO.updateIsChecked(added.getCartItemId(), true);
                        }
                    }
                    int cartCount = cartItemDAO.getDistinctItemCount(user.getId());
                    session.setAttribute("cartCount", cartCount);
                    json.put("cartCount", cartCount);
                    json.put("success", true);
                } catch (Exception e) {
                    e.printStackTrace();
                    json.put("success", false);
                    json.put("type", "error");
                    json.put("title", "Lỗi hệ thống");
                    json.put("message", "Đã xảy ra lỗi, vui lòng thử lại sau.");
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
                    json.put("title", "Lỗi!");
                    json.put("type", "error");
                    json.put("message", "Có lỗi xảy ra.");
                }
                out.print(json.toString());
                break;
            }

            case "/update-quantity": {
                try {
                    long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
                    int quantity = Integer.parseInt(req.getParameter("quantity"));
                    int MAX_QUANTITY_PER_PRODUCT = 20;
                    CartItem cartItem = cartItemDAO.getCartItemById(cartItemId);
                    if (cartItem == null) {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Không tìm thấy sản phẩm");
                        json.put("message", "Sản phẩm không tồn tại trong giỏ hàng.");
                        out.print(json.toString());
                        break;
                    }
                    Product product = productDAO.getBasicProductById(cartItem.getProductId());
                    if (product == null || !"ACTIVE".equals(product.getStatus())) {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Sản phẩm không khả dụng");
                        json.put("message", "Sản phẩm này hiện không còn khả dụng.");
                        out.print(json.toString());
                        break;
                    }

                    if (product.getQuantity() <= 0) {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Hết hàng");
                        json.put("message", "Sản phẩm này hiện đã hết hàng.");
                        out.print(json.toString());
                        break;
                    }

                    if (quantity > MAX_QUANTITY_PER_PRODUCT) {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Vượt giới hạn");
                        json.put("message", "Mỗi sản phẩm chỉ có thể mua tối đa " + MAX_QUANTITY_PER_PRODUCT);
                        out.print(json.toString());
                        break;
                    }
                    if (quantity > product.getQuantity()) {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Không đủ hàng");
                        json.put("message", "Sản phẩm '" + product.getTitle() + "' không đủ số lượng trong kho.");
                        out.print(json.toString());
                        break;
                    }

                    cartItem.setQuantity(quantity);
                    cartItem.setSubtotal(quantity * product.getSalePrice());
                    boolean updateQuantity = cartItemDAO.updateQuantity(cartItem);
                    if (!updateQuantity) {
                        json.put("success", false);
                        json.put("type", "error");
                        json.put("title", "Lỗi hệ thống");
                        json.put("message", "Đã xảy ra lỗi, vui lòng thử lại sau.");
                        out.print(json.toString());
                        break;
                    }

                    json.put("success", true);
                    json.put("type", "success");
                    json.put("title", "Thành công");
                    json.put("message", "Cập nhật số lượng thành công.");

                } catch (Exception e) {
                    e.printStackTrace();
                    json.put("success", false);
                    json.put("type", "error");
                    json.put("title", "Lỗi hệ thống!");
                    json.put("message", "Đã xảy ra lỗi, vui lòng thử lại sau.");
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
                    json.put("title", "Lỗi!");
                    json.put("type", "error");
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
                    json.put("title", "Lỗi!");
                    json.put("type", "error");
                    json.put("message", "Có lỗi xảy ra");
                }
                out.print(json.toString());
                break;
            }
            default:
                json.put("success", false);
                json.put("title", "Lỗi!");
                json.put("type", "error");
                json.put("message", "API không hợp lệ.");
                out.print(json.toString());
                break;
        }

    }
}