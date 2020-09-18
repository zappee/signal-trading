package com.remal.signaltrading.api.cotroller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;

import com.remal.signaltrading.api.converter.InstantConverter;
import com.remal.signaltrading.api.model.ChartDataSource;
import com.remal.signaltrading.api.model.SqlParam;
import com.remal.signaltrading.api.validator.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
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
public class SimpleChartController extends ChartController {

    /**
     * Initialize the data source.
     *
     * @param dataSource data source
     */
    public SimpleChartController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Historical average price.
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
    @RequestMapping("/fix-interval")
    public ResponseEntity<Resource> fixInterval(@RequestParam String ticker,
                                                @RequestParam long interval,
                                                @RequestParam long scale) {

        log.info("REST request to fix-interval endpoint, ticker: {}, interval: {}, scale: {}", ticker, interval, scale);

        if (RequestValidator.validateInterval(interval, scale)) {
            List<SqlParam> sqlParams = generateSqlParams(interval, scale);
            return generateResponse(ticker, sqlParams);
        } else {
            Resource resource = new ByteArrayResource(RequestValidator.getHelpMessage().getBytes());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resource);
        }
    }

    /**
     * Historical average price.
     *
     * @param ticker product identifier number
     * @param start start of the period
     * @param end end of the period
     * @param scale scale in seconds
     * @return data what can be used to generate chart
     */
    @RequestMapping("/period")
    public ResponseEntity<Resource> period(@RequestParam String ticker,
                                           @RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam long scale) {

        log.info("REST request to period endpoint, ticker: {}, period start: {}, period end: {}, scale: {}",
                ticker, start, end, scale);

        Instant startInstant = InstantConverter.fromTimestampString(start);
        Instant endInstant = InstantConverter.fromTimestampString(end);

        if (RequestValidator.validatePeriod(startInstant, endInstant, scale)) {
            List<SqlParam> sqlParams = generateSqlParams(startInstant, endInstant, scale);
            return generateResponse(ticker, sqlParams);
        } else {
            Resource resource = new ByteArrayResource(RequestValidator.getHelpMessage().getBytes());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resource);
        }
    }

    protected String chartToCvs(ChartDataSource chart) {
        StringBuilder sb = new StringBuilder()
                .append(chart.getTitle()).append(LINE_SEPARATOR)
                .append("date").append(DATA_SEPARATOR)
                .append("price").append(DATA_SEPARATOR)
                .append("volume").append(LINE_SEPARATOR);

        chart.getDataSeries().forEach(
            dataPoint -> sb
                    .append(dataPoint.getLabel())
                    .append(DATA_SEPARATOR)
                    .append(Objects.isNull(dataPoint.getPrice()) ? "0" : dataPoint.getPrice()).append(DATA_SEPARATOR)
                    .append(Objects.isNull(dataPoint.getVolume()) ? "0" : dataPoint.getVolume())
                    .append(LINE_SEPARATOR)
        );
        return sb.toString();
    }

    private List<SqlParam> generateSqlParams(long interval, long scale) {
        List<SqlParam> sqlParams = new ArrayList<>();
        Instant now = Instant.now();
        Instant startOfPeriod = now.minus(1, ChronoUnit.SECONDS).minus(interval, ChronoUnit.SECONDS);
        Instant endOfPeriod = startOfPeriod.plus(scale, ChronoUnit.SECONDS);
        while (endOfPeriod.isBefore(now)) {
            sqlParams.add(SqlParam.builder().startOfPeriod(startOfPeriod).endOfPeriod(endOfPeriod).build());
            startOfPeriod = endOfPeriod;
            endOfPeriod = startOfPeriod.plus(scale, ChronoUnit.SECONDS);
        }

        log.debug("{} sql queries will be executed", sqlParams.size());
        return sqlParams;
    }

    private List<SqlParam> generateSqlParams(Instant startOfPeriod, Instant endOfPeriod, long scale) {
        List<SqlParam> sqlParams = new ArrayList<>();
        while (startOfPeriod.isBefore(endOfPeriod)) {
            sqlParams.add(
                    SqlParam.builder()
                            .startOfPeriod(startOfPeriod)
                            .endOfPeriod(startOfPeriod.plus(scale - 1, ChronoUnit.SECONDS))
                            .build());

            startOfPeriod = startOfPeriod.plus(scale, ChronoUnit.SECONDS);
        }

        log.debug("{} sql queries will be executed", sqlParams.size());
        return sqlParams;
    }
}
