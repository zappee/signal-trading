package com.remal.signaltrading.tradehistory.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private long end = start - GRANULARITY.getValueInMilli();

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
    }

    /**
     * Business logic executor.
     */
    @Override
    public void run() {
        tokenBucket.consume().ifPresentOrElse(
            token -> callRestAndPersistResult(token),
            () -> log.trace("{} task has been skipped", id)
        );
    }

    private void callRestAndPersistResult(String token) {
        try {
            long now = Instant.now().toEpochMilli();
            start = (now - end < GRANULARITY.getValueInMilli()) ? now - GRANULARITY.getValueInMilli() : end;
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
                String sql = String.format(
                        "INSERT INTO coinbase_%s VALUES(?, ?, ?, ?, ?, ?)",
                        id.toLowerCase().replace("-", "_"));

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
                "UPDATE coinbase_%s "
                + "SET lowest_price = ?, highest_price = ?, opening_price = ?, closing_price = ?, volume = ? "
                + "WHERE trade_date = ?",
                id.toLowerCase().replace("-", "_"));

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
}
