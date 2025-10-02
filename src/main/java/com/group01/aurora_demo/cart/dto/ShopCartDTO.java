package com.group01.aurora_demo.cart.dto;

import java.util.List;

import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.cart.model.Shop;
import com.group01.aurora_demo.cart.model.Voucher;

public class ShopCartDTO {
    private Shop shop;
    private List<CartItem> items;
    private List<Voucher> vouchers;

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public List<Voucher> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<Voucher> vouchers) {
        this.vouchers = vouchers;
    }

}
