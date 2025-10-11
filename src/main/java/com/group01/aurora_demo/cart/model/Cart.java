package com.group01.aurora_demo.cart.model;

/**
 * Model đại diện cho giỏ hàng (Cart).
 * Mỗi giỏ hàng gắn với một user duy nhất thông qua userId.
 * 
 * @author Lê Minh Kha
 */
public class Cart {
    private long cartId; // ID giỏ hàng (primary key trong DB)
    private long userId; // ID người dùng sở hữu giỏ hàng

    /**
     * Constructor mặc định.
     */
    public Cart() {
    }

    public long getCartId() {
        return cartId;
    }

    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Trả về chuỗi mô tả giỏ hàng (hữu ích khi debug/log).
     */
    @Override
    public String toString() {
        return "Cart{" + "cartId=" + cartId + ", userId=" + userId + '}';
    }
}