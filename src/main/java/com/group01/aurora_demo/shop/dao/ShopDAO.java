package com.group01.aurora_demo.shop.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.profile.model.Address;
import com.group01.aurora_demo.shop.model.Shop;
import java.sql.*;

public class ShopDAO {
    public Shop getShopByUserId(long userId) {
        String sql = "SELECT * FROM Shops WHERE OwnerUserID = ?";
        try (Connection conn = DataSourceProvider.get().getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Shop shop = new Shop();
                shop.setShopId(rs.getLong("ShopID"));
                shop.setName(rs.getString("Name"));
                shop.setStatus(rs.getString("Status"));
                // Map các trường khác...
                return shop;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

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
        String addressSql = "INSERT INTO Addresses (RecipientName, Phone, City, Ward, Description, CreatedAt) " +
                "VALUES (?, ?, ?, ?, ?, SYSUTCDATETIME())";

        String shopSql = "INSERT INTO Shops " +
                "(Name, Description, RatingAvg, [Status], OwnerUserID, CreatedAt, PickupAddressID, InvoiceEmail, AvatarUrl) "
                +
                "VALUES (?, ?, 0, ?, ?, SYSUTCDATETIME(), ?, ?, ?)";

        try (Connection conn = DataSourceProvider.get().getConnection()) {
            PreparedStatement addrStmt = conn.prepareStatement(addressSql, Statement.RETURN_GENERATED_KEYS);
            addrStmt.setString(1, address.getRecipientName());
            addrStmt.setString(2, address.getPhone());
            addrStmt.setString(3, address.getCity());
            addrStmt.setString(4, address.getWard());
            addrStmt.setString(5, address.getDescription());

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

    public Shop getShopByOwnerUserId(Long ownerUserId) throws SQLException {
        String sql = "SELECT " +
                " s.ShopID, s.Name, s.Description, s.RatingAvg, s.Status, " +
                " s.OwnerUserID, s.InvoiceEmail, s.AvatarUrl, s.PickupAddressID, " +
                " a.AddressID, a.RecipientName, a.Phone, a.Line, a.City, a.District, a.Ward, a.PostalCode " +
                "FROM Shops s " +
                "JOIN Addresses a ON s.PickupAddressID = a.AddressID " +
                "WHERE s.OwnerUserID = ?";

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, ownerUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Shop shop = new Shop();
                    shop.setShopId(rs.getLong("ShopID"));
                    shop.setName(rs.getString("Name"));
                    shop.setDescription(rs.getString("Description"));
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
                    addr.setDescription(rs.getString("Description"));
                    addr.setCity(rs.getString("City"));
                    addr.setWard(rs.getString("Ward"));
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

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
