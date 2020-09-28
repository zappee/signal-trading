package com.remal.signaltrading.api.converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Java Instant to String converter.
 *
 * @author arnold.somogyi@gmail.com
 */
public class InstantConverter {

    /**
     * Converts Instant to String with date and time.
     * Used pattern: yyyy-MM-dd HH:mm:ss
     *
     * @param instant instant instance
     * @return string representation of the Instant
     */
    public static String toHumanReadableString(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);
        return formatter.format(instant);
    }

    /**
     * Converts Instant to String with date and time without space in it. It can be use as a filename.
     * Used pattern: yyyy-MM-dd_HH.mm.ss
     *
     * @param instant instant instance
     * @return string representation of the Instant
     */
    public static String toFilename(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss").withZone(ZoneOffset.UTC);
        return formatter.format(instant);
    }

    /**
     * Converts timestamp string to java.time.Instant.
     *
     * @param timestampString timestamp as a string in format of yyyy-MM-ddTHH:mm:ss
     * @return the java.time.Instant represents the input timestamp
     */
    public static Instant fromTimestampString(String timestampString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        TemporalAccessor temporalAccessor = formatter.parse(timestampString);
        LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneOffset.UTC);
        return Instant.from(zonedDateTime);
    }

    /**
     * Converts date string to java.time.Instant.
     *
     * @param dateString date as a string in  format of yyyy-MM-dd
     * @return the java.time.Instant represents the input timestamp
     */
    public static Instant fromDateString(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        TemporalAccessor temporalAccessor = formatter.parse(dateString);
        LocalDate localDate = LocalDate.from(temporalAccessor);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDate, LocalTime.MIN, ZoneOffset.UTC);
        return Instant.from(zonedDateTime);
    }

    /**
     * Instant to date string.
     *
     * @param instant the instant
     * @return the date as a string
     */
    public static String toDate(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return formatter.format(datetime);
    }

    /**
     * Instant to time string.
     *
     * @param instant the instant
     * @return the time as a string
     */
    public static String toTime(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return formatter.format(datetime);
    }

    /**
     * Utility classes should not have public constructor.
     */
    private InstantConverter() {
    }
}
