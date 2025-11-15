package com.group01.aurora_demo.cart.controller;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;

import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.cart.utils.CurrencyFormatter;
import com.group01.aurora_demo.catalog.controller.NotificationServlet;
import com.group01.aurora_demo.catalog.dao.FlashSaleDAO;
import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.model.FlashSaleItem;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dao.dto.ShopCartDTO;

@WebServlet("/cart/*")
public class CartServlet extends NotificationServlet {
    private CartItemDAO cartItemDAO;
    private VoucherDAO voucherDAO;
    private ProductDAO productDAO;
    private FlashSaleDAO flashSaleDAO;

    public CartServlet() {
        this.cartItemDAO = new CartItemDAO();
        this.voucherDAO = new VoucherDAO();
        this.productDAO = new ProductDAO();
        this.flashSaleDAO = new FlashSaleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            session.setAttribute("toastType", "warning");
            session.setAttribute("toastMsg", "Vui lòng đăng nhập trước khi đặt hàng.");
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
                    shopCartDTO.setVouchers(voucherDAO.getActiveVouchersByShopId(entry.getKey(), user.getUserID()));
                    return shopCartDTO;
                }).toList();

                req.setAttribute("shopCarts", shopCarts);
                req.setAttribute("systemVouchers", voucherDAO.getActiveSystemVouchers(user.getUserID()));
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
        if (path == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        switch (path) {
            case "/add": {
                try {
                    long productId = Long.parseLong(req.getParameter("productId"));
                    Product product = productDAO.getBasicProductById(productId);
                    if (!isProductAvailable(product, json))
                        return;
                    FlashSaleItem flashItem = flashSaleDAO.getActiveFlashSaleItemByProduct(productId);

                    CartItem existingItem = cartItemDAO.getCartItem(user.getId(), productId);
                    int currentQty = existingItem != null ? existingItem.getQuantity() : 0;
                    int newQuantity = currentQty + 1;

                    if (flashItem != null) {
                        int remainingFlashStock = flashItem.getFsStock();
                        if (currentQty <= remainingFlashStock && newQuantity > remainingFlashStock) {
                            json.put("success", true);
                            json.put("check", "flashsale_exceeded");
                            json.put("titleModal", "Vượt số lượng Flash Sale");
                            json.put("messageModal", "Giá bán này vượt số lượng mua tối đa chỉ " + remainingFlashStock
                                    + " sản phẩm, giá bán sẽ thay đổi thành "
                                    + CurrencyFormatter.format(product.getSalePrice()));
                        }
                    } else {
                        if (newQuantity > product.getQuantity()) {
                            json.put("success", false);
                            json.put("type", "warning");
                            json.put("title", "Không đủ số lượng");
                            json.put("message",
                                    "Sản phẩm '" + product.getTitle() + "' không đủ số lượng để thêm vào giỏ hàng.");
                            out.print(json.toString());
                            break;
                        }
                    }
                    if (existingItem != null) {
                        existingItem.setQuantity(newQuantity);
                        cartItemDAO.updateQuantity(existingItem);
                        json.put("success", true);
                        json.put("type", "success");
                        json.put("title", "Đã cập nhật");
                        json.put("message", "Đã tăng số lượng sản phẩm trong giỏ hàng.");
                    } else {
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
                    if (!isProductAvailable(product, json))
                        return;

                    FlashSaleItem flashItem = flashSaleDAO.getActiveFlashSaleItemByProduct(productId);
                    if (flashItem != null) {
                        int available = flashItem.getFsStock();
                        if (available <= 0) {
                            json.put("success", false);
                            json.put("type", "warning");
                            json.put("title", "Hết hàng Flash Sale");
                            json.put("message", "Sản phẩm '" + product.getTitle() + "' trong Flash Sale đã hết hàng.");
                            out.print(json.toString());
                            return;
                        }
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
                    CartItem cartItem = cartItemDAO.getCartItemById(cartItemId);
                    if (cartItem == null) {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Không tìm thấy sản phẩm");
                        json.put("message", "Sản phẩm không tồn tại trong giỏ hàng.");
                        out.print(json.toString());
                        break;
                    }
                    Product product = productDAO.getProductWithEffectivePrice(cartItem.getProductId(), quantity);
                    if (!isProductAvailable(product, json))
                        return;
                    FlashSaleItem flashSaleItem = flashSaleDAO
                            .getActiveFlashSaleItemByProduct(product.getProductId());
                    ;
                    if (flashSaleItem != null) {
                        int availableFlashStock = flashSaleItem.getFsStock();

                        if (cartItem.getQuantity() <= availableFlashStock && quantity > availableFlashStock) {
                            json.put("success", true);
                            json.put("check", "flashsale_exceeded");
                            json.put("title", "Vượt số lượng Flash Sale");
                            json.put("message", "Giá bán này vượt số lượng mua tối đa chỉ " + availableFlashStock
                                    + " sản phẩm, giá bán sẽ thay đổi thành "
                                    + CurrencyFormatter.format(product.getSalePrice()));
                            json.put("salePrice", product.getSalePrice());
                        }
                    } else {
                        if (quantity > product.getQuantity()) {
                            json.put("success", false);
                            json.put("type", "warning");
                            json.put("title", "Không đủ hàng");
                            json.put("message", "Sản phẩm '" + product.getTitle() + "' không đủ số lượng trong kho.");
                            out.print(json.toString());
                            break;
                        }
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
                    json.put("salePrice", product.getSalePrice());

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

    public boolean isProductAvailable(Product product, JSONObject json) {
        if (product == null) {
            json.put("success", false)
                    .put("type", "error")
                    .put("title", "Lỗi hệ thống")
                    .put("message", "Không tìm thấy sản phẩm.");
            return false;
        }
        if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
            json.put("success", false)
                    .put("type", "warning")
                    .put("title", "Sản phẩm không khả dụng")
                    .put("message", "Sản phẩm này hiện không được bán.");
            return false;
        }
        if (product.getQuantity() == null || product.getQuantity() <= 0) {
            json.put("success", false)
                    .put("type", "warning")
                    .put("title", "Hết hàng")
                    .put("message", "Sản phẩm này hiện đã hết hàng.");
            return false;
        }
        return true;
    }

}