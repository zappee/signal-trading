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

    private final long seconds;

    static {
        for (Interval interval : Interval.values()) {
            intervals.put(interval.getSeconds(), interval);
        }
    }

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
     * Scale range definition.
     *
     * @param interval value of the chart interval
     * @return possible scales belong to the given interval
     */
    public static EnumSet<Interval> getPossibleScales(Interval interval) {
        EnumSet<Interval> possibleScales;

        if (interval == Interval.FIVE_MINUTES) {
            possibleScales = EnumSet.of(
                    Interval.ONE_MINUTE);

        } else if (interval == Interval.FIFTEEN_MINUTES) {
            possibleScales = EnumSet.of(
                    Interval.ONE_MINUTE,
                    Interval.FIVE_MINUTES);

        } else if (interval == Interval.THIRTY_MINUTES) {
            possibleScales = EnumSet.of(
                    Interval.ONE_MINUTE,
                    Interval.FIVE_MINUTES,
                    Interval.FIFTEEN_MINUTES);

        } else if (interval == Interval.ONE_HOUR) {
            possibleScales = EnumSet.of(
                    Interval.ONE_MINUTE,
                    Interval.FIVE_MINUTES,
                    Interval.FIFTEEN_MINUTES,
                    Interval.THIRTY_MINUTES);

        } else if (interval == Interval.TWO_HOURS) {
            possibleScales = EnumSet.of(
                    Interval.ONE_MINUTE,
                    Interval.FIVE_MINUTES,
                    Interval.FIFTEEN_MINUTES,
                    Interval.THIRTY_MINUTES,
                    Interval.ONE_HOUR);

        } else if (interval == Interval.FOUR_HOURS) {
            possibleScales = EnumSet.of(
                    Interval.ONE_MINUTE,
                    Interval.FIVE_MINUTES,
                    Interval.FIFTEEN_MINUTES,
                    Interval.THIRTY_MINUTES,
                    Interval.ONE_HOUR,
                    Interval.TWO_HOURS);

        } else if (interval == Interval.EIGHT_HOURS) {
            possibleScales = EnumSet.of(
                    Interval.FIVE_MINUTES,
                    Interval.FIFTEEN_MINUTES,
                    Interval.THIRTY_MINUTES,
                    Interval.ONE_HOUR,
                    Interval.TWO_HOURS,
                    Interval.FOUR_HOURS);

        } else if (interval == Interval.ONE_DAY) {
            possibleScales = EnumSet.of(
                    Interval.FIVE_MINUTES,
                    Interval.FIFTEEN_MINUTES,
                    Interval.THIRTY_MINUTES,
                    Interval.ONE_HOUR,
                    Interval.TWO_HOURS,
                    Interval.FOUR_HOURS,
                    Interval.EIGHT_HOURS);

        } else if (interval == Interval.ONE_WEEK) {
            possibleScales = EnumSet.of(
                    Interval.ONE_HOUR,
                    Interval.TWO_HOURS,
                    Interval.FOUR_HOURS,
                    Interval.EIGHT_HOURS,
                    Interval.ONE_DAY);

        } else {
            possibleScales = EnumSet.noneOf(Interval.class);
        }

        return possibleScales;
    }

    @Override
    public String toString() {
        return name() + " (" + getSeconds() + ")";
    }
}
