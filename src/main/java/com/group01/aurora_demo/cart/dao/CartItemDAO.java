package com.group01.aurora_demo.cart.dao;

import com.group01.aurora_demo.shop.model.Shop;
import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.catalog.model.ProductImage;
import com.group01.aurora_demo.catalog.dao.AuthorDAO;
import com.group01.aurora_demo.catalog.model.Author;
import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.profile.dao.AddressDAO;
import com.group01.aurora_demo.profile.model.Address;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng CartItems.
 * Cung cấp các phương thức CRUD và các thao tác liên quan đến giỏ hàng chi
 * tiết.
 * 
 * @author Lê Minh Kha
 */
public class CartItemDAO {
    private AddressDAO addressDAO;
    private AuthorDAO authorDAO;

    public CartItemDAO() {
        this.addressDAO = new AddressDAO();
        this.authorDAO = new AuthorDAO();
    }

    /**
     * Thêm một CartItem mới vào giỏ hàng.
     * 
     * @param cartItem thông tin giỏ hàng chi tiết
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addCartItem(CartItem cartItem) {
        String sql = "INSERT INTO CartItems (UserID, ProductID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, cartItem.getUserId());
            ps.setLong(2, cartItem.getProductId());
            ps.setInt(3, cartItem.getQuantity());
            ps.setDouble(4, cartItem.getUnitPrice());

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
    public CartItem getCartItem(long userId, long productId) {
        String sql = "SELECT * FROM CartItems WHERE UserID = ? AND ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setCartItemId(rs.getLong("CartItemID"));
                cartItem.setUserId(rs.getLong("UserID"));
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
                cartItem.setUserId(rs.getLong("UserID"));
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
        String sql = "UPDATE CartItems SET Quantity = ?, CreatedAt = SYSUTCDATETIME() WHERE CartItemID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, cartItem.getQuantity());
            ps.setLong(2, cartItem.getCartItemId());
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
    public boolean updateAllChecked(long UserId, boolean checked) {
        String sql = "UPDATE CartItems SET IsChecked = ? WHERE UserID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setBoolean(1, checked);
            ps.setLong(2, UserId);
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
        String sql = "SELECT SalePrice FROM Products WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("SalePrice");
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
                     ci.UserID,
                     ci.ProductID,
                     ci.Quantity,
                     ci.UnitPrice,
                     ci.Subtotal,
                     ci.IsChecked,
                     p.Title ,
                     p.OriginalPrice,
                     p.SalePrice,
                	 s.ShopID,
                     s.Name AS ShopName,
                     img.Url AS ImageUrl
                FROM CartItems ci
                JOIN Products p ON ci.ProductID = p.ProductID
                JOIN Shops s ON p.ShopID = s.ShopID
                LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID
                WHERE ci.UserID = ? ORDER BY ci.CreatedAt DESC
                """;
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setCartItemId(rs.getLong("CartItemID"));
                cartItem.setUserId(rs.getLong("UserID"));
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

                Shop shop = new Shop();
                shop.setShopId(rs.getLong("ShopID"));
                shop.setName(rs.getString("ShopName"));
                product.setShop(shop);

                // Lấy ảnh chính của sản phẩm
                ProductImage productImages = new ProductImage();
                productImages.setUrl(rs.getString("ImageUrl"));
                product.setImages(List.of(productImages));

                List<Author> authors = authorDAO.getAuthorsByProductId(rs.getLong("ProductID"));
                product.setAuthors(authors);

                cartItem.setProduct(product);
                cartItems.add(cartItem);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return cartItems;
    }

    public List<CartItem> getCheckedCartItems(long userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = """
                SELECT
                     ci.CartItemID,
                     ci.UserID,
                     ci.ProductID,
                     ci.Quantity,
                     ci.UnitPrice,
                     ci.Subtotal,
                     ci.IsChecked,
                     p.Title ,
                     p.OriginalPrice,
                     p.SalePrice,
                     p.Weight,
                	 s.ShopID,
                     s.Name AS ShopName,
                     img.Url AS ImageUrl
                FROM CartItems ci
                JOIN Products p ON ci.ProductID = p.ProductID
                JOIN Shops s ON p.ShopID = s.ShopID
                LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID
                WHERE ci.UserID = ? AND ci.IsChecked = 1 ORDER BY ci.CreatedAt DESC
                """;
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setCartItemId(rs.getLong("CartItemID"));
                cartItem.setUserId(rs.getLong("UserID"));
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
                product.setWeight(rs.getDouble("Weight"));

                Shop shop = new Shop();
                shop.setShopId(rs.getLong("ShopID"));
                shop.setName(rs.getString("ShopName"));

                Address shopAddress = this.addressDAO.getAddressByShopId(rs.getLong("ShopID"));
                shop.setPickupAddress(shopAddress);
                product.setShop(shop);

                // Lấy ảnh chính của sản phẩm
                ProductImage productImages = new ProductImage();
                productImages.setUrl(rs.getString("ImageUrl"));
                product.setImages(List.of(productImages));

                List<Author> authors = authorDAO.getAuthorsByProductId(rs.getLong("ProductID"));
                product.setAuthors(authors);

                cartItem.setProduct(product);
                cartItems.add(cartItem);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return cartItems;
    }

    public List<CartItem> getCheckedCartItemsByShop(long userId, long shopId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = """
                SELECT
                     ci.CartItemID,
                     ci.UserID,
                     ci.ProductID,
                     ci.Quantity,
                     ci.UnitPrice,
                     ci.Subtotal,
                     ci.IsChecked,
                     p.Title ,
                     p.OriginalPrice,
                     p.SalePrice,
                     p.Weight,
                	 s.ShopID,
                     s.Name AS ShopName,
                     img.Url AS ImageUrl
                FROM CartItems ci
                JOIN Products p ON ci.ProductID = p.ProductID
                JOIN Shops s ON p.ShopID = s.ShopID
                LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                WHERE ci.UserID = ? AND s.ShopID = ? AND ci.IsChecked = 1 ORDER BY ci.CreatedAt DESC
                """;
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, shopId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setCartItemId(rs.getLong("CartItemID"));
                cartItem.setUserId(rs.getLong("UserID"));
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
                product.setWeight(rs.getDouble("Weight"));

                Shop shop = new Shop();
                shop.setShopId(rs.getLong("ShopID"));
                shop.setName(rs.getString("ShopName"));

                Address shopAddress = this.addressDAO.getAddressByShopId(rs.getLong("ShopID"));
                shop.setPickupAddress(shopAddress);
                product.setShop(shop);

                // Lấy ảnh chính của sản phẩm
                ProductImage productImages = new ProductImage();
                productImages.setUrl(rs.getString("ImageUrl"));
                product.setImages(List.of(productImages));

                List<Author> authors = authorDAO.getAuthorsByProductId(rs.getLong("ProductID"));
                product.setAuthors(authors);

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
    public int getDistinctItemCount(long userId) {
        String sql = "SELECT COUNT(ProductID) AS ProductCount FROM CartItems WHERE UserID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, userId);
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
        CartItemDAO cartItemDAO = new CartItemDAO();

        List<CartItem> cartItems = cartItemDAO.getCartItemsByUserId(6);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + cartItems);
    }
}