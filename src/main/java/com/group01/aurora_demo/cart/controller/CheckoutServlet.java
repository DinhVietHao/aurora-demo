package com.group01.aurora_demo.cart.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dao.dto.ShopCartDTO;
import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.cart.service.GHNShippingService;
import com.group01.aurora_demo.cart.utils.ShippingCalculator;
import com.group01.aurora_demo.profile.dao.AddressDAO;
import com.group01.aurora_demo.profile.model.Address;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
// import com.group01.aurora_demo.shop.model.Shop;
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
    private ShippingCalculator shippingCalculator;
    private GHNShippingService ghnShippingService;

    public CheckoutServlet() {
        this.cartItemDAO = new CartItemDAO();
        this.voucherDAO = new VoucherDAO();
        this.addressDAO = new AddressDAO();
        this.shippingCalculator = new ShippingCalculator();
        this.ghnShippingService = new GHNShippingService();
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

            long totalShippingFee = 0;
            if (selectedAddress != null) {
                for (Map.Entry<Long, List<CartItem>> entry : grouped.entrySet()) {
                    var items = entry.getValue();
                    var shop = items.get(0).getProduct().getShop();
                    double shopWeight = items.stream()
                            .mapToDouble(ci -> ci.getProduct().getWeight() * ci.getQuantity())
                            .sum();
                    double fee = shippingCalculator.calculateShippingFee(
                            shop.getPickupAddress().getCity(),
                            selectedAddress.getCity(),
                            shopWeight);

                    totalShippingFee += fee;
                }
            }
            req.setAttribute("shopCarts", shopCarts);
            req.setAttribute("systemVouchers", voucherDAO.getActiveSystemVouchers());
            req.setAttribute("addresses", addressList);
            req.setAttribute("isAddress", isAddress);
            req.setAttribute("address", selectedAddress);
            req.setAttribute("selectedAddressId", selectedAddress != null ? selectedAddress.getAddressId() : null);
            req.setAttribute("shippingFee", totalShippingFee);

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
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getPathInfo();
        switch (path) {
            case "/update-summary": {
                try {
                    String addressId = req.getParameter("addressId");
                    String systemVoucherDiscountCode = req.getParameter("systemVoucherDiscount");
                    String systemVoucherShipCode = req.getParameter("systemVoucherShip");

                    Address selectedAddress = this.addressDAO.getDefaultAddress(user.getId());

                    if (addressId != null && !addressId.isEmpty()) {
                        try {
                            selectedAddress = this.addressDAO.getAddressById(user.getId(), Long.parseLong(addressId));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    Map<Long, String> shopVouchers = new HashMap<>();
                    req.getParameterMap().forEach((key, value) -> {
                        if (key.startsWith("shopVoucher_")) {
                            long shopId = Long.parseLong(key.replace("shopVoucher_", ""));
                            shopVouchers.put(shopId, value[0]);
                        }
                    });

                    double totalDiscount = 0;
                    for (Map.Entry<Long, String> entry : shopVouchers.entrySet()) {
                        List<CartItem> CartItemsByShop = cartItemDAO.getCheckedCartItemsByShop(user.getId(),
                                entry.getKey());
                        double totalShop = CartItemsByShop.stream()
                                .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity())
                                .sum();
                        String code = entry.getValue();
                        if (code != null && !code.isEmpty()) {
                            Voucher voucher = this.voucherDAO.getVoucherByCode(code, true);
                            if (voucher == null || voucher.getShopID() != entry.getKey()) {
                                json.put("success", false);
                                json.put("message", "Mã giảm giá không tồn tại.");
                            } else if (voucher.getStatus().equalsIgnoreCase("EXPIRED")) {
                                json.put("success", false);
                                json.put("message", "Mã giảm giá đã hết hạn.");
                            } else if (voucher.getStatus().equalsIgnoreCase("UPCOMING")) {
                                json.put("success", false);
                                json.put("message", "Mã giảm giá chưa đến thời gian áp dụng.");
                            } else if (voucher.getUsageLimit() > 0
                                    && voucher.getUsageCount() >= voucher.getUsageLimit()) {
                                json.put("success", false);
                                json.put("message", "Mã giảm giá đã đạt giới hạn sử dụng.");
                            } else {
                                if (totalShop < voucher.getMinOrderAmount()) {
                                    json.put("success", false);
                                    json.put("message", "Đơn hàng chưa đạt mức tối thiểu để dùng mã này.");
                                } else {
                                    double discountValue = 0;
                                    if (voucher.getDiscountType().equalsIgnoreCase("PERCENT")) {
                                        discountValue = totalShop * voucher.getValue() / 100;
                                        if (discountValue > voucher.getMaxAmount()) {
                                            discountValue = voucher.getMaxAmount();
                                        }
                                    } else {
                                        discountValue = voucher.getValue();
                                    }
                                    totalDiscount += discountValue;
                                }
                            }
                        }
                    }

                    List<CartItem> cartItems = cartItemDAO.getCheckedCartItems(user.getId());
                    double totalProduct = cartItems.stream()
                            .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity()).sum();

                    if (systemVoucherDiscountCode != null && !systemVoucherDiscountCode.isEmpty()) {
                        Voucher systemVoucher = this.voucherDAO.getVoucherByCode(systemVoucherDiscountCode, false);
                        if (systemVoucher == null) {
                            json.put("success", false);
                            json.put("message", "Mã giảm giá không tồn tại.");
                        } else if (systemVoucher.getStatus().equalsIgnoreCase("EXPIRED")) {
                            json.put("success", false);
                            json.put("message", "Mã giảm giá đã hết hạn.");
                        } else if (systemVoucher.getStatus().equalsIgnoreCase("UPCOMING")) {
                            json.put("success", false);
                            json.put("message", "Mã giảm giá chưa đến thời gian áp dụng.");
                        } else if (systemVoucher.getUsageLimit() > 0
                                && systemVoucher.getUsageCount() >= systemVoucher.getUsageLimit()) {
                            json.put("success", false);
                            json.put("message", "Mã giảm giá đã đạt giới hạn sử dụng.");
                        } else {
                            if (totalProduct < systemVoucher.getMinOrderAmount()) {
                                json.put("success", false);
                                json.put("message", "Đơn hàng chưa đạt mức tối thiểu để dùng mã này.");
                            } else {
                                double discountValueSystem = 0;
                                if (systemVoucher.getDiscountType().equalsIgnoreCase("PERCENT")) {
                                    discountValueSystem = totalProduct * systemVoucher.getValue() / 100;
                                    if (discountValueSystem > systemVoucher.getMaxAmount()) {
                                        discountValueSystem = systemVoucher.getMaxAmount();
                                    }
                                } else {
                                    discountValueSystem = systemVoucher.getValue();
                                }
                                totalDiscount += discountValueSystem;
                            }
                        }

                    }
                    double shipDiscount = 0;
                    if (systemVoucherShipCode != null && !systemVoucherShipCode.isEmpty()) {
                        Voucher systemVoucherShip = voucherDAO.getVoucherByCode(systemVoucherShipCode, false);
                        if (systemVoucherShip == null) {
                            json.put("success", false);
                            json.put("message", "Mã giảm giá không tồn tại.");
                        } else if (systemVoucherShip.getStatus().equalsIgnoreCase("EXPIRED")) {
                            json.put("success", false);
                            json.put("message", "Mã giảm giá đã hết hạn.");
                        } else if (systemVoucherShip.getStatus().equalsIgnoreCase("UPCOMING")) {
                            json.put("success", false);
                            json.put("message", "Mã giảm giá chưa đến thời gian áp dụng.");
                        } else if (systemVoucherShip.getUsageLimit() > 0
                                && systemVoucherShip.getUsageCount() >= systemVoucherShip.getUsageLimit()) {
                            json.put("success", false);
                            json.put("message", "Mã giảm giá đã đạt giới hạn sử dụng.");
                        } else {
                            if (totalProduct < systemVoucherShip.getMinOrderAmount()) {
                                json.put("success", false);
                                json.put("message", "Đơn hàng chưa đạt mức tối thiểu để dùng mã này.");
                            } else {
                                shipDiscount = systemVoucherShip.getValue();
                            }
                        }
                    }

                    Map<Long, List<CartItem>> grouped = cartItems.stream()
                            .collect(Collectors.groupingBy(ci -> ci.getProduct().getShop().getShopId()));

                    double totalShippingFee = 0;
                    if (selectedAddress != null) {
                        for (Map.Entry<Long, List<CartItem>> entry : grouped.entrySet()) {
                            List<CartItem> items = entry.getValue();
                            // Shop shop = items.get(0).getProduct().getShop();
                            double shopWeight = items.stream()
                                    .mapToDouble(ci -> ci.getProduct().getWeight() * ci.getQuantity())
                                    .sum();
                            JSONArray jsonItems = new JSONArray();
                            for (CartItem ci : items) {
                                JSONObject item = new JSONObject();
                                item.put("name", ci.getProduct().getTitle());
                                item.put("quantity", ci.getQuantity());
                                item.put("weight", ci.getProduct().getWeight() * ci.getQuantity());
                                jsonItems.put(item);
                            }
                            double fee = this.ghnShippingService.calculateFee(
                                    1454,
                                    "21211",
                                    1452,
                                    "21012", shopWeight,
                                    jsonItems,
                                    53320, null);

                            totalShippingFee += fee;
                        }
                    }

                    double finalAmount = totalProduct + totalShippingFee - shipDiscount - totalDiscount;
                    System.out.println("Check " + totalProduct + " " + totalShippingFee + " " + totalDiscount + " "
                            + shipDiscount + " " + finalAmount);

                    json.put("success", true);
                    json.put("totalProduct", totalProduct);
                    json.put("totalShippingFee", totalShippingFee);
                    json.put("totalDiscount", totalDiscount);
                    json.put("shipDiscount", shipDiscount);
                    json.put("finalAmount", finalAmount);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    json.put("success", false);
                }
                out.print(json.toString());
                break;
            }
            case "/voucher/shop": {
                try {
                    String code = req.getParameter("code");
                    long shopId = Long.parseLong(req.getParameter("shopId"));
                    Voucher voucher = this.voucherDAO.getVoucherByCode(code, true);
                    if (voucher == null || voucher.getShopID() != shopId) {
                        json.put("success", false);
                        json.put("message", "Mã giảm giá không tồn tại.");
                    } else if (voucher.getStatus().equalsIgnoreCase("EXPIRED")) {
                        json.put("success", false);
                        json.put("message", "Mã giảm giá đã hết hạn.");
                    } else if (voucher.getStatus().equalsIgnoreCase("UPCOMING")) {
                        json.put("success", false);
                        json.put("message", "Mã giảm giá chưa đến thời gian áp dụng.");
                    } else if (voucher.getUsageLimit() > 0 && voucher.getUsageCount() >= voucher.getUsageLimit()) {
                        json.put("success", false);
                        json.put("message", "Mã giảm giá đã đạt giới hạn sử dụng.");
                    } else {
                        List<CartItem> cartItems = cartItemDAO.getCheckedCartItemsByShop(user.getId(), shopId);
                        double totalShop = cartItems.stream()
                                .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity())
                                .sum();
                        if (totalShop < voucher.getMinOrderAmount()) {
                            json.put("success", false);
                            json.put("message", "Đơn hàng chưa đạt mức tối thiểu để dùng mã này.");
                        } else {
                            double discountValue = 0;
                            if (voucher.getDiscountType().equalsIgnoreCase("PERCENT")) {
                                discountValue = totalShop * voucher.getValue() / 100;
                                if (discountValue > voucher.getMaxAmount()) {
                                    discountValue = voucher.getMaxAmount();
                                }
                            } else {
                                discountValue = voucher.getValue();
                            }
                            json.put("success", true);
                            json.put("discountValue", discountValue);
                        }
                    }
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
                    } else if (voucher.getStatus().equalsIgnoreCase("EXPIRED")) {
                        json.put("success", false);
                        json.put("message", "Mã giảm giá đã hết hạn.");
                    } else if (voucher.getStatus().equalsIgnoreCase("UPCOMING")) {
                        json.put("success", false);
                        json.put("message", "Mã giảm giá chưa đến thời gian áp dụng.");
                    } else if (voucher.getUsageLimit() > 0 && voucher.getUsageCount() >= voucher.getUsageLimit()) {
                        json.put("success", false);
                        json.put("message", "Mã giảm giá đã đạt giới hạn sử dụng.");
                    } else {
                        List<CartItem> cartItems = cartItemDAO.getCheckedCartItems(user.getId());
                        double totalOrder = cartItems.stream()
                                .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity())
                                .sum();

                        if (totalOrder < voucher.getMinOrderAmount()) {
                            json.put("success", false);
                            json.put("message", "Đơn hàng chưa đạt mức tối thiểu để dùng mã này.");
                        } else {
                            double discountValue = 0;
                            double shipValue = 0;
                            if (voucher.getDiscountType().equalsIgnoreCase("PERCENT")) {
                                discountValue = totalOrder * voucher.getValue() / 100;
                                if (discountValue > voucher.getMaxAmount()) {
                                    discountValue = voucher.getMaxAmount();
                                }
                            } else if (voucher.getDiscountType().equalsIgnoreCase("SHIPPING")) {
                                shipValue = voucher.getValue();
                            } else {
                                discountValue = voucher.getValue();
                            }
                            json.put("success", true);
                            json.put("type", voucher.getDiscountType());
                            json.put("discountValue", discountValue);
                            json.put("shipValue", shipValue);
                        }
                    }
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
