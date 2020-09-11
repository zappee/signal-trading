package com.remal.signaltrading.api.cotroller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;

import com.remal.signaltrading.api.model.RadarChart;
import com.remal.signaltrading.api.model.SqlParam;
import com.remal.signaltrading.api.validator.RadarChartRequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API controller.
 *
 * @author arnold.somogyi@gmail.com
 */
@Slf4j
@RestController
public class RadarChartController {

    private final DataSource dataSource;

    /**
     * Initialize the data source.
     *
     * @param dataSource data source
     */
    public RadarChartController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Radar chart data provider endpoint.
     *
     * <p>Possible values for interval and scale are the followings:
     *    - 1 min, 5 minutes, 15 minutes, 30 minutes
     *    - 1 hour, 2 hours, 4 hours, 8 hours
     *    - 1 day, 1 week, 1 month</p>
     *
     * <p>Example GET REST call: http://localhost:8081/api/radar?ticker=ETH-EUR&interval=300&scale=60</p>
     *
     * @param ticker product identifier number
     * @param interval interval in seconds
     * @param scale scale in seconds
     * @return data what can be used to generate chart
     */
    @RequestMapping("/radar")
    public ResponseEntity<Resource> radarChart(@RequestParam String ticker,
                                               @RequestParam long interval,
                                               @RequestParam long scale) {

        log.info("serving a request, ticker: {}, interval: {}, scale: {}", ticker, interval, scale);

        if (RadarChartRequestValidator.validateRequest(interval, scale)) {
            List<SqlParam> sqlParams = generateSqlParams(interval, scale);
            RadarChart radarChart = executeQueries(ticker, sqlParams);
            String csv = radarChartToCvs(radarChart);
            HttpHeaders headers = getHttpHeaders(ticker);
            ByteArrayResource resource = new ByteArrayResource(csv.getBytes());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            Resource resource = new ByteArrayResource(RadarChartRequestValidator.getHelpMessage().getBytes());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resource);
        }
    }

    private HttpHeaders getHttpHeaders(String ticker) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss").withZone(ZoneId.of("UTC"));
        String filename = ticker + "_" + formatter.format(Instant.now()) + ".csv";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return headers;
    }

    private String radarChartToCvs(RadarChart radarChart) {
        String lineSeparator = "\r\n";
        String dataSeparator = ",";

        StringBuilder sb = new StringBuilder();
        sb.append(radarChart.getTitle()).append(lineSeparator);
        radarChart.getDataPoints().forEach(
            dataPoint -> sb
                    .append(dataPoint.getLabel())
                    .append(dataSeparator)
                    .append(Objects.isNull(dataPoint.getPrice()) ? "" : dataPoint.getPrice())
                    .append(lineSeparator)
        );
        return sb.toString();
    }

    private RadarChart executeQueries(String ticker, List<SqlParam> sqlParams) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
        RadarChart.RadarChartBuilder radarChartBuilder = RadarChart.builder().title(ticker);
        String sql = String.format(
                "SELECT AVG(closing_price) FROM %s WHERE trade_date >= ? AND trade_date < ?",
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

                    radarChartBuilder.dataPoint(
                            RadarChart.DataPoint.builder()
                                    .label(formatter.format(sqlParam.getStartOfPeriod()))
                                    .price(price)
                                    .build());
                }
            } catch (SQLException e) {
                log.error("An error appeared while updating a record in the database.", e);
            }
        });

        return radarChartBuilder.build();
    }

    private String getTableName(String ticker) {
        return "coinbase_" + ticker.toLowerCase().replace("-", "_");
    }

    private List<SqlParam> generateSqlParams(long interval, long scale) {
        List<SqlParam> sqlParams = new ArrayList<>();
        Instant endOfPeriod = Instant.now();
        Instant startOfPeriod = endOfPeriod.minus(interval, ChronoUnit.SECONDS);
        while (startOfPeriod.isBefore(endOfPeriod)) {
            sqlParams.add(SqlParam.builder().startOfPeriod(startOfPeriod).endOfPeriod(endOfPeriod).build());
            startOfPeriod = startOfPeriod.plus(scale, ChronoUnit.SECONDS);
        }

        log.debug("{} sql queries will be executed", sqlParams.size());
        return sqlParams;
    }
}
