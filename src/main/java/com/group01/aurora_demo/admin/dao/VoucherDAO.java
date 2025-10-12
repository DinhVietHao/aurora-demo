package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.admin.model.Voucher;
import com.group01.aurora_demo.utils.DBConnectionUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoucherDAO {

    public List<Voucher> getAllVouchers() {
        List<Voucher> vouchers = new ArrayList<>();
        String query = "SELECT * FROM Vouchers ORDER BY VoucherID DESC";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                vouchers.add(mapResultSetToVoucher(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return vouchers;
    }
    
    public Optional<Voucher> getVoucherById(long voucherId) {
        String query = "SELECT * FROM Vouchers WHERE VoucherID = ?";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setLong(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVoucher(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public Optional<Voucher> getVoucherByCode(String code) {
        String query = "SELECT * FROM Vouchers WHERE Code = ?";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVoucher(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Check if a voucher code already exists
     * @param code The voucher code to check
     * @return true if the code exists, false otherwise
     */
    public boolean isVoucherCodeExists(String code) {
        String query = "SELECT COUNT(*) AS Count FROM Vouchers WHERE Code = ?";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Count") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public List<Voucher> getVouchersByStatus(String status) {
        List<Voucher> vouchers = new ArrayList<>();
        String query = "SELECT * FROM Vouchers WHERE Status = ? ORDER BY VoucherID DESC";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vouchers.add(mapResultSetToVoucher(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return vouchers;
    }
    
    public boolean createVoucher(Voucher voucher) {
        String query = "INSERT INTO Vouchers (Code, DiscountType, Value, MaxAmount, MinOrderAmount, " +
                      "StartAt, EndAt, UsageLimit, PerUserLimit, Status, UsageCount) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, voucher.getCode());
            ps.setString(2, voucher.getDiscountType());
            ps.setBigDecimal(3, voucher.getValue());
            
            if (voucher.getMaxAmount() != null) {
                ps.setBigDecimal(4, voucher.getMaxAmount());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }
            
            ps.setBigDecimal(5, voucher.getMinOrderAmount());
            ps.setTimestamp(6, Timestamp.valueOf(voucher.getStartAt()));
            ps.setTimestamp(7, Timestamp.valueOf(voucher.getEndAt()));
            
            if (voucher.getUsageLimit() != null) {
                ps.setInt(8, voucher.getUsageLimit());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            
            if (voucher.getPerUserLimit() != null) {
                ps.setInt(9, voucher.getPerUserLimit());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            
            ps.setString(10, voucher.getStatus());
            ps.setInt(11, voucher.getUsageCount());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    voucher.setVoucherId(generatedKeys.getLong(1));
                }
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateVoucher(Voucher voucher) {
        String query = "UPDATE Vouchers SET Code = ?, DiscountType = ?, Value = ?, MaxAmount = ?, " +
                      "MinOrderAmount = ?, StartAt = ?, EndAt = ?, UsageLimit = ?, " +
                      "PerUserLimit = ?, Status = ? WHERE VoucherID = ?";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, voucher.getCode());
            ps.setString(2, voucher.getDiscountType());
            ps.setBigDecimal(3, voucher.getValue());
            
            if (voucher.getMaxAmount() != null) {
                ps.setBigDecimal(4, voucher.getMaxAmount());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }
            
            ps.setBigDecimal(5, voucher.getMinOrderAmount());
            ps.setTimestamp(6, Timestamp.valueOf(voucher.getStartAt()));
            ps.setTimestamp(7, Timestamp.valueOf(voucher.getEndAt()));
            
            if (voucher.getUsageLimit() != null) {
                ps.setInt(8, voucher.getUsageLimit());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            
            if (voucher.getPerUserLimit() != null) {
                ps.setInt(9, voucher.getPerUserLimit());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            
            ps.setString(10, voucher.getStatus());
            ps.setLong(11, voucher.getVoucherId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateVoucherStatus(long voucherId, String status) {
        String query = "UPDATE Vouchers SET Status = ? WHERE VoucherID = ?";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, status);
            ps.setLong(2, voucherId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteVoucher(long voucherId) {
        String query = "DELETE FROM Vouchers WHERE VoucherID = ?";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setLong(1, voucherId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> getAllDiscountTypes() {
        List<String> types = new ArrayList<>();
        String query = "SELECT TypeCode FROM DiscountType";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                types.add(rs.getString("TypeCode"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Nếu không có dữ liệu từ database, trả về giá trị mặc định
        if (types.isEmpty()) {
            types.add("AMOUNT");
            types.add("PERCENT");
        }
        
        return types;
    }
    
    public List<String> getAllVoucherStatuses() {
        List<String> statuses = new ArrayList<>();
        String query = "SELECT StatusCode FROM VoucherStatus";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                statuses.add(rs.getString("StatusCode"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return statuses;
    }
    
    public int getActiveVouchersCount() {
        return getVoucherCountByStatus("active");
    }
    
    public int getPendingVouchersCount() {
        return getVoucherCountByStatus("pending");
    }
    
    public int getExpiredVouchersCount() {
        return getVoucherCountByStatus("expired");
    }
    
    public int getTotalUsageCount() {
        String query = "SELECT SUM(UsageCount) AS TotalUsage FROM Vouchers";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("TotalUsage");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    private int getVoucherCountByStatus(String status) {
        String query = "SELECT COUNT(*) AS Count FROM Vouchers WHERE Status = ?";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    private Voucher mapResultSetToVoucher(ResultSet rs) throws SQLException {
        Voucher voucher = new Voucher();
        
        voucher.setVoucherId(rs.getLong("VoucherID"));
        voucher.setCode(rs.getString("Code"));
        voucher.setDiscountType(rs.getString("DiscountType"));
        voucher.setValue(rs.getBigDecimal("Value"));
        
        BigDecimal maxAmount = rs.getBigDecimal("MaxAmount");
        if (!rs.wasNull()) {
            voucher.setMaxAmount(maxAmount);
        }
        
        voucher.setMinOrderAmount(rs.getBigDecimal("MinOrderAmount"));
        voucher.setStartAt(rs.getTimestamp("StartAt").toLocalDateTime());
        voucher.setEndAt(rs.getTimestamp("EndAt").toLocalDateTime());
        
        int usageLimit = rs.getInt("UsageLimit");
        if (!rs.wasNull()) {
            voucher.setUsageLimit(usageLimit);
        }
        
        int perUserLimit = rs.getInt("PerUserLimit");
        if (!rs.wasNull()) {
            voucher.setPerUserLimit(perUserLimit);
        }
        
        voucher.setStatus(rs.getString("Status"));
        voucher.setUsageCount(rs.getInt("UsageCount"));
        
        return voucher;
    }
}
