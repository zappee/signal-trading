package com.remal.signaltrading.api.validator;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumSet;

import com.remal.signaltrading.api.converter.InstantConverter;
import com.remal.signaltrading.api.model.Interval;

/**
 * API request validator. It validates the incoming interval and scale values
 * whether they are insync.
 *
 * @author arnold.somogyi@gmail.com
 */
public class RequestValidator {

    /**
     * Validates the value of the incoming interval and scale fields.
     *
     * @param interval interval in seconds
     * @param scale scale in seconds
     * @return true if the given values are in sync
     */
    public static boolean validateInterval(long interval, long scale) {
        Interval intervalEnum = Interval.valueOf(interval);
        Interval scaleEnum = Interval.valueOf(scale);
        EnumSet<Interval> possibleScales = getPossibleScales(intervalEnum);
        return possibleScales.contains(scaleEnum) && Interval.UNDEFINED != scaleEnum;
    }

    /**
     * Validates the length of the given timeframe.
     *
     * @param periodStart start of the period
     * @param periodEnd end of the period
     * @param scale scale in seconds
     * @return true if the size of the given timeframe is less or equals with the expectation, otherwise false
     */
    public static boolean validatePeriod(Instant periodStart, Instant periodEnd, long scale) {
        long difference = Duration.between(periodStart, periodEnd).toMillis();
        Interval intervalEnum = Interval.getNearestInMillis(difference);
        Interval scaleEnum = Interval.valueOf(scale);
        EnumSet<Interval> possibleScales = getPossibleScales(intervalEnum);
        return possibleScales.contains(scaleEnum) && Interval.UNDEFINED != scaleEnum;
    }

    /**
     * Validates the given scale.
     *
     * @param scale scale in seconds
     * @return true if the scale is in the range
     */
    public static boolean validateWeeks(long scale) {
        Interval scaleEnum = Interval.valueOf(scale);
        EnumSet<Interval> possibleScales = getPossibleScales(Interval.ONE_DAY);
        return possibleScales.contains(scaleEnum) && Interval.UNDEFINED != scaleEnum;
    }

    /**
     * Help message, displayed if the request contains invalid request parameters.
     *
     * @return the HTML formatted help message
     */
    public static String getHelpMessage() {
        Interval[] supportedIntervals = {
            Interval.FIVE_MINUTES,
            Interval.FIFTEEN_MINUTES,
            Interval.THIRTY_MINUTES,
            Interval.ONE_HOUR,
            Interval.TWO_HOURS,
            Interval.FOUR_HOURS,
            Interval.EIGHT_HOURS,
            Interval.ONE_DAY,
            Interval.ONE_WEEK,
            Interval.ONE_MONTH,
            Interval.ONE_YEAR
        };

        String liBegin = "<li>";
        String liEnd = "</il>";

        StringBuilder sb = new StringBuilder()
                .append("<p style=\"color:red; font-size:25px;\">")
                .append(InstantConverter.toHumanReadableString(Instant.now()))
                .append(" (UTC) - Invalid interval or scale.")
                .append("</p>")
                .append("<p>Possible values: </p>")
                .append("<ul>");

        Arrays.stream(supportedIntervals).forEach(interval ->
                sb.append(liBegin).append(interval).append(": ").append(getPossibleScales(interval)).append(liEnd)
        );

        sb.append("</ul>");

        return sb.toString();
    }

    /**
     * Scale range definition.
     *
     * @param interval value of the chart interval
     * @return possible scales belong to the given interval
     */
    private static EnumSet<Interval> getPossibleScales(Interval interval) {
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
                    Interval.ONE_MINUTE,
                    Interval.FIVE_MINUTES,
                    Interval.FIFTEEN_MINUTES,
                    Interval.THIRTY_MINUTES,
                    Interval.ONE_HOUR,
                    Interval.TWO_HOURS,
                    Interval.FOUR_HOURS,
                    Interval.EIGHT_HOURS);

        } else if (interval == Interval.ONE_WEEK) {
            possibleScales = EnumSet.of(
                    Interval.FIFTEEN_MINUTES,
                    Interval.THIRTY_MINUTES,
                    Interval.ONE_HOUR,
                    Interval.TWO_HOURS,
                    Interval.FOUR_HOURS,
                    Interval.EIGHT_HOURS,
                    Interval.ONE_DAY);

        } else if (interval == Interval.ONE_MONTH) {
            possibleScales = EnumSet.of(
                    Interval.FIFTEEN_MINUTES,
                    Interval.THIRTY_MINUTES,
                    Interval.ONE_HOUR,
                    Interval.TWO_HOURS,
                    Interval.FOUR_HOURS,
                    Interval.EIGHT_HOURS,
                    Interval.ONE_DAY);

        } else if (interval == Interval.ONE_YEAR) {
            possibleScales = EnumSet.of(
                    Interval.ONE_DAY,
                    Interval.ONE_WEEK,
                    Interval.ONE_MONTH);

        } else {
            possibleScales = EnumSet.noneOf(Interval.class);
        }
        return possibleScales;
    }

    /**
     * Utility classes should not have public constructor.
     */
    private RequestValidator() {
    }
}
