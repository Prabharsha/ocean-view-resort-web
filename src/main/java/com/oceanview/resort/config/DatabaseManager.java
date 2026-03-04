package com.oceanview.resort.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database connection manager using HikariCP connection pool.
 *
 * <p><b>Design Pattern: Singleton Pattern</b></p>
 * <p>This class ensures only one connection pool exists in the application.
 * Initialised once during application startup by {@link AppContextListener}
 * and provides connections to all DAO classes.</p>
 */
public class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);

    /** Singleton instance */
    private static DatabaseManager instance;

    private HikariDataSource dataSource;

    private DatabaseManager() {}

    /**
     * Initialises the singleton with database connection parameters.
     *
     * @param url      JDBC URL
     * @param username DB username
     * @param password DB password
     */
    public static synchronized void init(String url, String username, String password) {
        if (instance != null) {
            log.warn("DatabaseManager already initialised");
            return;
        }

        instance = new DatabaseManager();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        instance.dataSource = new HikariDataSource(config);
        log.info("DatabaseManager initialised with HikariCP pool (max=20)");
    }

    /** Returns the singleton instance. */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager not initialised. Call init() first.");
        }
        return instance;
    }

    /** Gets a connection from the pool. */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /** Returns the underlying DataSource. */
    public DataSource getDataSource() {
        return dataSource;
    }

    /** Shuts down the connection pool. */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("DatabaseManager connection pool closed");
        }
    }
}

