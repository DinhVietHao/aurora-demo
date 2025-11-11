package com.group01.aurora_demo.utils;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnectionUtil {
    private static HikariDataSource dataSource;
    
    static {
        try {
            HikariConfig config = new HikariConfig();

            // ========================
            // 🔽 CẤU HÌNH DÀNH CHO SQL SERVER LOCAL
            // ========================
            config.setJdbcUrl("jdbc:sqlserver://localhost:1433;databaseName=AuroraBook3;encrypt=true;");
            config.setUsername("sa");
            config.setPassword("123456");

            // ========================
            // 🔽 CẤU HÌNH DÀNH CHO AZURE SQL (ĐÃ COMMENT LẠI)
            // ========================
            /*
            config.setJdbcUrl("jdbc:sqlserver://<your-server-name>.database.windows.net:1433;"
                    + "database=<your-database-name>;"
                    + "encrypt=true;"
                    + "trustServerCertificate=false;"
                    + "hostNameInCertificate=*.database.windows.net;"
                    + "loginTimeout=30;");
            config.setUsername("<your-azure-username>");
            config.setPassword("<your-azure-password>");
            */

            // ========================
            // ⚙️ THÔNG SỐ CHUNG CỦA HIKARI CONNECTION POOL
            // ========================
            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    private DBConnectionUtil() {
        // Private constructor to prevent instantiation
    }
}
