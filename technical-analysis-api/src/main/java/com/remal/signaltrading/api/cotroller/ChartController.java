package com.remal.signaltrading.api.cotroller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import javax.sql.DataSource;

import com.remal.signaltrading.api.converter.InstantConverter;
import com.remal.signaltrading.api.model.ChartDataSource;
import com.remal.signaltrading.api.model.SqlParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Abstract class for common functions.
 *
 * @author arnold.somogyi@gmail.com
 */
@Slf4j
public abstract class ChartController {

    protected static final String LINE_SEPARATOR = "\r\n";
    protected static final String DATA_SEPARATOR = ",";

    protected DataSource dataSource;

    protected ChartDataSource executeQueries(String ticker, List<SqlParam> sqlParams) {
        ChartDataSource.ChartDataSourceBuilder dataSourceBuilder = ChartDataSource.builder().title(ticker);
        String sql = String.format(
                "SELECT AVG(closing_price), SUM(volume) FROM %s WHERE trade_date >= ? AND trade_date < ?",
                getTableName(ticker));

        log.debug("executing sql queries...");
        sqlParams.forEach(sqlParam -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setTimestamp(1, Timestamp.from(sqlParam.getStartOfPeriod()));
                stmt.setTimestamp(2, Timestamp.from(sqlParam.getEndOfPeriod()));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    BigDecimal price = rs.getBigDecimal(1);
                    BigDecimal volume = rs.getBigDecimal(2);

                    dataSourceBuilder.dataSeries(
                            ChartDataSource.DataSeries.builder()
                                    .label(InstantConverter.toHumanReadableString(sqlParam.getStartOfPeriod()))
                                    .startOfPeriod(sqlParam.getStartOfPeriod())
                                    .price(price)
                                    .volume(volume)
                                    .build());
                }
            } catch (SQLException e) {
                log.error("An error appeared while updating a record in the database.", e);
            }
        });

        return dataSourceBuilder.build();
    }

    protected ResponseEntity<Resource> generateResponse(String ticker, List<SqlParam> sqlParams) {
        ChartDataSource chart = executeQueries(ticker, sqlParams);
        String csv = chartToCvs(chart);
        HttpHeaders headers = getHttpHeaders(ticker);
        ByteArrayResource resource = new ByteArrayResource(csv.getBytes());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    protected abstract String chartToCvs(ChartDataSource chart);

    protected HttpHeaders getHttpHeaders(String ticker) {
        String filename = ticker + "_" + InstantConverter.toFilename(Instant.now()) + ".csv";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return headers;
    }

    protected String getTableName(String ticker) {
        return "coinbase_" + ticker.toLowerCase().replace("-", "_");
    }
}
