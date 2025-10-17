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

        // JDBC URL: reads system property AURORA_JDBC_URL, falls back to local SQL
        // Server.
        // cfg.setJdbcUrl(System.getProperty("AURORA_JDBC_URL",
        // "jdbc:sqlserver://online-bookstore-dbserver.database.windows.net:1433;"
        // + "database=OnlineBookstore;"
        // + "encrypt=true;"
        // + "trustServerCertificate=false;"
        // + "hostNameInCertificate=*.database.windows.net;"
        // + "loginTimeout=30;"));

        // // DB username: can be overridden with -DAURORA_DB_USER=...
        // cfg.setUsername(System.getProperty("AURORA_DB_USER", "bookstoredbadmin"));

        // // DB password: can be overridden with -DAURORA_DB_PASSWORD=...
        // cfg.setPassword(System.getProperty("AURORA_DB_PASSWORD",
        // "Aurora@2025!Group1"));
        cfg.setJdbcUrl("jdbc:sqlserver://localhost:1433;database=online-bookstore-dbserver;encrypt=false;");
        cfg.setUsername("sa");
        cfg.setPassword("123456");

        // Pool size config
        cfg.setMaximumPoolSize(20); // Tăng từ 5 lên 20
        cfg.setMinimumIdle(5); // Giữ 5 idle connections

        // Timeouts
        cfg.setConnectionTimeout(30000); // 30s
        cfg.setIdleTimeout(600000); // 10 phút (close idle connections)
        cfg.setMaxLifetime(1800000); // 30 phút (refresh connections)
        cfg.setLeakDetectionThreshold(60000); // Detect leaks sau 60s

        // Test connections (cho SQL Server)
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.setValidationTimeout(5000); // Timeout cho test

        // Explicit driver; often optional with modern JDBC but kept for clarity
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