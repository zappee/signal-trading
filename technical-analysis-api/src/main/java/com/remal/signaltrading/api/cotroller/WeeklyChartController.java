package com.remal.signaltrading.api.cotroller;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
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
public class WeeklyChartController extends ChartController {

    /**
     * Initialize the data source.
     *
     * @param dataSource data source
     */
    public WeeklyChartController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Weekly average price with 7 timelines for each day of the week.
     *
     * @param ticker product identifier number
     * @param day any day in the week
     * @param scale scale in seconds
     * @return data what can be used to generate chart
     */
    @RequestMapping("/weekly")
    public ResponseEntity<Resource> weekly(@RequestParam String ticker,
                                           @RequestParam String day,
                                           @RequestParam long scale) {

        log.info("REST request to weekly endpoint, ticker: {}, day: {}, scale: {}", ticker, day, scale);

        Instant dayInWeek = InstantConverter.fromDateString(day);
        if (RequestValidator.validateWeeks(scale)) {
            List<SqlParam> sqlParams = generateSqlParams(dayInWeek, scale);
            return generateResponse(ticker, sqlParams);
        } else {
            Resource resource = new ByteArrayResource(RequestValidator.getHelpMessage().getBytes());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resource);
        }
    }

    private List<SqlParam> generateSqlParams(Instant dayInWeek, long scale) {
        List<SqlParam> sqlParams = new ArrayList<>();
        LocalDate firstDayOfWeek = LocalDateTime.ofInstant(dayInWeek, ZoneOffset.UTC).toLocalDate();

        // days of the week
        LongStream.rangeClosed(1, 7).forEach(dayOfWeek -> {
            Instant startOfPeriod = firstDayOfWeek
                    .with(ChronoField.DAY_OF_WEEK, dayOfWeek)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();
            Instant startOfNexDay = startOfPeriod.plus(1, ChronoUnit.DAYS);

            // within the day
            while (startOfPeriod.isBefore(startOfNexDay)) {
                Instant endOfPeriod = startOfPeriod.plus(scale, ChronoUnit.SECONDS);
                sqlParams.add(SqlParam.builder()
                        .startOfPeriod(startOfPeriod)
                        .endOfPeriod(endOfPeriod)
                        .build());

                startOfPeriod = endOfPeriod;
            }
        });

        log.debug("{} sql queries will be executed", sqlParams.size());
        return sqlParams;
    }

    protected String chartToCvs(ChartDataSource chart) {
        List<ChartDataSource.DataSeries> monday = chart.getDataSeries()
                .stream()
                .filter(dataSeries -> dataSeries.getStartOfPeriod()
                        .atZone(ZoneOffset.UTC)
                        .getDayOfWeek() == DayOfWeek.MONDAY).collect(Collectors.toList());

        List<ChartDataSource.DataSeries> tuesday = chart.getDataSeries()
                .stream()
                .filter(dataSeries -> dataSeries.getStartOfPeriod()
                        .atZone(ZoneOffset.UTC)
                        .getDayOfWeek() == DayOfWeek.TUESDAY).collect(Collectors.toList());

        List<ChartDataSource.DataSeries> wednesday = chart.getDataSeries()
                .stream()
                .filter(dataSeries -> dataSeries.getStartOfPeriod()
                        .atZone(ZoneOffset.UTC)
                        .getDayOfWeek() == DayOfWeek.WEDNESDAY).collect(Collectors.toList());

        List<ChartDataSource.DataSeries> thursday = chart.getDataSeries()
                .stream()
                .filter(dataSeries -> dataSeries.getStartOfPeriod()
                        .atZone(ZoneOffset.UTC)
                        .getDayOfWeek() == DayOfWeek.THURSDAY).collect(Collectors.toList());

        List<ChartDataSource.DataSeries> friday = chart.getDataSeries()
                .stream()
                .filter(dataSeries -> dataSeries.getStartOfPeriod()
                        .atZone(ZoneOffset.UTC)
                        .getDayOfWeek() == DayOfWeek.FRIDAY).collect(Collectors.toList());

        List<ChartDataSource.DataSeries> saturday = chart.getDataSeries()
                .stream()
                .filter(dataSeries -> dataSeries.getStartOfPeriod()
                        .atZone(ZoneOffset.UTC)
                        .getDayOfWeek() == DayOfWeek.SATURDAY).collect(Collectors.toList());

        List<ChartDataSource.DataSeries> sunday = chart.getDataSeries()
                .stream()
                .filter(dataSeries -> dataSeries.getStartOfPeriod()
                        .atZone(ZoneOffset.UTC)
                        .getDayOfWeek() == DayOfWeek.SUNDAY).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder()
                .append(chart.getTitle())

                // title
                .append(" (")
                .append(InstantConverter.toDate(monday.get(0).getStartOfPeriod()))
                .append(" - ")
                .append(InstantConverter.toDate(sunday.get(0).getStartOfPeriod()))
                .append(")").append(LINE_SEPARATOR)

                // header
                .append("time-frame").append(DATA_SEPARATOR)
                .append(DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH)).append(DATA_SEPARATOR)
                .append(DayOfWeek.TUESDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH)).append(DATA_SEPARATOR)
                .append(DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH)).append(DATA_SEPARATOR)
                .append(DayOfWeek.THURSDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH)).append(DATA_SEPARATOR)
                .append(DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH)).append(DATA_SEPARATOR)
                .append(DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH)).append(DATA_SEPARATOR)
                .append(DayOfWeek.SUNDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH)).append(LINE_SEPARATOR);

        int lastEntry = monday.size();
        IntStream.range(0, lastEntry).forEach(index -> sb
                .append(InstantConverter.toTime(monday.get(index).getStartOfPeriod())).append(DATA_SEPARATOR)
                .append(Objects.isNull(monday.get(index).getPrice()) ? "0" : monday.get(index).getPrice())
                .append(DATA_SEPARATOR)
                .append(Objects.isNull(tuesday.get(index).getPrice()) ? "0" : tuesday.get(index).getPrice())
                .append(DATA_SEPARATOR)
                .append(Objects.isNull(wednesday.get(index).getPrice()) ? "0" : wednesday.get(index).getPrice())
                .append(DATA_SEPARATOR)
                .append(Objects.isNull(thursday.get(index).getPrice()) ? "0" : thursday.get(index).getPrice())
                .append(DATA_SEPARATOR)
                .append(Objects.isNull(friday.get(index).getPrice()) ? "0" : friday.get(index).getPrice())
                .append(DATA_SEPARATOR)
                .append(Objects.isNull(saturday.get(index).getPrice()) ? "0" : saturday.get(index).getPrice())
                .append(DATA_SEPARATOR)
                .append(Objects.isNull(sunday.get(index).getPrice()) ? "0" : sunday.get(index).getPrice())
                .append(LINE_SEPARATOR));
        return sb.toString();
    }
}
