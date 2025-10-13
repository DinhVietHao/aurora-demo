package com.group01.aurora_demo.profile.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.profile.model.Address;
import com.group01.aurora_demo.profile.model.UserAddress;

public class AddressDAO {

    public boolean hasAddress(long userId) {
        String sql = "SELECT COUNT(*) AS addressCount FROM Users_Addresses WHERE UserID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("addressCount") > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Address> getAddressesByUserId(long userId) {
        List<Address> list = new ArrayList<>();
        String sql = """
                    SELECT a.AddressID, a.RecipientName, a.Phone, a.City,
                           a.Description, a.Ward, ua.IsDefault
                    FROM Addresses a
                    JOIN Users_Addresses ua ON a.AddressID = ua.AddressID
                    WHERE ua.UserID = ?
                    ORDER BY ua.IsDefault DESC, a.CreatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Address address = new Address();
                address.setAddressId(rs.getLong("AddressID"));
                address.setRecipientName(rs.getString("RecipientName"));
                address.setPhone(rs.getString("Phone"));
                address.setCity(rs.getString("City"));
                address.setDescription(rs.getString("Description"));
                address.setWard(rs.getString("Ward"));

                UserAddress userAddress = new UserAddress();
                userAddress.setUserId(userId);
                userAddress.setAddressId(address.getAddressId());
                userAddress.setDefaultAddress(rs.getBoolean("IsDefault"));
                address.setUserAddress(userAddress);

                list.add(address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Address getDefaultAddress(long userId) {
        String sql = """
                    SELECT a.AddressID, a.RecipientName, a.Phone, a.City, a.Ward, a.Description, ua.IsDefault
                    FROM Addresses a
                    JOIN Users_Addresses ua ON a.AddressID = ua.AddressID
                    WHERE ua.UserID = ? AND ua.IsDefault = 1
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Address address = new Address();
                address.setAddressId(rs.getLong("AddressID"));
                address.setRecipientName(rs.getString("RecipientName"));
                address.setPhone(rs.getString("Phone"));
                address.setCity(rs.getString("City"));
                address.setWard(rs.getString("Ward"));
                address.setDescription(rs.getString("Description"));

                UserAddress userAddress = new UserAddress();
                userAddress.setUserId(userId);
                userAddress.setAddressId(address.getAddressId());
                userAddress.setDefaultAddress(rs.getBoolean("IsDefault"));
                address.setUserAddress(userAddress);

                return address;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Address getAddressById(long userId, long addressId) {
        String sql = """
                    SELECT a.AddressID, a.RecipientName, a.Phone, a.City,
                           a.Description, a.Ward, ua.IsDefault
                    FROM Addresses a
                    JOIN Users_Addresses ua ON a.AddressID = ua.AddressID
                    WHERE ua.UserID = ? AND a.AddressID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, addressId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Address address = new Address();
                address.setAddressId(rs.getLong("AddressID"));
                address.setRecipientName(rs.getString("RecipientName"));
                address.setPhone(rs.getString("Phone"));
                address.setCity(rs.getString("City"));
                address.setWard(rs.getString("Ward"));
                address.setDescription(rs.getString("Description"));

                UserAddress userAddress = new UserAddress();
                userAddress.setUserId(userId);
                userAddress.setAddressId(address.getAddressId());
                userAddress.setDefaultAddress(rs.getBoolean("IsDefault"));
                address.setUserAddress(userAddress);

                return address;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addAddress(long userId, Address address, boolean isDefault) {
        String insertAddress = """
                    INSERT INTO Addresses (RecipientName, Phone, City, Ward, Description)
                    VALUES (?, ?, ?, ?, ?)
                """;
        String insertUserAddress = """
                    INSERT INTO Users_Addresses (UserID, AddressID, IsDefault)
                    VALUES (?, ?, ?)
                """;
        String updateDefault = "UPDATE Users_Addresses SET IsDefault = 0 WHERE UserID = ?";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps1 = cn.prepareStatement(insertAddress, Statement.RETURN_GENERATED_KEYS)) {

            ps1.setString(1, address.getRecipientName());
            ps1.setString(2, address.getPhone());
            ps1.setString(3, address.getCity());
            ps1.setString(4, address.getWard());
            ps1.setString(5, address.getDescription());
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            long addressId = 0;
            if (rs.next())
                addressId = rs.getLong(1);

            if (isDefault) {
                try (PreparedStatement reset = cn.prepareStatement(updateDefault)) {
                    reset.setLong(1, userId);
                    reset.executeUpdate();
                }
            }

            try (PreparedStatement ps2 = cn.prepareStatement(insertUserAddress)) {
                ps2.setLong(1, userId);
                ps2.setLong(2, addressId);
                ps2.setBoolean(3, isDefault);
                ps2.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAddress(long userId, Address address, boolean setAsDefault) {
        String updateAddress = """
                    UPDATE Addresses
                    SET RecipientName=?, Phone=?, City=?, Ward=?, Description=?, createdAt = SYSUTCDATETIME()
                    WHERE AddressID=?
                """;
        String updateDefault = "UPDATE Users_Addresses SET IsDefault = 0 WHERE UserID = ?";
        String updateUserAddress = "UPDATE Users_Addresses SET IsDefault=? WHERE UserID=? AND AddressID=?";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps1 = cn.prepareStatement(updateAddress)) {

            ps1.setString(1, address.getRecipientName());
            ps1.setString(2, address.getPhone());
            ps1.setString(3, address.getCity());
            ps1.setString(4, address.getWard());
            ps1.setString(5, address.getDescription());
            ps1.setLong(6, address.getAddressId());
            ps1.executeUpdate();

            if (setAsDefault) {
                try (PreparedStatement reset = cn.prepareStatement(updateDefault)) {
                    reset.setLong(1, userId);
                    reset.executeUpdate();
                }
            }

            try (PreparedStatement ps2 = cn.prepareStatement(updateUserAddress)) {
                ps2.setBoolean(1, setAsDefault);
                ps2.setLong(2, userId);
                ps2.setLong(3, address.getAddressId());
                ps2.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteAddress(long userId, long addressId) {
        String deleteUserAddress = "DELETE FROM Users_Addresses WHERE UserID=? AND AddressID=?";
        String deleteAddress = "DELETE FROM Addresses WHERE AddressID=?";

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            try (PreparedStatement ps1 = cn.prepareStatement(deleteUserAddress)) {
                ps1.setLong(1, userId);
                ps1.setLong(2, addressId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = cn.prepareStatement(deleteAddress)) {
                ps2.setLong(1, addressId);
                ps2.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setDefaultAddress(long userId, long addressId) {
        String resetDefault = "UPDATE Users_Addresses SET IsDefault = 0 WHERE UserID = ?";
        String setDefault = "UPDATE Users_Addresses SET IsDefault = 1 WHERE UserID = ? AND AddressID = ?";

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            try (PreparedStatement ps1 = cn.prepareStatement(resetDefault)) {
                ps1.setLong(1, userId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = cn.prepareStatement(setDefault)) {
                ps2.setLong(1, userId);
                ps2.setLong(2, addressId);
                ps2.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Address getAddressByShopId(long shopId) {
        String sql = """
                    SELECT a.AddressID, a.RecipientName, a.Phone, a.City, a.Ward, a.Description
                    FROM Shops s
                    JOIN Addresses a ON s.PickupAddressID = a.AddressID
                    WHERE s.ShopID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Address address = new Address();
                    address.setAddressId(rs.getLong("AddressID"));
                    address.setRecipientName(rs.getString("RecipientName"));
                    address.setPhone(rs.getString("Phone"));
                    address.setCity(rs.getString("City"));
                    address.setWard(rs.getString("Ward"));
                    address.setDescription(rs.getString("Description"));
                    return address;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
