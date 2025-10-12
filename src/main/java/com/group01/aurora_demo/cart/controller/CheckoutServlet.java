package com.group01.aurora_demo.cart.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dao.dto.CheckoutSummaryDTO;
import com.group01.aurora_demo.cart.dao.dto.ShopCartDTO;
import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.cart.service.CheckoutService;
import com.group01.aurora_demo.cart.utils.VoucherValidator;
import com.group01.aurora_demo.profile.dao.AddressDAO;
import com.group01.aurora_demo.profile.model.Address;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
import com.group01.aurora_demo.shop.model.Voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/checkout/*")
public class CheckoutServlet extends HttpServlet {
    private CartItemDAO cartItemDAO;
    private VoucherDAO voucherDAO;
    private AddressDAO addressDAO;
    private VoucherValidator voucherValidator;
    private CheckoutService checkoutService;

    public CheckoutServlet() {
        this.cartItemDAO = new CartItemDAO();
        this.voucherDAO = new VoucherDAO();
        this.addressDAO = new AddressDAO();
        this.voucherValidator = new VoucherValidator();
        this.checkoutService = new CheckoutService();
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
        if (path == null || path.equals("/")) {
            List<CartItem> cartItems = cartItemDAO
                    .getCheckedCartItems(user.getId());

            if (cartItems.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }
            Map<Long, List<CartItem>> grouped = cartItems.stream()
                    .collect(Collectors.groupingBy(ci -> ci.getProduct().getShop().getShopId(),
                            LinkedHashMap::new,
                            Collectors.toList()));

            List<ShopCartDTO> shopCarts = grouped.entrySet().stream().map(entry -> {
                ShopCartDTO shopCartDTO = new ShopCartDTO();
                shopCartDTO.setShop(entry.getValue().get(0).getProduct().getShop());
                shopCartDTO.setItems(entry.getValue());
                shopCartDTO.setVouchers(voucherDAO.getActiveVouchersByShopId(entry.getKey()));
                return shopCartDTO;
            }).toList();

            List<Address> addressList = this.addressDAO.getAddressesByUserId(user.getId());
            boolean isAddress = addressDAO.hasAddress(user.getId());

            Address selectedAddress = this.addressDAO.getDefaultAddress(user.getId());
            String addressId = req.getParameter("addressId");
            if (addressId != null && !addressId.isEmpty()) {
                try {
                    selectedAddress = this.addressDAO.getAddressById(user.getId(), Long.parseLong(addressId));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            // long totalShippingFee = 0;
            // if (selectedAddress != null) {
            // for (Map.Entry<Long, List<CartItem>> entry : grouped.entrySet()) {
            // var items = entry.getValue();
            // var shop = items.get(0).getProduct().getShop();
            // double shopWeight = items.stream()
            // .mapToDouble(ci -> ci.getProduct().getWeight() * ci.getQuantity())
            // .sum();
            // double fee = shippingCalculator.calculateShippingFee(
            // shop.getPickupAddress().getCity(),
            // selectedAddress.getCity(),
            // shopWeight);

            // totalShippingFee += fee;
            // }
            // }
            req.setAttribute("shopCarts", shopCarts);
            req.setAttribute("systemVouchers", voucherDAO.getActiveSystemVouchers());
            req.setAttribute("addresses", addressList);
            req.setAttribute("isAddress", isAddress);
            req.setAttribute("address", selectedAddress);
            req.setAttribute("selectedAddressId", selectedAddress != null ? selectedAddress.getAddressId() : null);
            // req.setAttribute("shippingFee", totalShippingFee);

            req.getRequestDispatcher("/WEB-INF/views/customer/checkout/checkout.jsp").forward(req, resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JSONObject json = new JSONObject();

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String path = req.getPathInfo();
        if (path == null) {
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }
        switch (path) {
            case "/update-summary": {
                try {
                    Long addressId = Long.parseLong(req.getParameter("addressId"));
                    String systemVoucherDiscountCode = req.getParameter("systemVoucherDiscount");
                    String systemVoucherShipCode = req.getParameter("systemVoucherShip");

                    Map<Long, String> shopVouchers = new HashMap<>();
                    req.getParameterMap().forEach((key, value) -> {
                        if (key.startsWith("shopVoucher_")) {
                            long shopId = Long.parseLong(key.replace("shopVoucher_", ""));
                            shopVouchers.put(shopId, value[0]);
                        }
                    });

                    CheckoutSummaryDTO summary = this.checkoutService.calculateCheckoutSummary(
                            user.getId(),
                            addressId,
                            systemVoucherDiscountCode,
                            systemVoucherShipCode,
                            shopVouchers);

                    json.put("success", true);
                    json.put("totalProduct", summary.getTotalProduct());
                    json.put("totalDiscount", summary.getTotalDiscount());
                    json.put("totalShippingFee", summary.getTotalShippingFee());
                    json.put("shipDiscount", summary.getShippingDiscount());
                    json.put("finalAmount", summary.getFinalAmount());

                } catch (Exception e) {
                    e.printStackTrace();
                    json.put("success", false);
                    json.put("message", "Đã xảy ra lỗi khi cập nhật tóm tắt đơn hàng.");
                }
                out.print(json.toString());
                break;
            }
            case "/voucher/shop": {
                try {
                    String code = req.getParameter("code");
                    long shopId = Long.parseLong(req.getParameter("shopId"));
                    Voucher voucher = this.voucherDAO.getVoucherByCode(code, true);
                    if (voucher == null) {
                        json.put("success", false);
                        json.put("message", "Mã giảm giá không tồn tại.");
                        out.print(json.toString());
                        return;
                    }
                    List<CartItem> cartItems = cartItemDAO.getCheckedCartItemsByShop(user.getId(), shopId);
                    double totalShop = cartItems.stream()
                            .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity())
                            .sum();
                    String validation = this.voucherValidator.validate(voucher, totalShop, shopId);
                    if (validation != null) {
                        json.put("success", false);
                        json.put("message", validation);
                        out.print(json.toString());
                        return;
                    }
                    double discountValue = this.voucherValidator.calculateDiscount(voucher, totalShop);
                    json.put("success", true);
                    json.put("discountValue", discountValue);
                    json.put("voucherType", voucher.getDiscountType());
                } catch (Exception e) {
                    json.put("success", false);
                    json.put("message", "Đã xảy ra lỗi khi kiểm tra mã.");
                }
                out.print(json.toString());
                break;
            }

            case "/voucher/system": {
                try {
                    String code = req.getParameter("code");
                    Voucher voucher = this.voucherDAO.getVoucherByCode(code, false);
                    if (voucher == null) {
                        json.put("success", false);
                        json.put("message", "Mã giảm giá không tồn tại.");
                        out.print(json.toString());
                        return;
                    }
                    List<CartItem> cartItems = cartItemDAO.getCheckedCartItems(user.getId());
                    double totalOrder = cartItems.stream()
                            .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity())
                            .sum();

                    String validation = this.voucherValidator.validate(voucher, totalOrder, null);
                    if (validation != null) {
                        json.put("success", false);
                        json.put("message", validation);
                        out.print(json.toString());
                        return;
                    }

                    double discountValue = 0;
                    double shipValue = 0;

                    if (voucher.getDiscountType().equalsIgnoreCase("SHIPPING")) {
                        shipValue = voucher.getValue();
                    } else {
                        discountValue = this.voucherValidator.calculateDiscount(voucher, totalOrder);
                    }

                    json.put("success", true);
                    json.put("type", voucher.getDiscountType());
                    json.put("discountValue", discountValue);
                    json.put("shipValue", shipValue);

                } catch (Exception e) {
                    json.put("success", false);
                    json.put("message", "Đã xảy ra lỗi khi kiểm tra mã.");
                }
                out.print(json.toString());
                break;
            }
            default:
                resp.sendRedirect(req.getContextPath() + "/checkout");
        }
    }
}
