package com.remal.signaltrading.api.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Interval definitions for the charts.
 *
 * @author arnold.somogyi@gmail.com
 */
@Getter
@AllArgsConstructor
public enum Interval {

    UNDEFINED(0),
    ONE_MINUTE(Interval.MINUTE),
    FIVE_MINUTES(Interval.MINUTE * 5),
    FIFTEEN_MINUTES(Interval.MINUTE * 15),
    THIRTY_MINUTES(Interval.MINUTE * 30),
    ONE_HOUR(Interval.HOUR),
    TWO_HOURS(Interval.HOUR * 2),
    FOUR_HOURS(Interval.HOUR * 4),
    EIGHT_HOURS(Interval.HOUR * 8),
    ONE_DAY(Interval.DAY),
    ONE_WEEK(Interval.DAY * 7),
    ONE_MONTH(Interval.DAY * 30);

    private static final long MINUTE = 60L;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final Map<Long, Interval> intervals = new HashMap<>();

    static {
        for (Interval interval : Interval.values()) {
            intervals.put(interval.getSeconds(), interval);
        }
    }

    private final long seconds;

    /**
     * Constructor.
     *
     * @param seconds enum representation in seconds
     * @return the enum
     */
    public static Interval valueOf(long seconds) {
        Interval interval = intervals.get(seconds);
        if (Objects.isNull(interval)) {
            interval = Interval.UNDEFINED;
        }
        return interval;
    }

    @Override
    public String toString() {
        return name() + " (" + getSeconds() + ")";
    }
}
