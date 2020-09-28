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
    ONE_MONTH(Interval.DAY * 30),
    ONE_YEAR(Interval.DAY * 366);

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

    /**
     * Converts the given value into the nearest interval.
     *
     * @param seconds the value in seconds
     * @return the nearest interval
     */
    public static Interval getNearestInSeconds(long seconds) {
        if (seconds <= ONE_MINUTE.getSeconds()) {
            return ONE_MINUTE;

        } else if (seconds <= FIVE_MINUTES.getSeconds()) {
            return FIVE_MINUTES;

        } else if (seconds <= FIFTEEN_MINUTES.getSeconds()) {
            return FIFTEEN_MINUTES;

        } else if (seconds <= THIRTY_MINUTES.getSeconds()) {
            return THIRTY_MINUTES;

        } else if (seconds <= ONE_HOUR.getSeconds()) {
            return ONE_HOUR;

        } else if (seconds <= TWO_HOURS.getSeconds()) {
            return TWO_HOURS;

        } else if (seconds <= FOUR_HOURS.getSeconds()) {
            return FOUR_HOURS;

        } else if (seconds <= EIGHT_HOURS.getSeconds()) {
            return EIGHT_HOURS;

        } else if (seconds <= ONE_DAY.getSeconds()) {
            return ONE_DAY;

        } else if (seconds <= ONE_WEEK.getSeconds()) {
            return ONE_WEEK;

        } else if (seconds <= ONE_MONTH.getSeconds()) {
            return ONE_MONTH;

        } else if (seconds <= ONE_YEAR.getSeconds()) {
            return ONE_YEAR;
        } else {
            return UNDEFINED;
        }
    }

    /**
     * Converts the given value into the nearest interval.
     *
     * @param millis the value in milliseconds
     * @return the nearest interval
     */
    public static Interval getNearestInMillis(long millis) {
        return getNearestInSeconds(millis / 1000);
    }

    @Override
    public String toString() {
        return name() + " (" + getSeconds() + ")";
    }
}
