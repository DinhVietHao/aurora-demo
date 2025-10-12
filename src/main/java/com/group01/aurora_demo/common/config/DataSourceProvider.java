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
        // Note: encrypt=false is convenient for local dev; enable encryption in
        // production.
        cfg.setJdbcUrl(System.getProperty("AURORA_JDBC_URL",
                "jdbc:sqlserver://localhost:1433;databaseName=AuroraDemo2;encrypt=false"));
        // DB username: can be overridden with -DAURORA_DB_USER=...
        cfg.setUsername(System.getProperty("AURORA_DB_USER", "sa"));

        // DB password: can be overridden with -DAURORA_DB_PASSWORD=...
        // Avoid hard-coding secrets in production; use env vars/secret manager.
        cfg.setPassword(System.getProperty("AURORA_DB_PASSWORD", "123456"));

        // Max number of connections in the pool (tune per workload)
        cfg.setMaximumPoolSize(5);

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