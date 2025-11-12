package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingDAO {

    public double getPlatformFeePercentage() {
        String sql = "SELECT SettingValue FROM Setting WHERE SettingKey = N'Platform_fee'";
        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String value = rs.getString("SettingValue");
                return Double.parseDouble(value);
            }

        } catch (SQLException | NumberFormatException e) {
            System.err.println("[ERROR] SettingDAO#getPlatformFeePercentage: " + e.getMessage());
            e.printStackTrace();
        }
        return 5.0;
    }

}
