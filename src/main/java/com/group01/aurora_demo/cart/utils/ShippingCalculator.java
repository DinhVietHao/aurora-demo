package com.group01.aurora_demo.cart.utils;

import java.util.Arrays;

public class ShippingCalculator {
    public String getRegion(String province) {
        String[] north = {
                "Tuyên Quang", "Cao Bằng", "Lai Châu", "Lào Cai", "Thái Nguyên",
                "Điện Biên", "Lạng Sơn", "Sơn La", "Phú Thọ", "Bắc Ninh",
                "Quảng Ninh", "Thành phố Hà Nội", "Hải Phòng", "Hưng Yên", "Ninh Bình"
        };

        String[] central = {
                "Thanh Hóa", "Nghệ An", "Hà Tĩnh", "Quảng Trị", "Thừa Thiên Huế",
                "Đà Nẵng", "Quảng Ngãi", "Gia Lai", "Khánh Hòa", "Lâm Đồng"
        };

        String[] south = {
                "Tây Ninh", "Hồ Chí Minh", "Đồng Tháp", "An Giang",
                "Vĩnh Long", "Cần Thơ", "Hậu Giang", "Bạc Liêu", "Cà Mau"
        };

        if (Arrays.asList(north).contains(province))
            return "Bắc";
        if (Arrays.asList(central).contains(province))
            return "Trung";
        return "Nam";
    }

    public long calculateShippingFee(String fromProvince, String toProvince, double totalWeightKg) {
        String regionFrom = getRegion(fromProvince);
        String regionTo = getRegion(toProvince);
        double baseFee;
        double feePerHalfKg;

        boolean sameProvince = fromProvince.equalsIgnoreCase(toProvince);
        boolean sameRegion = regionFrom.equalsIgnoreCase(regionTo);

        if (sameProvince) {
            if (fromProvince.equalsIgnoreCase("Thành phố Hà Nội") || fromProvince.equalsIgnoreCase("Hồ Chí Minh")) {
                baseFee = 21000;
            } else {
                baseFee = 15500;
            }
            feePerHalfKg = 2500;
        } else if (sameRegion) {
            baseFee = 29000;
            feePerHalfKg = 2500;
        } else {
            baseFee = 29000;
            feePerHalfKg = 5000;
        }

        double extraWeight = Math.max(0, totalWeightKg - 0.5);
        long extraSteps = (long) Math.ceil(extraWeight / 0.5);
        long shippingFee = (long) (baseFee + extraSteps * feePerHalfKg);

        return shippingFee;
    }
}
