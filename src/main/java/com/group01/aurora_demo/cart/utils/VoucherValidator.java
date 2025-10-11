package com.group01.aurora_demo.cart.utils;

import java.util.Date;

import com.group01.aurora_demo.shop.model.Voucher;

public class VoucherValidator {

    /**
     * Kiểm tra tính hợp lệ của voucher
     * 
     * @param voucher        đối tượng voucher lấy từ DB
     * @param expectedShopId shopId mong đợi (null nếu voucher hệ thống)
     * @return true nếu hợp lệ
     */
    public static boolean isVoucherValid(Voucher voucher, Long expectedShopId) {
        if (voucher == null)
            return false;

        // --- Kiểm tra shop (voucher shop không thể dùng cho shop khác) ---
        if (expectedShopId != null && voucher.getShopID() != null
                && !voucher.getShopID().equals(expectedShopId)) {
            return false;
        }

        // --- Kiểm tra trạng thái cơ bản ---
        String status = voucher.getStatus() == null ? "" : voucher.getStatus().toUpperCase();
        if (status.equals("EXPIRED") || status.equals("UPCOMING")) {
            return false;
        }

        // --- Kiểm tra ngày bắt đầu & kết thúc ---
        Date now = new Date();
        if (voucher.getStartAt() != null && voucher.getStartAt().after(now)) {
            return false;
        }
        if (voucher.getEndAt() != null && voucher.getEndAt().before(now)) {
            return false;
        }

        // --- Kiểm tra số lượt dùng ---
        if (voucher.getUsageLimit() > 0 && voucher.getUsageCount() >= voucher.getUsageLimit()) {
            return false;
        }

        return true;
    }

    /**
     * Tính toán giá trị giảm giá áp dụng cho đơn hàng.
     * 
     * @param voucher     đối tượng voucher hợp lệ
     * @param totalAmount tổng tiền của shop hoặc toàn đơn hàng
     * @return số tiền được giảm
     */
    public static double calculateDiscountValue(Voucher voucher, double totalAmount) {
        if (voucher == null)
            return 0;

        double discount = 0;

        if ("PERCENT".equalsIgnoreCase(voucher.getDiscountType())) {
            discount = totalAmount * voucher.getValue() / 100;
            if (voucher.getMaxAmount() > 0 && discount > voucher.getMaxAmount()) {
                discount = voucher.getMaxAmount();
            }
        } else {
            discount = voucher.getValue();
        }

        // Không cho phép giảm nhiều hơn giá trị đơn hàng
        return Math.min(discount, totalAmount);
    }
}
