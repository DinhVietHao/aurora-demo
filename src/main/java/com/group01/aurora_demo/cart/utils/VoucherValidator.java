package com.group01.aurora_demo.cart.utils;

import java.util.Date;

import com.group01.aurora_demo.shop.model.Voucher;

public class VoucherValidator {

    /**
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
    }

    /**
     * Tính toán giá trị giảm giá áp dụng cho đơn hàng.
     * 
     * @param voucher     đối tượng voucher hợp lệ
     * @param totalAmount tổng tiền của shop hoặc toàn đơn hàng
     * @return số tiền được giảm
     */
    public double calculateDiscount(Voucher voucher, double totalAmount) {
        if (voucher == null)
            return 0;

        double discount = 0;

        if (voucher.getDiscountType().equalsIgnoreCase("PERCENT")) {
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
