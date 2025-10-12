package com.group01.aurora_demo.cart.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.ProductImages;
import com.group01.aurora_demo.catalog.model.BookDetail;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.cart.model.CartItem;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng CartItems.
 * Cung cấp các phương thức CRUD và các thao tác liên quan đến giỏ hàng chi
 * tiết.
 * 
 * @author Lê Minh Kha
 */
public class CartItemDAO {

    /**
     * Thêm một CartItem mới vào giỏ hàng.
     * 
     * @param cartItem thông tin giỏ hàng chi tiết
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addCartItem(CartItem cartItem) {
        String sql = "INSERT INTO CartItems (CartID, ProductID, Quantity, UnitPrice, Subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, cartItem.getCartId());
            ps.setLong(2, cartItem.getProductId());
            ps.setInt(3, cartItem.getQuantity());
            ps.setDouble(4, cartItem.getUnitPrice());
            ps.setDouble(5, cartItem.getSubtotal());

            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Xóa một CartItem khỏi giỏ hàng.
     * 
     * @param cartItemId id của cart item
     * @return true nếu xóa thành công
     */
    public boolean deleteCartItem(long cartItemId) {
        String sql = "DELETE FROM CartItems WHERE CartItemID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, cartItemId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Lấy một CartItem dựa theo cartId và productId (giúp kiểm tra trùng sản phẩm).
     * 
     * @param cartId    id giỏ hàng
     * @param productId id sản phẩm
     * @return CartItem nếu tồn tại, null nếu không có
     */
    public CartItem getCartItem(long cartId, long productId) {
        String sql = "SELECT * FROM CartItems WHERE CartID = ? AND ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, cartId);
            ps.setLong(2, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setCartItemId(rs.getLong("CartItemID"));
                cartItem.setCartId(rs.getLong("CartID"));
                cartItem.setProductId(rs.getLong("ProductID"));
                cartItem.setQuantity(rs.getInt("Quantity"));
                cartItem.setUnitPrice(rs.getDouble("UnitPrice"));
                return cartItem;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Lấy CartItem theo ID.
     * 
     * @param cartItemId id của cart item
     * @return CartItem nếu tìm thấy
     */
    public CartItem getCartItemById(long cartItemId) {
        String sql = "SELECT * FROM CartItems WHERE CartItemID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, cartItemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setCartItemId(rs.getLong("CartItemID"));
                cartItem.setCartId(rs.getLong("CartID"));
                cartItem.setProductId(rs.getLong("ProductID"));
                cartItem.setQuantity(rs.getInt("Quantity"));
                cartItem.setUnitPrice(rs.getDouble("UnitPrice"));
                cartItem.setSubtotal(rs.getDouble("Subtotal"));
                return cartItem;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Cập nhật số lượng và subtotal của CartItem.
     * 
     * @param cartItem cart item cần cập nhật
     * @return true nếu thành công
     */
    public boolean updateQuantity(CartItem cartItem) {
        String sql = "UPDATE CartItems SET Quantity = ?, Subtotal = ? WHERE CartItemID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, cartItem.getQuantity());
            ps.setDouble(2, cartItem.getSubtotal());
            ps.setLong(3, cartItem.getCartItemId());
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật trạng thái chọn/bỏ chọn của một cart item.
     */
    public boolean updateIsChecked(long cartItemId, boolean isChecked) {
        String sql = "UPDATE CartItems SET IsChecked = ? WHERE CartItemID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setBoolean(1, isChecked);
            ps.setLong(2, cartItemId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật trạng thái chọn/bỏ chọn toàn bộ cart items theo cartId.
     */
    public boolean updateAllChecked(long cartId, boolean checked) {
        String sql = "UPDATE CartItems SET IsChecked = ? WHERE CartID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setBoolean(1, checked);
            ps.setLong(2, cartId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Lấy giá cuối cùng của sản phẩm dựa vào ProductID (Price - Discount).
     * 
     * @param productId id sản phẩm
     * @return giá sau khi giảm
     */
    public double getUnitPriceByProductId(long productId) {
        String sql = "SELECT Price, Discount FROM Products WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double price = rs.getDouble("Price");
                double discount = rs.getDouble("Discount");
                double finalPrice = price - discount; // áp dụng giảm giá
                return finalPrice;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0.0;
    }

    /**
     * Lấy danh sách CartItems của một user.
     * Join nhiều bảng để lấy thêm thông tin sản phẩm, hình ảnh, tác giả.
     * 
     * @param userId id của user
     * @return danh sách cart items
     */
    public List<CartItem> getCartItemsByUserId(long userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = """
                 SELECT
                    ci.CartItemID,
                    ci.CartID,
                    ci.Quantity,
                    ci.UnitPrice,
                    ci.Subtotal,
                    ci.IsChecked,
                    p.ProductID,
                    p.Title,
                    p.OriginalPrice,
                    p.SalePrice,
                    pi.Url AS ImageUrl,
                    a.AuthorName AS Author
                FROM CartItems ci
                JOIN Carts c ON ci.CartID = c.CartID
                JOIN Products p ON ci.ProductID = p.ProductID
                LEFT JOIN ProductImages pi ON p.ProductID = pi.ProductID AND pi.IsPrimary = 1
                LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID
                LEFT JOIN Authors a ON a.AuthorID = ba.AuthorID
                WHERE c.UserID = ?
                """;
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setCartItemId(rs.getLong("CartItemID"));
                cartItem.setCartId(rs.getLong("CartID"));
                cartItem.setQuantity(rs.getInt("Quantity"));
                cartItem.setUnitPrice(rs.getDouble("UnitPrice"));
                cartItem.setSubtotal(rs.getDouble("Subtotal"));
                cartItem.setIsChecked(rs.getBoolean("IsChecked"));

                // Mapping thông tin sản phẩm
                Product product = new Product();
                product.setProductId(rs.getLong("ProductID"));
                product.setTitle(rs.getString("Title"));
                 product.setOriginalPrice(rs.getDouble("OriginalPrice"));
                product.setSalePrice(rs.getDouble("SalePrice"));

                // Lấy ảnh chính của sản phẩm
                ProductImages productImages = new ProductImages();
                productImages.setImageUrl(rs.getString("ImageUrl"));
                product.setImages(List.of(productImages));

                // Lấy thông tin chi tiết sách (nếu có)
                BookDetail bookDetail = new BookDetail();
                bookDetail.setAuthor(rs.getString("Author"));
                product.setBookDetail(bookDetail);

                cartItem.setProduct(product);
                cartItems.add(cartItem);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return cartItems;
    }

    /**
     * Lấy tổng số lượng items (sản phẩm) trong giỏ hàng.
     * 
     * @param cartId id giỏ hàng
     * @return số lượng items
     */
    public int getDistinctItemCount(long cartId) {
        String sql = "SELECT COUNT(ProductID) AS ProductCount FROM CartItems WHERE CartID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, cartId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ProductCount");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static void main(String[] args) {
        // Test cập nhật số lượng cart item
        CartItemDAO c = new CartItemDAO();
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(1);
        cartItem.setCartId(1);
        cartItem.setProductId(1);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(95000);
        cartItem.setSubtotal(190000);
        boolean test = c.updateQuantity(cartItem);
        System.out.println(test);
    }
}