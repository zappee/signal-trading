package com.remal.signaltrading.api.cotroller;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Interval definitions for charts.
 *
 * @author arnold.somogyi@gmail.com
 */
@Getter
@AllArgsConstructor
public enum Interval {

    UNDEFINED(0),
    MINUTE_1(Interval.ONE_MINUTE),
    MINUTES_5(Interval.ONE_MINUTE * 5),
    MINUTES_15(Interval.ONE_MINUTE * 15),
    MINUTES_30(Interval.ONE_MINUTE * 30),
    HOUR_1(Interval.ONE_HOUR),
    HOUR_2(Interval.ONE_HOUR * 2),
    HOUR_4(Interval.ONE_HOUR * 4),
    HOUR_8(Interval.ONE_HOUR * 8),
    DAY_1(Interval.ONE_DAY),
    WEEK_1(Interval.ONE_DAY * 7),
    MONTH_1(Interval.ONE_DAY * 30);

    private static final long ONE_MINUTE = 60L * 1000;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static final Map<Long, Interval> intervals = new HashMap<>();

    private final long intervalInMilliseconds;

    static {
        for (Interval interval : Interval.values()) {
            intervals.put(interval.getIntervalInMilliseconds(), interval);
        }
    }

    /**
     * Constructor.
     *
     * @param intervalInMilliseconds enum representation in milliseconds
     * @return the enum
     */
    public static Interval valueOf(long intervalInMilliseconds) {
        Interval interval = intervals.get(intervalInMilliseconds);
        if (Objects.isNull(interval)) {
            interval = Interval.UNDEFINED;
        }
        return interval;
    }

    /**
     * Scale range definition.
     *
     * @param interval value of the chart interval
     * @return possible scales belong to the given interval
     */
    public static EnumSet<Interval> getPossibleScales(Interval interval) {
        EnumSet<Interval> possibleScales;

        if (interval == Interval.MINUTES_5) {
            possibleScales = EnumSet.of(
                    Interval.MINUTE_1);

        } else if (interval == Interval.MINUTES_15) {
            possibleScales = EnumSet.of(
                    Interval.MINUTE_1,
                    Interval.MINUTES_5);

        } else if (interval == Interval.MINUTES_30) {
            possibleScales = EnumSet.of(
                    Interval.MINUTE_1,
                    Interval.MINUTES_5,
                    Interval.MINUTES_15);

        } else if (interval == Interval.HOUR_1) {
            possibleScales = EnumSet.of(
                    Interval.MINUTE_1,
                    Interval.MINUTES_5,
                    Interval.MINUTES_15,
                    Interval.MINUTES_30);

        } else {
            possibleScales = EnumSet.noneOf(Interval.class);
        }

        return possibleScales;
    }
}
