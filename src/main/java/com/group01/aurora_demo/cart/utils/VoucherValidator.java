package com.group01.aurora_demo.cart.utils;

import java.util.Date;

import com.group01.aurora_demo.shop.model.Voucher;

public class VoucherValidator {

    /**
<<<<<<< HEAD
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
=======
     * Kiểm tra điều kiện hợp lệ của voucher
     * 
     * @param voucher:    đối tượng voucher cần kiểm tra
     * @param totalOrder: tổng giá trị đơn hàng áp dụng voucher
     * @param shopId:     ID cửa hàng (nếu là voucher shop); null nếu là voucher hệ
     *                    thống
     * @return null nếu hợp lệ, hoặc chuỗi message lỗi nếu không hợp lệ
     */
    public String validate(Voucher voucher, double totalOrder, Long shopId) {
        Date now = new Date();
        if (voucher == null)
            return "Mã giảm giá không tồn tại.";

        // Kiểm tra shopId hợp lệ
        if (voucher.getShopID() != null && shopId != null && !voucher.getShopID().equals(shopId))
            return "Mã giảm giá không áp dụng cho cửa hàng này.";

        // Kiểm tra thời gian áp dụng
        if (voucher.getStartAt() != null && voucher.getStartAt().after(now))
            return "Mã giảm giá chưa đến thời gian áp dụng.";

        if (voucher.getEndAt() != null && voucher.getEndAt().before(now))
            return "Mã giảm giá đã hết hạn ";

        // Kiểm tra trạng thái
        String status = voucher.getStatus();
        if (status != null) {
            switch (status.toUpperCase()) {
                case "EXPIRED":
                    return "Mã giảm giá đã hết hạn.";
                case "UPCOMING":
                    return "Mã giảm giá chưa đến thời gian áp dụng.";
            }
        }

        // Kiểm tra giới hạn sử dụng
        if (voucher.getUsageLimit() > 0 && voucher.getUsageCount() >= voucher.getUsageLimit())
            return "Mã giảm giá đã đạt giới hạn sử dụng.";

        // Kiểm tra giá trị đơn hàng tối thiểu
        if (voucher.getMinOrderAmount() > 0 && totalOrder < voucher.getMinOrderAmount())
            return "Đơn hàng chưa đạt mức tối thiểu để dùng mã này";

        // Nếu qua hết điều kiện → hợp lệ
        return null;
>>>>>>> 6a13786814f123593cf52f52fe60d13c593aa470
    }

    /**
     * Tính toán giá trị giảm giá áp dụng cho đơn hàng.
     * 
     * @param voucher     đối tượng voucher hợp lệ
     * @param totalAmount tổng tiền của shop hoặc toàn đơn hàng
     * @return số tiền được giảm
     */
<<<<<<< HEAD
    public static double calculateDiscountValue(Voucher voucher, double totalAmount) {
=======
    public double calculateDiscount(Voucher voucher, double totalAmount) {
>>>>>>> 6a13786814f123593cf52f52fe60d13c593aa470
        if (voucher == null)
            return 0;

        double discount = 0;

<<<<<<< HEAD
        if ("PERCENT".equalsIgnoreCase(voucher.getDiscountType())) {
=======
        if (voucher.getDiscountType().equalsIgnoreCase("PERCENT")) {
>>>>>>> 6a13786814f123593cf52f52fe60d13c593aa470
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
