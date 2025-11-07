package com.group01.aurora_demo.cart.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    private static final Locale VIETNAM = new Locale("vi", "VN");

    /**
     * Định dạng số tiền theo kiểu Việt Nam (VD: 1.000 ₫)
     * 
     * @param amount giá trị tiền
     * @return chuỗi đã định dạng
     */
    public static String format(double amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(VIETNAM);
        return currencyFormatter.format(amount);
    }
}
