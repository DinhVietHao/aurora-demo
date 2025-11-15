package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.admin.model.Setting;
import com.group01.aurora_demo.common.config.DataSourceProvider;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Setting operations in admin module
 *
 * @author Aurora Team
 */
public class SettingDAO {
    private final DataSource dataSource;

    public SettingDAO() {
        this.dataSource = DataSourceProvider.get();
    }

    /**
     * Get all settings
     */
    public List<Setting> getAllSettings() {
        List<Setting> list = new ArrayList<>();
        String sql = "SELECT setting_id, setting_key, setting_value, description, created_at, updated_at " +
                     "FROM setting ORDER BY setting_key ASC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Setting setting = mapResultSetToSetting(rs);
                list.add(setting);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get setting by ID
     */
    public Setting getSettingById(int settingId) {
        String sql = "SELECT setting_id, setting_key, setting_value, description, created_at, updated_at " +
                     "FROM setting WHERE setting_id = ?";
        Setting setting = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, settingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    setting = mapResultSetToSetting(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return setting;
    }

    /**
     * Get setting by key
     */
    public Setting getSettingByKey(String settingKey) {
        String sql = "SELECT setting_id, setting_key, setting_value, description, created_at, updated_at " +
                     "FROM setting WHERE setting_key = ?";
        Setting setting = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, settingKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    setting = mapResultSetToSetting(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return setting;
    }

    /**
     * Add new setting
     */
    public boolean addSetting(Setting setting) {
        String sql = "INSERT INTO setting (setting_key, setting_value, description) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, setting.getSettingKey());
            ps.setString(2, setting.getSettingValue());
            ps.setString(3, setting.getDescription());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update setting
     */
    public boolean updateSetting(Setting setting) {
        String sql = "UPDATE setting SET setting_key = ?, setting_value = ?, description = ?, " +
                     "updated_at = GETDATE() WHERE setting_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, setting.getSettingKey());
            ps.setString(2, setting.getSettingValue());
            ps.setString(3, setting.getDescription());
            ps.setInt(4, setting.getSettingId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete setting
     */
    public boolean deleteSetting(int settingId) {
        String sql = "DELETE FROM setting WHERE setting_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, settingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if setting key exists
     */
    public boolean settingKeyExists(String settingKey) {
        String sql = "SELECT COUNT(*) FROM setting WHERE setting_key = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, settingKey);
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
     * Check if setting key exists excluding a specific setting ID (for update validation)
     */
    public boolean settingKeyExistsExcludingId(String settingKey, int excludeSettingId) {
        String sql = "SELECT COUNT(*) FROM setting WHERE setting_key = ? AND setting_id != ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, settingKey);
            ps.setInt(2, excludeSettingId);
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
     * Get settings with pagination and filtering
     */
    public List<Setting> getSettingsWithPagination(String searchTerm, int offset, int limit) {
        List<Setting> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT setting_id, setting_key, setting_value, description, created_at, updated_at " +
            "FROM setting WHERE 1=1"
        );

        // Add search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (setting_key LIKE ? OR setting_value LIKE ? OR description LIKE ?)");
        }

        sql.append(" ORDER BY setting_key ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set search parameters
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            // Set pagination parameters
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Setting setting = mapResultSetToSetting(rs);
                    list.add(setting);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get total count of settings with filtering
     */
    public int getSettingCount(String searchTerm) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM setting WHERE 1=1");

        // Add search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (setting_key LIKE ? OR setting_value LIKE ? OR description LIKE ?)");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Set search parameters
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.trim() + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
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

    /**
     * Helper method to map ResultSet to Setting object
     */
    private Setting mapResultSetToSetting(ResultSet rs) throws SQLException {
        Setting setting = new Setting();
        setting.setSettingId(rs.getInt("setting_id"));
        setting.setSettingKey(rs.getString("setting_key"));
        setting.setSettingValue(rs.getString("setting_value"));
        setting.setDescription(rs.getString("description"));

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            setting.setCreatedAt(createdTs.toLocalDateTime());
        }

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            setting.setUpdatedAt(updatedTs.toLocalDateTime());
        }

        return setting;
    }
}

