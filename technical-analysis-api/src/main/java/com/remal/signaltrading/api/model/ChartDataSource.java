package com.remal.signaltrading.api.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * This POJO holds information for the charts.
 * Its data will be exported to CSV file.
 *
 * @author arnold.somogyi@gmail.com
 */
@Builder
@Getter
public class ChartDataSource {

    private String title;

    @Singular("dataSeries")
    private List<DataSeries> dataSeries;

    /**
     * Inner class for chart data points.
     */
    @Builder
    @Getter
    public static class DataSeries {
        private String label;
        private Instant startOfPeriod;
        private BigDecimal price;
        private BigDecimal volume;
    }
}
