package com.remal.signaltrading.tradehistory.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Hikari connection pool configuration.
 *
 * @author arnold.somogyi@gmail.com
 */
@SpringBootConfiguration
public class HikariConfiguration {

    /**
     * JDBC driver class name.
     */
    @Value("${datasource.driver-class-name}")
    private String driverClassName;

    /**
     * JDBC URL.
     */
    @Value("${datasource.url}")
    private String url;

    /**
     * The user to use for connecting to the database.
     */
    @Value("${datasource.username}")
    private String username;

    /**
     * Password for the connecting user.
     */
    @Value("${datasource.password}")
    private String password;

    /**
     * The minimum number of idle connections Hikari maintains in the pool.
     * Additional connections will be established to meet this value unless
     * the pool is full.
     */
    @Value("${datasource.minimum-idle}")
    private int minimumIdle;

    /**
     * Limits the total number of concurrent connections this pool will keep.
     * Ideal values for this setting are highly variable on app design,
     * infrastructure, and database.
     */
    @Value("${datasource.maximum-pool-size}")
    private int maximumPoolSize;

    /**
     * The maximum amount of time a connection can sit in the pool. Connections
     * that sit idle for this many milliseconds are retried if minimumIdle is
     * exceeded.
     */
    @Value("${datasource.idle-timeout}")
    private int idleTimeout;

    /**
     * The maximum possible lifetime of a connection in the pool. Connections
     * that live longer than this many milliseconds will be closed and
     * reestablished between uses. This value should be several minutes shorter
     * than the database's timeout value to avoid unexpected terminations.
     */
    @Value("${datasource.max-lifetime}")
    private int maxLifetime;

    /**
     * SQL query to be executed to test the validity of connections.
     */
    @Value("${datasource.validation-query}")
    private String validationQuery;

    /**
     * Hikari data-source builder.
     *
     * @return datasource
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setConnectionTestQuery(validationQuery);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 256);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);

        return new HikariDataSource(config);
    }
}
