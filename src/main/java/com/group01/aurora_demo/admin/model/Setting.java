package com.group01.aurora_demo.admin.model;

import java.time.LocalDateTime;

/**
 * Model representing a Setting in the admin module
 *
 * @author Aurora Team
 */
public class Setting {
    private Integer settingId;
    private String settingKey;
    private String settingValue;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Setting() {
    }

    public Setting(Integer settingId, String settingKey, String settingValue, String description, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.settingId = settingId;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getSettingId() {
        return settingId;
    }

    public void setSettingId(Integer settingId) {
        this.settingId = settingId;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "settingId=" + settingId +
                ", settingKey='" + settingKey + '\'' +
                ", settingValue='" + settingValue + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

