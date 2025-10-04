package com.group01.aurora_demo.shop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.shop.model.Address;
import com.group01.aurora_demo.shop.model.Shop;

public class ShopDAO {
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
                    shop.setRatingAvg(rs.getBigDecimal("RatingAvg"));
                    shop.setStatus(rs.getString("Status"));
                    shop.setOwnerUserId(rs.getLong("OwnerUserID"));
                    shop.setInvoiceEmail(rs.getString("InvoiceEmail"));
                    shop.setAvatarUrl(rs.getString("AvatarUrl"));
                    shop.setPickupAddressId(rs.getLong("PickupAddressID"));

                    Address addr = new Address();
                    addr.setAddressId(rs.getLong("AddressID"));
                    addr.setRecipientName(rs.getString("RecipientName"));
                    addr.setPhone(rs.getString("Phone"));
                    addr.setLine(rs.getString("Line"));
                    addr.setCity(rs.getString("City"));
                    addr.setDistrict(rs.getString("District"));
                    addr.setWard(rs.getString("Ward"));
                    addr.setPostalCode(rs.getString("PostalCode"));

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

}
