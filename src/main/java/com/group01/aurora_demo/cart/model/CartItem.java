package com.group01.aurora_demo.cart.model;

import java.time.LocalDateTime;

import com.group01.aurora_demo.catalog.model.Product;

/**
 * Model đại diện cho một sản phẩm trong giỏ hàng (CartItem).
 * Mỗi CartItem gắn với một Cart (cartId) và một Product (productId).
 * 
 * @author Lê Minh Kha
 */
public class CartItem {

    private long cartItemId; // ID duy nhất của cart item
    private long UserId;
    private long productId; // ID sản phẩm trong cart item
    private int quantity; // Số lượng sản phẩm
    private double unitPrice; // Giá 1 đơn vị sản phẩm
    private double subtotal; // Thành tiền (quantity * unitPrice)
    private boolean isChecked; // Trạng thái đã chọn hay chưa (ví dụ: chọn để thanh toán)
    private Product product; // Thông tin chi tiết sản phẩm (object liên kết)
    private LocalDateTime createdAt;

    /**
     * Constructor mặc định.
     */
    public CartItem() {
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public boolean isIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CartItem [cartItemId=" + cartItemId + ", UserId=" + UserId + ", productId=" + productId + ", quantity="
                + quantity + ", unitPrice=" + unitPrice + ", subtotal=" + subtotal + ", isChecked=" + isChecked
                + ", product=" + product + "]";
    }

}