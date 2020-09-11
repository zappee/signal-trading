package com.remal.signaltrading.api.model;

import java.math.BigDecimal;
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
public class RadarChart {

    private String title;

    @Singular
    private List<DataPoint> dataPoints;

    /**
     * Inner class for chart data points.
     */
    @Builder
    @Getter
    public static class DataPoint {
        private String label;
        private BigDecimal price;
    }
}
