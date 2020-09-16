package com.remal.signaltrading.tradehistory.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import javax.sql.DataSource;

import com.remal.signaltrading.tradehistory.model.Candle;
import com.remal.signaltrading.tradehistory.service.coinbase.CandlesService;
import com.remal.signaltrading.tradehistory.service.coinbase.Granularity;
import com.remal.signaltrading.tradehistory.taskscheduler.TokenBucket;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a task, executed by CommandLineRunner.
 *
 * @author arnold.somogyi@gmail.com
 */
@Slf4j
public class CoinbaseCandlesTask implements Runnable {

    private static final Granularity GRANULARITY = Granularity.ONE_MINUTE;

    private final String id;
    private final TokenBucket tokenBucket;
    private final CandlesService service;
    private final DataSource dataSource;

    private long start = Instant.now().toEpochMilli();
    private long end = start - GRANULARITY.getMilliseconds();

    /**
     * Constructor.
     *
     * @param productId coinbase product ID
     * @param tokenBucket token bucket to manage concurrent REST API calls
     * @param service REST API endpoint caller
     * @param dataSource JDBC data source
     */
    public CoinbaseCandlesTask(String productId,
                               TokenBucket tokenBucket,
                               CandlesService service,
                               DataSource dataSource) {

        this.id = productId;
        this.tokenBucket = tokenBucket;
        this.service = service;
        this.dataSource = dataSource;
        checkTable();
    }

    /**
     * Business logic executor.
     */
    @Override
    public void run() {
        tokenBucket.acquire(id).ifPresentOrElse(
                this::callRestAndPersistResult,
                () -> log.trace("{} task has been skipped", id)
        );
    }

    private void callRestAndPersistResult(String token) {
        try {
            long now = Instant.now().toEpochMilli();
            start = (now - end < GRANULARITY.getMilliseconds()) ? now - GRANULARITY.getMilliseconds() : end;
            end = now;
            List<Candle> candles = service.getCandles(
                    id,
                    Instant.ofEpochMilli(start),
                    Instant.ofEpochMilli(end),
                    Granularity.ONE_MINUTE);
            persist(candles);
        } finally {
            tokenBucket.release(token);
        }
    }

    private void persist(List<Candle> candles) {
        try (Connection connection = dataSource.getConnection()) {
            candles.forEach(candle -> {
                log.info("persisting {}...", candle.toString());
                String sql = String.format("INSERT INTO %s VALUES(?, ?, ?, ?, ?, ?)", getTableName());

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setTimestamp(1, Timestamp.from(candle.getTime()));
                    stmt.setBigDecimal(2, candle.getLowestPrice());
                    stmt.setBigDecimal(3, candle.getHighestPrice());
                    stmt.setBigDecimal(4, candle.getOpeningPrice());
                    stmt.setBigDecimal(5, candle.getClosingPrice());
                    stmt.setBigDecimal(6, candle.getVolume());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    if ("23505".equals(e.getSQLState())) {
                        update(candle);
                    } else {
                        log.error("An error appeared while inserting to the database.", e);
                    }
                }
            });

        } catch (SQLException e) {
            log.error("An error appeared while getting the database connection.", e);
        }
    }

    private void update(Candle candle) {
        String sql = String.format(
                "UPDATE %s "
                + "SET lowest_price = ?, highest_price = ?, opening_price = ?, closing_price = ?, volume = ? "
                + "WHERE trade_date = ?",
                getTableName());

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setBigDecimal(1, candle.getLowestPrice());
            stmt.setBigDecimal(2, candle.getHighestPrice());
            stmt.setBigDecimal(3, candle.getOpeningPrice());
            stmt.setBigDecimal(4, candle.getClosingPrice());
            stmt.setBigDecimal(5, candle.getVolume());
            stmt.setTimestamp(6, Timestamp.from(candle.getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An error appeared while updating a record in the database.", e);
        }
    }

    private String getTableName() {
        return "coinbase_" + id.toLowerCase().replace("-", "_");
    }

    private void checkTable() {
        String sql = String.format("SELECT * FROM %s", getTableName());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.executeQuery();
        } catch (SQLException e) {
            if ("42P01".equals(e.getSQLState())) {
                log.debug("creating database table for {}...", id);
                createTable();
            } else {
                log.error("An unknown error appeared while initializing the database table for {}.", id, e);
            }
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE %s ("
                + "trade_date TIMESTAMP PRIMARY KEY,"
                + "lowest_price DECIMAL(11, 6) NOT NULL,"
                + "highest_price DECIMAL(11, 6) NOT NULL,"
                + "opening_price DECIMAL(11, 6) NOT NULL,"
                + "closing_price DECIMAL(11, 6) NOT NULL,"
                + "volume DECIMAL(14,8) NOT NULL"
                + ")";
        sql = String.format(sql, getTableName());

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute(sql);
        } catch (SQLException e) {
            if ("42P01".equals(e.getSQLState())) {
                createTable();
            } else {
                log.error("An unknown error appeared while initializing the database table for {}.", id, e);
            }
        }
    }
}
