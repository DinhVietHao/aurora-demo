package com.group01.aurora_demo.cart.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dao.dto.CheckoutSummaryDTO;
import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.cart.utils.VoucherValidator;
import com.group01.aurora_demo.profile.dao.AddressDAO;
import com.group01.aurora_demo.profile.model.Address;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
import com.group01.aurora_demo.shop.model.Shop;
import com.group01.aurora_demo.shop.model.Voucher;

public class CheckoutService {
    private CartItemDAO cartItemDAO;
    private VoucherDAO voucherDAO;
    private AddressDAO addressDAO;
    private GHNShippingService ghnShippingService;
    private VoucherValidator voucherValidator;

    public CheckoutService() {
        this.cartItemDAO = new CartItemDAO();
        this.voucherDAO = new VoucherDAO();
        this.addressDAO = new AddressDAO();
        this.ghnShippingService = new GHNShippingService();
        this.voucherValidator = new VoucherValidator();
    }

    public CheckoutSummaryDTO calculateCheckoutSummary(
            long userId,
            Long addressId,
            String systemVoucherDiscount,
            String systemVoucherShip,
            Map<Long, String> shopVouchers) {

        List<CartItem> cartItems = cartItemDAO.getCheckedCartItems(userId);
        double totalProduct = cartItems.stream()
                .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity())
                .sum();

        double totalDiscount = 0;
        double shipDiscount = 0;
        double totalShippingFee = 0;

        for (Map.Entry<Long, String> entry : shopVouchers.entrySet()) {
            String code = entry.getValue();
            if (code != null && !code.isEmpty()) {
                Voucher voucher = voucherDAO.getVoucherByCode(code, true);
                if (voucher != null) {
                    List<CartItem> shopItems = cartItemDAO.getCheckedCartItemsByShop(userId, entry.getKey());
                    double shopTotal = shopItems.stream()
                            .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity())
                            .sum();

                    String validation = voucherValidator.validate(voucher, shopTotal, entry.getKey());
                    if (validation == null)
                        totalDiscount += voucherValidator.calculateDiscount(voucher, shopTotal);
                }
            }
        }

        if (systemVoucherDiscount != null && !systemVoucherDiscount.isEmpty()) {
            Voucher voucher = voucherDAO.getVoucherByCode(systemVoucherDiscount, false);
            if (voucher != null) {
                String validation = voucherValidator.validate(voucher, totalProduct, null);
                if (validation == null)
                    totalDiscount += voucherValidator.calculateDiscount(voucher, totalProduct);
            }
        }

        if (systemVoucherShip != null && !systemVoucherShip.isEmpty()) {
            Voucher voucher = voucherDAO.getVoucherByCode(systemVoucherShip, false);
            if (voucher != null && voucher.getDiscountType().equalsIgnoreCase("SHIPPING"))
                shipDiscount = voucher.getValue();
        }

        if (addressId != null) {
            Address address = addressDAO.getAddressById(userId, addressId);
            if (address != null) {
                totalShippingFee = calculateShippingFee(cartItems, address);
            }
        }

        double finalAmount = totalProduct + totalShippingFee - totalDiscount - shipDiscount;

        return new CheckoutSummaryDTO(totalProduct, totalDiscount, totalShippingFee, shipDiscount, finalAmount);
    }

    public double calculateShippingFee(List<CartItem> cartItems, Address address) {
        double totalShipping = 0;

        Map<Long, List<CartItem>> grouped = cartItems.stream()
                .collect(Collectors.groupingBy(ci -> ci.getProduct().getShop().getShopId()));

        for (Map.Entry<Long, List<CartItem>> entry : grouped.entrySet()) {
            List<CartItem> items = entry.getValue();
            Shop shop = items.get(0).getProduct().getShop();

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

            double fee = ghnShippingService.calculateFee(
                    1454, "21211", 1452, "21012", shopWeight, jsonItems, 53320, null);

            totalShipping += fee;
        }

        return totalShipping;
    }
}
