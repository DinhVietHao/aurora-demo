package com.group01.aurora_demo.admin.service;

import com.group01.aurora_demo.admin.dao.SettingDAO;
import com.group01.aurora_demo.admin.model.Setting;
import com.group01.aurora_demo.common.util.Pagination;
import com.group01.aurora_demo.common.util.PaginatedResult;

import java.util.List;

/**
 * Service class for Setting business logic
 *
 * @author Aurora Team
 */
public class SettingService {
    private final SettingDAO settingDAO;

    public SettingService() {
        this.settingDAO = new SettingDAO();
    }

    /**
     * Get all settings
     */
    public List<Setting> getAllSettings() {
        return settingDAO.getAllSettings();
    }

    /**
     * Get setting by ID
     */
    public Setting getSettingById(int settingId) {
        return settingDAO.getSettingById(settingId);
    }

    /**
     * Get setting by key
     */
    public Setting getSettingByKey(String settingKey) {
        return settingDAO.getSettingByKey(settingKey);
    }

    /**
     * Add new setting
     */
    public boolean addSetting(String settingKey, String settingValue, String description) {
        // Validate input
        if (settingKey == null || settingKey.trim().isEmpty()) {
            return false;
        }

        // Check if setting key already exists
        if (settingDAO.settingKeyExists(settingKey.trim())) {
            return false;
        }

        Setting setting = new Setting();
        setting.setSettingKey(settingKey.trim());
        setting.setSettingValue(settingValue != null ? settingValue.trim() : null);
        setting.setDescription(description != null ? description.trim() : null);

        return settingDAO.addSetting(setting);
    }

    /**
     * Update setting
     */
    public boolean updateSetting(int settingId, String settingKey, String settingValue, String description) {
        // Validate input
        if (settingKey == null || settingKey.trim().isEmpty()) {
            return false;
        }

        // Check if setting exists
        Setting existingSetting = settingDAO.getSettingById(settingId);
        if (existingSetting == null) {
            return false;
        }

        // Check if new key conflicts with another setting
        if (settingDAO.settingKeyExistsExcludingId(settingKey.trim(), settingId)) {
            return false;
        }

        Setting setting = new Setting();
        setting.setSettingId(settingId);
        setting.setSettingKey(settingKey.trim());
        setting.setSettingValue(settingValue != null ? settingValue.trim() : null);
        setting.setDescription(description != null ? description.trim() : null);

        return settingDAO.updateSetting(setting);
    }

    /**
     * Delete setting
     */
    public boolean deleteSetting(int settingId) {
        // Check if setting exists
        Setting setting = settingDAO.getSettingById(settingId);
        if (setting == null) {
            return false;
        }

        return settingDAO.deleteSetting(settingId);
    }

    /**
     * Check if setting key exists
     */
    public boolean settingKeyExists(String settingKey) {
        return settingDAO.settingKeyExists(settingKey);
    }

    /**
     * Get settings with pagination and filtering
     */
    public PaginatedResult<Setting> getSettingsWithPagination(String searchTerm, int page, int pageSize) {
        // Get total count with filters
        int totalRecords = settingDAO.getSettingCount(searchTerm);

        // Create pagination object
        Pagination pagination = new Pagination(page, pageSize, totalRecords);

        // Get paginated data
        List<Setting> settings = settingDAO.getSettingsWithPagination(searchTerm, pagination.getOffset(), pagination.getLimit());

        return new PaginatedResult<>(settings, pagination);
    }

    /**
     * Get setting value by key (convenience method)
     */
    public String getSettingValue(String settingKey) {
        Setting setting = settingDAO.getSettingByKey(settingKey);
        return setting != null ? setting.getSettingValue() : null;
    }

    /**
     * Update setting value by key (convenience method)
     */
    public boolean updateSettingValue(String settingKey, String settingValue) {
        Setting setting = settingDAO.getSettingByKey(settingKey);
        if (setting == null) {
            return false;
        }
        
        setting.setSettingValue(settingValue);
        return settingDAO.updateSetting(setting);
    }
}

