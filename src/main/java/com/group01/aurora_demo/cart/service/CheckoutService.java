package com.group01.aurora_demo.cart.service;

import java.util.HashMap;
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
    private GHNService ghnService;
    private VoucherValidator voucherValidator;

    public CheckoutService() {
        this.cartItemDAO = new CartItemDAO();
        this.voucherDAO = new VoucherDAO();
        this.addressDAO = new AddressDAO();
        this.ghnService = new GHNService();
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

        double shopDiscount = 0;
        double systemDiscount = 0;
        double totalShippingFee = 0;
        double systemShippingDiscount = 0;

        Map<Long, List<CartItem>> itemsByShop = cartItems.stream()
                .collect(Collectors.groupingBy(ci -> ci.getProduct().getShop().getShopId()));

        for (Map.Entry<Long, String> entry : shopVouchers.entrySet()) {
            String code = entry.getValue();
            if (code != null && !code.isEmpty()) {
                Voucher voucher = voucherDAO.getVoucherByCode(entry.getKey(), code, true);
                if (voucher != null) {
                    List<CartItem> shopItems = itemsByShop.get(entry.getKey());
                    if (shopItems == null || shopItems.isEmpty())
                        continue;
                    double shopTotal = shopItems.stream()
                            .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity())
                            .sum();

                    String validation = voucherValidator.validate(voucher, shopTotal, entry.getKey(), userId);
                    if (validation == null) {
                        shopDiscount += voucherValidator.calculateDiscount(voucher, shopTotal);

                    }
                }
            }
        }

        double remainingAfterShopDiscount = Math.max(0, totalProduct - shopDiscount);
        if (systemVoucherDiscount != null && !systemVoucherDiscount.isEmpty()) {
            Voucher voucher = voucherDAO.getVoucherByCode(null, systemVoucherDiscount, false);
            if (voucher != null && !"SHIPPING".equalsIgnoreCase(voucher.getDiscountType())) {
                String validation = voucherValidator.validate(voucher, remainingAfterShopDiscount, null, userId);
                if (validation == null) {
                    systemDiscount = voucherValidator.calculateDiscount(voucher, remainingAfterShopDiscount);
                    if (voucher.getMaxAmount() > 0) {
                        systemDiscount = Math.min(systemDiscount, voucher.getMaxAmount());
                    }
                }

            }
        }
        if (addressId != null) {
            Address address = addressDAO.getAddressById(userId, addressId);
            if (address != null) {
                totalShippingFee = calculateShippingFee(cartItems, address);
            }
        }

        if (systemVoucherShip != null && !systemVoucherShip.isEmpty()) {
            Voucher voucher = voucherDAO.getVoucherByCode(null, systemVoucherShip, false);
            if (voucher != null && voucher.getDiscountType().equalsIgnoreCase("SHIPPING")) {
                String validation = voucherValidator.validate(voucher, totalProduct, null, userId);
                if (validation == null) {
                    systemShippingDiscount = Math.min(voucher.getValue(), totalShippingFee);

                }
            }

        }
        double finalAmount = Math.max(0,
                totalProduct + totalShippingFee - shopDiscount - systemDiscount - systemShippingDiscount);

        return new CheckoutSummaryDTO(totalProduct, shopDiscount, systemDiscount, totalShippingFee,
                systemShippingDiscount,
                finalAmount);
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

            double fee = this.ghnService.calculateFee(
                    shop.getPickupAddress().getDistrictId(), shop.getPickupAddress().getWardCode(),
                    address.getDistrictId(), address.getWardCode(), shopWeight, jsonItems, null, null);
            totalShipping += fee;
        }

        return totalShipping;
    }

    public Map<Long, Double> calculateShippingFeePerShop(List<CartItem> cartItems, Address address) {
        Map<Long, Double> shopShippingFees = new HashMap<>();

        Map<Long, List<CartItem>> grouped = cartItems.stream()
                .collect(Collectors.groupingBy(ci -> ci.getProduct().getShop().getShopId()));

        for (Map.Entry<Long, List<CartItem>> entry : grouped.entrySet()) {
            Long shopId = entry.getKey();
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

            double fee = this.ghnService.calculateFee(
                    shop.getPickupAddress().getDistrictId(), shop.getPickupAddress().getWardCode(),
                    address.getDistrictId(), address.getWardCode(), shopWeight, jsonItems, null, null);
            shopShippingFees.put(shopId, fee);
        }

        return shopShippingFees;
    }
}