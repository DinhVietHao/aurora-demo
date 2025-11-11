package com.group01.aurora_demo.common.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;
import javax.sql.DataSource;

/**
 * Provides a singleton HikariCP-backed DataSource for the app.
 * Create once, reuse everywhere.
 */
public class DataSourceProvider {

    // Single, shared connection pool for the whole application
    private static final HikariDataSource DS;

    static {
        // Container for HikariCP settings
        HikariConfig cfg = new HikariConfig();

        // =====================================================
        // ✅ CẤU HÌNH DÙNG CHO SQL SERVER LOCAL
        // =====================================================
        cfg.setJdbcUrl("jdbc:sqlserver://localhost:1433;databaseName=AuroraBook3;encrypt=false;");
        cfg.setUsername("sa");
        cfg.setPassword("123456");

        // =====================================================
        // ❌ CẤU HÌNH CŨ DÀNH CHO AZURE SQL (ĐÃ COMMENT LẠI)
        // =====================================================
        /*
        // JDBC URL: reads system property AURORA_JDBC_URL, falls back to Azure SQL Server
        cfg.setJdbcUrl(System.getProperty("AURORA_JDBC_URL",
                "jdbc:sqlserver://online-bookstore-dbserver.database.windows.net:1433;"
                        + "database=OnlineBookstore;"
                        + "encrypt=true;"
                        + "trustServerCertificate=false;"
                        + "hostNameInCertificate=*.database.windows.net;"
                        + "loginTimeout=30;"));

        // DB username: can be overridden with -DAURORA_DB_USER=...
        cfg.setUsername(System.getProperty("AURORA_DB_USER", "bookstoredbadmin"));

        // DB password: can be overridden with -DAURORA_DB_PASSWORD=...
        cfg.setPassword(System.getProperty("AURORA_DB_PASSWORD", "Aurora@2025!Group1"));
        */

        // =====================================================
        // ⚙️ CÁC THIẾT LẬP CHUNG CHO CONNECTION POOL
        // =====================================================
        cfg.setMaximumPoolSize(10);         // tối đa 10 connection
        cfg.setMinimumIdle(2);              // luôn giữ sẵn 2 connection
        cfg.setConnectionTimeout(30000);    // timeout 30s
        cfg.setIdleTimeout(600000);         // idle 10 phút
        cfg.setMaxLifetime(1800000);        // mỗi connection sống 30 phút
        cfg.setLeakDetectionThreshold(60000); // cảnh báo nếu connection bị leak > 60s
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.setValidationTimeout(5000);
        cfg.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        // Build the HikariCP DataSource (initializes the pool)
        DS = new HikariDataSource(cfg);
    }

    /**
     * Returns the shared DataSource. Do not close it in callers;
     * let the application manage its lifecycle on shutdown.
     */
    public static DataSource get() {
        return DS;
    }
}
