package com.remal.signaltrading.api.converter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss").withZone(ZoneId.of("UTC"));
        return formatter.format(instant);
    }

    /**
     * Utility classes should not have public constructor.
     */
    private InstantConverter() {
    }
}
