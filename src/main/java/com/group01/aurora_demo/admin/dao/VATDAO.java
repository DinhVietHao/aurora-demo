package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.admin.model.VAT;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for VAT operations
 *
 * @author Aurora Team
 */
public class VATDAO {
    private final DataSource dataSource;

    public VATDAO() {
        this.dataSource = DataSourceProvider.get();
    }

    /**
     * Get all VAT records
     */
    public List<VAT> getAllVAT() {
        List<VAT> vatList = new ArrayList<>();
        String sql = "SELECT VATCode, VATRate, Description FROM VAT ORDER BY VATRate";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                VAT vat = new VAT();
                vat.setVatCode(rs.getString("VATCode"));
                vat.setVatRate(rs.getDouble("VATRate"));
                vat.setDescription(rs.getString("Description"));
                vatList.add(vat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vatList;
    }

    /**
     * Get VAT by code
     */
    public VAT getVATByCode(String vatCode) {
        String sql = "SELECT VATCode, VATRate, Description FROM VAT WHERE VATCode = ?";
        VAT vat = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vatCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    vat = new VAT();
                    vat.setVatCode(rs.getString("VATCode"));
                    vat.setVatRate(rs.getDouble("VATRate"));
                    vat.setDescription(rs.getString("Description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vat;
    }

    /**
     * Get VAT rate by product ID (through category)
     */
    public double getVATRateByProductId(long productId) {
        String sql = "SELECT v.VATRate FROM VAT v " +
                     "INNER JOIN Category c ON v.VATCode = c.VATCode " +
                     "INNER JOIN ProductCategory pc ON c.CategoryID = pc.CategoryID " +
                     "WHERE pc.ProductID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("VATRate");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Default VAT rate if not found
    }

    /**
     * Add new VAT record
     */
    public boolean addVAT(VAT vat) {
        String sql = "INSERT INTO VAT (VATCode, VATRate, Description) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vat.getVatCode());
            ps.setDouble(2, vat.getVatRate());
            ps.setString(3, vat.getDescription());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update VAT record
     */
    public boolean updateVAT(VAT vat) {
        String sql = "UPDATE VAT SET VATRate = ?, Description = ? WHERE VATCode = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, vat.getVatRate());
            ps.setString(2, vat.getDescription());
            ps.setString(3, vat.getVatCode());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete VAT record
     */
    public boolean deleteVAT(String vatCode) {
        String sql = "DELETE FROM VAT WHERE VATCode = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vatCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if VAT code exists
     */
    public boolean vatCodeExists(String vatCode) {
        String sql = "SELECT COUNT(*) FROM VAT WHERE VATCode = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vatCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if VAT code is in use by any category
     */
    public boolean isVATInUse(String vatCode) {
        String sql = "SELECT COUNT(*) FROM Category WHERE VATCode = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vatCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get VAT records with pagination and filtering
     */
    public List<VAT> getVATWithPagination(String searchTerm, int offset, int limit) {
        List<VAT> vatList = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT VATCode, VATRate, Description FROM VAT WHERE 1=1"
        );

        // Add search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (VATCode LIKE ? OR Description LIKE ?)");
        }

        sql.append(" ORDER BY VATRate ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set search parameters
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            // Set pagination parameters
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VAT vat = new VAT();
                    vat.setVatCode(rs.getString("VATCode"));
                    vat.setVatRate(rs.getDouble("VATRate"));
                    vat.setDescription(rs.getString("Description"));
                    vatList.add(vat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vatList;
    }

    /**
     * Get total count of VAT records with filtering
     */
    public int getVATCount(String searchTerm) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM VAT WHERE 1=1");

        // Add search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (VATCode LIKE ? OR Description LIKE ?)");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Set search parameters
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.trim() + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
