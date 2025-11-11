package com.group01.aurora_demo.cart.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.cart.model.Cart;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Data Access Object (DAO) cho bảng Carts.
 * Chứa các phương thức thao tác với dữ liệu giỏ hàng trong database.
 * 
 * @author Lê Minh Kha
 */
public class CartDAO {

    /**
     * Lấy giỏ hàng theo userId.
     * 
     * @param userId id của user cần tìm giỏ hàng
     * @return Cart nếu tồn tại, null nếu không có
     */
    public Cart getCartByUserId(long userId) {
        String sql = "SELECT * FROM Carts WHERE UserID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { // Nếu user đã có giỏ hàng
                Cart cart = new Cart();
                cart.setCartId(rs.getLong("CartID"));
                cart.setUserId(rs.getLong("UserID"));
                return cart;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage()); // log lỗi
        }
        return null;
    }

    /**
     * Tạo giỏ hàng mới cho user.
     * 
     * @param userId id của user
     * @return Cart vừa tạo, null nếu thất bại
     */
    public Cart createCart(long userId) {
        String sql = "INSERT INTO Carts (UserID) VALUES (?)";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            // Statement.RETURN_GENERATED_KEYS: lấy CartID vừa insert
            PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) { // Lấy CartID được DB tự sinh
                Cart cart = new Cart();
                cart.setCartId(rs.getLong(1));
                cart.setUserId(userId);
                return cart;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Xoá giỏ hàng theo cartId.
     * 
     * @param cartId id của giỏ hàng cần xoá
     * @return true nếu xoá thành công, false nếu thất bại
     */
    public boolean deleteCart(long cartId) {
        String sql = "DELETE FROM Carts WHERE CartID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, cartId);

            int result = ps.executeUpdate();
            return result > 0; // Trả về true nếu có ít nhất 1 dòng bị xoá

        } catch (Exception e) {
            // Có thể bổ sung log chi tiết hơn
        }
        return false;
    }

    public static void main(String[] args) {
        // Test thử chức năng tạo giỏ hàng
        CartDAO c = new CartDAO();
        Cart cart = c.createCart(2);
        System.out.println(cart);
    }

}