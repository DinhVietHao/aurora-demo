package com.group01.aurora_demo.shop.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.profile.model.Address;
import com.group01.aurora_demo.shop.model.Shop;
import java.sql.*;

public class ShopDAO {

    public boolean isShopNameExists(String name) {
        String sql = "SELECT COUNT(*) FROM Shops WHERE Name = ?";
        try (Connection conn = DataSourceProvider.get().getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean createShop(Shop shop, Address address) {
        String addressSql = "INSERT INTO Addresses (RecipientName, Phone, City, ProvinceID, District, DistrictID, Ward, WardCode, Description, CreatedAt) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME())";

        String shopSql = "INSERT INTO Shops " +
                "(Name, Description, RatingAvg, [Status], OwnerUserID, CreatedAt, PickupAddressID, InvoiceEmail, AvatarUrl) "
                +
                "VALUES (?, ?, 0, ?, ?, SYSUTCDATETIME(), ?, ?, ?)";

        try (Connection conn = DataSourceProvider.get().getConnection()) {
            PreparedStatement addrStmt = conn.prepareStatement(addressSql, Statement.RETURN_GENERATED_KEYS);
            addrStmt.setString(1, address.getRecipientName());
            addrStmt.setString(2, address.getPhone());
            addrStmt.setString(3, address.getCity());
            addrStmt.setInt(4, address.getProvinceId());
            addrStmt.setString(5, address.getDistrict());
            addrStmt.setInt(6, address.getDistrictId());
            addrStmt.setString(7, address.getWard());
            addrStmt.setString(8, address.getWardCode());
            addrStmt.setString(9, address.getDescription());

            int addrInserted = addrStmt.executeUpdate();
            if (addrInserted == 0) {
                System.out.println("Không lấy được id address");
                return false;
            }

            ResultSet keys = addrStmt.getGeneratedKeys();
            if (keys.next()) {
                long addressId = keys.getLong(1);

                PreparedStatement shopStmt = conn.prepareStatement(shopSql);
                shopStmt.setString(1, shop.getName());
                shopStmt.setString(2, shop.getDescription());
                shopStmt.setString(3, shop.getStatus());
                shopStmt.setLong(4, shop.getOwnerUserId());
                shopStmt.setLong(5, addressId);
                shopStmt.setString(6, shop.getInvoiceEmail());
                shopStmt.setString(7, shop.getAvatarUrl());

                return shopStmt.executeUpdate() == 1;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Shop getShopByUserId(Long ownerUserId) throws SQLException {
        String sql = """
                SELECT
                s.ShopID, s.Name, s.Description AS ShopDescription, s.RatingAvg, s.Status,
                s.OwnerUserID, s.InvoiceEmail, s.AvatarUrl, s.PickupAddressID,
                a.AddressID, a.RecipientName, a.Phone, a.City, a.ProvinceID,
                a.District, a.DistrictID, a.Ward, a.WardCode, a.Description AS AddressDescription
                FROM Shops s
                JOIN Addresses a ON s.PickupAddressID = a.AddressID
                WHERE s.OwnerUserID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, ownerUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Shop shop = new Shop();
                    shop.setShopId(rs.getLong("ShopID"));
                    shop.setName(rs.getString("Name"));
                    shop.setDescription(rs.getString("ShopDescription"));
                    shop.setRatingAvg(rs.getDouble("RatingAvg"));
                    shop.setStatus(rs.getString("Status"));
                    shop.setOwnerUserId(rs.getLong("OwnerUserID"));
                    shop.setInvoiceEmail(rs.getString("InvoiceEmail"));
                    shop.setAvatarUrl(rs.getString("AvatarUrl"));
                    shop.setPickupAddressId(rs.getLong("PickupAddressID"));

                    Address addr = new Address();
                    addr.setAddressId(rs.getLong("AddressID"));
                    addr.setRecipientName(rs.getString("RecipientName"));
                    addr.setPhone(rs.getString("Phone"));
                    addr.setCity(rs.getString("City"));
                    addr.setProvinceId(rs.getInt("ProvinceID"));
                    addr.setDistrict(rs.getString("District"));
                    addr.setDistrictId(rs.getInt("DistrictID"));
                    addr.setWard(rs.getString("Ward"));
                    addr.setWardCode(rs.getString("WardCode"));
                    addr.setDescription(rs.getString("AddressDescription"));

                    shop.setPickupAddress(addr);
                    return shop;
                }
            }
        }
        return null;
    }

    public long getShopIdByUserId(long userId) throws SQLException {
        String sql = "SELECT ShopID FROM Shops WHERE OwnerUserID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("ShopID");
                }
            }
        }
        return -1;
    }

    public boolean updateAvatarShop(long shopId, String avatarUrl) {
        String sql = "UPDATE Shops SET AvatarUrl = ? WHERE ShopID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, avatarUrl);
            ps.setLong(2, shopId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateShopProfile(Shop shop, Address address) {
        String updateShopSql = "UPDATE Shops SET Name = ?, Description = ?, InvoiceEmail = ? WHERE ShopID = ?";

        String updateAddressSql = "UPDATE Addresses SET Phone = ?, City = ?, ProvinceID = ?, " +
                "District = ?, DistrictID = ?, Ward = ?, WardCode = ?, Description = ? " +
                "WHERE AddressID = ?";

        Connection conn = null;
        try {
            conn = DataSourceProvider.get().getConnection();
            conn.setAutoCommit(false);

            // Update Shop
            try (PreparedStatement psShop = conn.prepareStatement(updateShopSql)) {
                psShop.setString(1, shop.getName());
                psShop.setString(2, shop.getDescription());
                psShop.setString(3, shop.getInvoiceEmail());
                psShop.setLong(4, shop.getShopId());
                if (psShop.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Update Address
            try (PreparedStatement psAddr = conn.prepareStatement(updateAddressSql)) {
                psAddr.setString(1, address.getPhone());
                psAddr.setString(2, address.getCity());
                psAddr.setInt(3, address.getProvinceId());
                psAddr.setString(4, address.getDistrict());
                psAddr.setInt(5, address.getDistrictId());
                psAddr.setString(6, address.getWard());
                psAddr.setString(7, address.getWardCode());
                psAddr.setString(8, address.getDescription());
                psAddr.setLong(9, address.getAddressId());
                if (psAddr.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR] ShopDAO#updateShopProfile: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Shop getShopByIdWithStats(Long shopId) {
        String sql = """
                    SELECT
                        s.ShopID, s.Name, s.Description, s.RatingAvg, s.Status,
                        s.OwnerUserID, s.InvoiceEmail, s.AvatarUrl, s.PickupAddressID, s.CreatedAt,

                        -- Address fields
                        a.AddressID, a.RecipientName, a.Phone, a.City, a.ProvinceID,
                        a.District, a.DistrictID, a.Ward, a.WardCode, a.Description AS AddressDescription,

                        -- Statistics
                        (SELECT COUNT(*)
                         FROM Products
                         WHERE ShopID = s.ShopID AND Status = 'ACTIVE') AS ProductCount,

                        (SELECT COUNT(DISTINCT r.ReviewID)
                         FROM Reviews r
                         INNER JOIN OrderItems oi ON r.OrderItemID = oi.OrderItemID
                         INNER JOIN OrderShops os ON oi.OrderShopID = os.OrderShopID
                         WHERE os.ShopID = s.ShopID) AS ReviewCount,

                        (SELECT ISNULL(AVG(CAST(r.Rating AS FLOAT)), 0)
                         FROM Reviews r
                         INNER JOIN OrderItems oi ON r.OrderItemID = oi.OrderItemID
                         INNER JOIN OrderShops os ON oi.OrderShopID = os.OrderShopID
                         WHERE os.ShopID = s.ShopID) AS AvgRating

                    FROM Shops s
                    LEFT JOIN Addresses a ON s.PickupAddressID = a.AddressID
                    WHERE s.ShopID = ? AND s.Status = 'ACTIVE'
                """;
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, shopId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Shop shop = new Shop();

                // Basic shop info
                shop.setShopId(rs.getLong("ShopID"));
                shop.setName(rs.getString("Name"));
                shop.setDescription(rs.getString("Description"));
                shop.setRatingAvg(rs.getDouble("RatingAvg"));
                shop.setStatus(rs.getString("Status"));
                shop.setOwnerUserId(rs.getLong("OwnerUserID"));
                shop.setInvoiceEmail(rs.getString("InvoiceEmail"));
                shop.setAvatarUrl(rs.getString("AvatarUrl"));
                shop.setPickupAddressId(rs.getLong("PickupAddressID"));
                shop.setCreatedAt(rs.getTimestamp("CreatedAt"));

                // Statistics
                shop.setProductCount(rs.getInt("ProductCount"));
                shop.setReviewCount(rs.getInt("ReviewCount"));
                shop.setAvgRating(rs.getDouble("AvgRating"));

                // Load Address object
                Long addressId = rs.getLong("AddressID");
                if (!rs.wasNull() && addressId != null) {
                    Address address = new Address();
                    address.setAddressId(addressId);
                    address.setRecipientName(rs.getString("RecipientName"));
                    address.setPhone(rs.getString("Phone"));
                    address.setCity(rs.getString("City"));

                    // Handle nullable province ID
                    Integer provinceId = rs.getObject("ProvinceID", Integer.class);
                    address.setProvinceId(provinceId);
                    address.setDistrict(rs.getString("District"));

                    // Handle nullable district ID
                    Integer districtId = rs.getObject("DistrictID", Integer.class);
                    address.setDistrictId(districtId);

                    address.setWard(rs.getString("Ward"));
                    address.setWardCode(rs.getString("WardCode"));
                    address.setDescription(rs.getString("AddressDescription"));
                    shop.setPickupAddress(address);
                }
                return shop;
            }
        } catch (SQLException e) {
            System.err.println("Error in getShopByIdWithStats: " + e.getMessage());
        }
        return null;
    }

    public Long getShopIdByProductId(Long productId) {
        String sql = "SELECT ShopID FROM Products WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("ShopID");
            }
        } catch (Exception e) {
            System.out.println("Error in \"getShopIdByProductId\" function: " + e.getMessage());
        }
        return null;
    }
}
